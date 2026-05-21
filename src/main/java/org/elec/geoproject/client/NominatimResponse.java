package org.elec.geoproject.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimResponse {

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("address")
  private Map<String, Object> address;
}
