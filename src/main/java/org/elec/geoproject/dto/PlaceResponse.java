package org.elec.geoproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Place details")
public class PlaceResponse {

  private Long id;
  private String name;
  private String type;
  private String description;

  @Schema(description = "Latitude")
  private Double lat;

  @Schema(description = "Longitude")
  private Double lon;

  private Long osmId;
  private String address;

  @Schema(description = "Detailed address components from Nominatim")
  private Map<String, Object> addressDetails;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
