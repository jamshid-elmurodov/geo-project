package org.elec.geoproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Spatial cluster of places grouped by type")
public class ClusterResult {

  @Schema(description = "Place type")
  private String type;

  @Schema(description = "Number of places in this cluster")
  private Long placeCount;

  @Schema(description = "Centroid latitude of the cluster")
  private Double centroidLat;

  @Schema(description = "Centroid longitude of the cluster")
  private Double centroidLon;
}
