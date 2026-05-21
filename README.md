# Geo Analytics Backend

## Ishga tushirish


```bash
docker-compose up --build
```

App: http://localhost:8080  
Swagger: http://localhost:8080/swagger-ui.html


## API Endpointlar

### Import

```bash
# Import boshlash (async — darhol job ID qaytaradi)
curl -X POST http://localhost:8080/import \
  -H "Content-Type: application/json" \
  -d '{"city": "Toshkent", "amenities": ["school", "hotel", "park"]}'

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