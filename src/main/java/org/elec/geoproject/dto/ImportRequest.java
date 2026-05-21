package org.elec.geoproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Request to import OSM places for a city")
public class ImportRequest {

  @NotBlank(message = "City name is required")
  @Schema(description = "City name as it appears in OpenStreetMap", example = "Toshkent")
  private String city;

  @NotEmpty(message = "At least one place type is required")
  @Schema(
      description = "OSM amenity/leisure types",
      example = "[\"school\", \"hospital\", \"park\"]"
  )
  private List<String> amenities;
}
