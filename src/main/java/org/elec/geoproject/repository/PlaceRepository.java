package org.elec.geoproject.repository;

import java.util.List;
import java.util.Optional;
import org.elec.geoproject.entity.Place;
import org.elec.geoproject.repository.projection.ClusterProjection;
import org.elec.geoproject.repository.projection.NearbyProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

  Page<Place> findAllByDeletedAtIsNull(Pageable pageable);

  Optional<Place> findByIdAndDeletedAtIsNull(Long id);

  boolean existsByOsmId(Long osmId);

  List<Place> findAllByAddressIsNullAndDeletedAtIsNull();

  long countByDeletedAtIsNull();

  long countByAddressIsNotNullAndDeletedAtIsNull();

  @Query(value = """
      SELECT type, COUNT(*) as count
      FROM places
      WHERE deleted_at IS NULL
      GROUP BY type
      """, nativeQuery = true)
  List<Object[]> countByType();

  @Query(value = """
      SELECT
          p.id,
          p.name,
          p.type,
          p.address,
          ST_Y(p.location)                                                  AS lat,
          ST_X(p.location)                                                  AS lon,
          ST_Distance(
              p.location::geography,
              ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
          )                                                                 AS distance_meters
      FROM places p
      WHERE p.deleted_at IS NULL
        AND ST_DWithin(
              p.location::geography,
              ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
              :radiusMeters
            )
        AND (:type IS NULL OR p.type = :type)
      ORDER BY distance_meters
      LIMIT :limitCount
      """, nativeQuery = true)
  List<NearbyProjection> findNearby(
      @Param("lat") double lat,
      @Param("lon") double lon,
      @Param("radiusMeters") double radiusMeters,
      @Param("type") String type,
      @Param("limitCount") int limitCount
  );

  @Query(value = """
      SELECT
          type,
          COUNT(*)                                 AS place_count,
          ST_Y(ST_Centroid(ST_Collect(location)))  AS centroid_lat,
          ST_X(ST_Centroid(ST_Collect(location)))  AS centroid_lon
      FROM places
      WHERE deleted_at IS NULL
      GROUP BY type
      ORDER BY place_count DESC
      """, nativeQuery = true)
  List<ClusterProjection> findClusters();
}
