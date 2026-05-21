package org.elec.geoproject.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elec.geoproject.dto.ClusterResult;
import org.elec.geoproject.dto.NearbyResult;
import org.elec.geoproject.dto.StatsResponse;
import org.elec.geoproject.entity.ImportJob;
import org.elec.geoproject.entity.ImportJobStatus;
import org.elec.geoproject.repository.ImportJobRepository;
import org.elec.geoproject.repository.PlaceRepository;
import org.elec.geoproject.repository.projection.ClusterProjection;
import org.elec.geoproject.repository.projection.NearbyProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

  private final PlaceRepository placeRepository;
  private final ImportJobRepository importJobRepository;

  public List<NearbyResult> searchNearby(
      double lat, double lon, double radiusMeters, String type, int limit) {

    if (radiusMeters <= 0 || radiusMeters > 100_000) {
      throw new IllegalArgumentException("radiusMeters must be between 1 and 100000");
    }
    if (limit <= 0 || limit > 500) {
      throw new IllegalArgumentException("limit must be between 1 and 500");
    }

    List<NearbyProjection> rows = placeRepository.findNearby(lat, lon, radiusMeters, type, limit);

    return rows.stream()
        .map(p -> NearbyResult.builder()
            .id(p.getId())
            .name(p.getName())
            .type(p.getType())
            .address(p.getAddress())
            .lat(p.getLat())
            .lon(p.getLon())
            .distanceMeters(p.getDistanceMeters() != null
                ? Math.round(p.getDistanceMeters() * 100.0) / 100.0
                : null)
            .build())
        .collect(Collectors.toList());
  }

  public List<ClusterResult> getClusters() {
    List<ClusterProjection> rows = placeRepository.findClusters();
    return rows.stream()
        .map(c -> ClusterResult.builder()
            .type(c.getType())
            .placeCount(c.getPlaceCount())
            .centroidLat(c.getCentroidLat())
            .centroidLon(c.getCentroidLon())
            .build())
        .collect(Collectors.toList());
  }

  public StatsResponse getStats() {
    long total = placeRepository.countByDeletedAtIsNull();
    long enriched = placeRepository.countByAddressIsNotNullAndDeletedAtIsNull();
    double percent = total > 0 ? Math.round(enriched * 10000.0 / total) / 100.0 : 0.0;

    List<Object[]> typeRows = placeRepository.countByType();
    Map<String, Long> byType = typeRows.stream()
        .collect(Collectors.toMap(
            row -> (String) row[0],
            row -> ((Number) row[1]).longValue()
        ));

    var lastImportJob = importJobRepository
        .findTopByStatusOrderByCompletedAtDesc(ImportJobStatus.DONE);

    return StatsResponse.builder()
        .totalPlaces(total)
        .byType(byType)
        .enrichedCount(enriched)
        .enrichedPercent(percent)
        .lastImport(lastImportJob.map(ImportJob::getCompletedAt).orElse(null))
        .build();
  }
}
