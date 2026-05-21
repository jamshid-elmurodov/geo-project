package org.elec.geoproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to update an existing place (all fields optional)")
public class PlaceUpdateRequest {

  @Size(max = 500)
  @Schema(description = "Place name")
  private String name;

  @Size(max = 100)
  @Schema(description = "Place type")
  private String type;

  @Schema(description = "Description")
  private String description;

  @DecimalMin(value = "-90.0")
  @DecimalMax(value = "90.0")
  @Schema(description = "Latitude")
  private Double lat;

  @DecimalMin(value = "-180.0")
  @DecimalMax(value = "180.0")
  @Schema(description = "Longitude")
  private Double lon;

  @Schema(description = "Human-readable address")
  private String address;
}
