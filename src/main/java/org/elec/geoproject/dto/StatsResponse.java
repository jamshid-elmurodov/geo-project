package org.elec.geoproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Geo-analytics statistics")
public class StatsResponse {

  @Schema(description = "Total number of active places")
  private Long totalPlaces;

  @Schema(description = "Count per place type")
  private Map<String, Long> byType;

  @Schema(description = "Number of places with enriched address")
  private Long enrichedCount;

  @Schema(description = "Percentage of places that are address-enriched")
  private Double enrichedPercent;

  @Schema(description = "Timestamp of the last completed import job")
  private LocalDateTime lastImport;
}
