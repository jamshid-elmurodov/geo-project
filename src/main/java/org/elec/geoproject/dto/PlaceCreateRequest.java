package org.elec.geoproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to create a new place")
public class PlaceCreateRequest {

  @NotBlank(message = "Name is required")
  @Size(max = 500)
  @Schema(description = "Place name", example = "Mirzo Ulugbek Maktabi")
  private String name;

  @NotBlank(message = "Type is required")
  @Size(max = 100)
  @Schema(description = "Place type (e.g. school, hospital, park)", example = "school")
  private String type;

  @Schema(description = "Optional description")
  private String description;

  @NotNull(message = "Latitude is required")
  @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
  @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
  @Schema(description = "Latitude (WGS84)", example = "41.2995")
  private Double lat;

  @NotNull(message = "Longitude is required")
  @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
  @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
  @Schema(description = "Longitude (WGS84)", example = "69.2401")
  private Double lon;

  @Schema(description = "OpenStreetMap node/way ID")
  private Long osmId;

  @Schema(description = "Human-readable address")
  private String address;
}
