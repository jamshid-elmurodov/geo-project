package org.elec.geoproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.elec.geoproject.dto.ClusterResult;
import org.elec.geoproject.dto.NearbyResult;
import org.elec.geoproject.dto.StatsResponse;
import org.elec.geoproject.service.AnalyticsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "PostGIS spatial analytics endpoints")
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  @GetMapping("/search-nearby")
  @Operation(summary = "Find places within a radius (PostGIS ST_DWithin + ST_Distance)")
  public List<NearbyResult> searchNearby(
      @Parameter(description = "Latitude", example = "41.2995")
      @RequestParam @DecimalMin("-90") @DecimalMax("90") double lat,

      @Parameter(description = "Longitude", example = "69.2401")
      @RequestParam @DecimalMin("-180") @DecimalMax("180") double lon,

      @Parameter(description = "Search radius in meters (max 100 km)", example = "2000")
      @RequestParam(defaultValue = "1000") @Min(1) @Max(100000) double radiusMeters,

      @Parameter(description = "Filter by place type (optional)", example = "school")
      @RequestParam(required = false) String type,

      @Parameter(description = "Max results", example = "20")
      @RequestParam(defaultValue = "20") @Min(1) @Max(500) int limit
  ) {
    return analyticsService.searchNearby(lat, lon, radiusMeters, type, limit);
  }

  @GetMapping("/cluster")
  @Operation(summary = "Spatial clusters grouped by type (ST_Centroid + ST_Collect)")
  public List<ClusterResult> getClusters() {
    return analyticsService.getClusters();
  }

  @GetMapping("/stats")
  @Operation(summary = "Overall statistics: total, by type, enrichment rate, last import")
  public StatsResponse getStats() {
    return analyticsService.getStats();
  }
}
