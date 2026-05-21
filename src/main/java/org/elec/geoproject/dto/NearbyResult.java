package org.elec.geoproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "A place found within the search radius")
public class NearbyResult {

  private Long id;
  private String name;
  private String type;
  private String address;
  private Double lat;
  private Double lon;

  @Schema(description = "Distance from query point in meters")
  private Double distanceMeters;
}
