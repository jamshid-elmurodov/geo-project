# Geo Analytics Backend

OpenStreetMap shahar obyektlarini import qilish → PostgreSQL + PostGIS'da saqlash → Nominatim orqali manzil boyitish → geoanalitika API'lari.

## Stack

- **Java 21** + **Spring Boot 3.4**
- **PostgreSQL 16** + **PostGIS 3.4**
- **Liquibase** (DB migrations)
- **WebFlux WebClient** (Overpass + Nominatim)
- **Hibernate Spatial** + **JTS**
- **Swagger / OpenAPI 3**
- **Docker Compose**

---

## Ishga tushirish

### Docker bilan (tavsiya etiladi)

```bash
docker-compose up --build
```

App: http://localhost:8080  
Swagger: http://localhost:8080/swagger-ui.html

### Local (Maven)

PostgreSQL + PostGIS o'rnatilgan bo'lishi kerak. DB yarating:
```sql
CREATE DATABASE geoproject;
```

```bash
export DB_URL=jdbc:postgresql://localhost:5432/geoproject
export DB_USER=postgres
export DB_PASS=postgres
./mvnw spring-boot:run
```

---

## Environment o'zgaruvchilari

| Variable      | Default                                    | Description            |
|---------------|--------------------------------------------|------------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:5432/geoproject` | JDBC URL               |
| `DB_USER`     | `postgres`                                 | DB foydalanuvchi       |
| `DB_PASS`     | `postgres`                                 | DB parol               |
| `SERVER_PORT` | `8080`                                     | Server port            |

---

## API Endpointlar

### CRUD — Places

```bash
# Barcha joylar (paginated)
GET /places?page=0&size=20

# ID bo'yicha
GET /places/1

# Yangi joy qo'shish
POST /places
Content-Type: application/json
{
  "name": "1-maktab",
  "type": "school",
  "lat": 41.2995,
  "lon": 69.2401
}

# Yangilash (PATCH)
PATCH /places/1
{ "name": "Yangi nom" }

# O'chirish (soft delete)
DELETE /places/1
```

---

### Import — OSM dan shahar obyektlarini import qilish

```bash
# Import boshlash (async — darhol job ID qaytaradi)
curl -X POST http://localhost:8080/import \
  -H "Content-Type: application/json" \
  -d '{"city": "Toshkent", "placeType": "school"}'

# Job holati
curl http://localhost:8080/import/jobs/1

# Barcha joblar
curl http://localhost:8080/import/jobs

# Manzillari yo'q joylarni Nominatim bilan boyitish
curl -X POST http://localhost:8080/import/enrich-all
```

**Qo'llab-quvvatlanadigan placeType lar:**
`school`, `hospital`, `pharmacy`, `bank`, `restaurant`, `cafe`, `park`, `hotel`, `fuel` va boshqa OSM amenity/leisure turlari.

---

### Analytics — PostGIS geoanalitika

```bash
# Yaqin joylarni qidirish (ST_DWithin + ST_Distance)
curl "http://localhost:8080/analytics/search-nearby?lat=41.2995&lon=69.2401&radiusMeters=2000&type=school&limit=10"

# Klasterlar — type bo'yicha centroid (ST_Centroid + ST_Collect)
curl http://localhost:8080/analytics/cluster

# Statistika
curl http://localhost:8080/analytics/stats
```

**search-nearby parametrlari:**

| Param          | Tavsif                    | Default | Range         |
|----------------|---------------------------|---------|---------------|
| `lat`          | Kenglik                   | -       | -90 … 90      |
| `lon`          | Uzunlik                   | -       | -180 … 180    |
| `radiusMeters` | Qidiruv radiusi (metr)    | 1000    | 1 … 100000    |
| `type`         | Joy turi (ixtiyoriy)      | -       | -             |
| `limit`        | Maksimal natijalar soni   | 20      | 1 … 500       |

---

## Misol: To'liq workflow

```bash
# 1. Toshkentdagi maktablarni import qiling
curl -X POST http://localhost:8080/import \
  -H "Content-Type: application/json" \
  -d '{"city": "Toshkent", "placeType": "school"}'
# {"id": 1, "status": "PENDING", ...}

# 2. Job holatini kuzating
curl http://localhost:8080/import/jobs/1
# {"status": "DONE", "totalFetched": 150, "totalSaved": 148, ...}

# 3. Manzillarni boyiting (Nominatim, 1 req/sec)
curl -X POST http://localhost:8080/import/enrich-all

# 4. Maktablarni yaqin-atrofdan qidiring
curl "http://localhost:8080/analytics/search-nearby?lat=41.2995&lon=69.2401&radiusMeters=3000&type=school"

# 5. Statistika
curl http://localhost:8080/analytics/stats
```

---

## DB Schema

### places
```sql
id, name, type, description,
location GEOMETRY(Point,4326),  -- GiST indexed
osm_id BIGINT UNIQUE,
address TEXT,
address_details JSONB,
created_at, updated_at, deleted_at
```

### import_jobs
```sql
id, city, place_type, status (PENDING/RUNNING/DONE/FAILED),
total_fetched, total_saved, total_enriched,
error_message, created_at, updated_at, completed_at
```

---

## Swagger UI

Barcha endpointlarni interaktiv test qilish:  
**http://localhost:8080/swagger-ui.html**
