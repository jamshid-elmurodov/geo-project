package org.elec.geoproject.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OverpassResponse {

  private List<OverpassElement> elements;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OverpassElement {

    private String type;
    private Long id;
    private Double lat;
    private Double lon;
    private OverpassCenter center;
    private Map<String, String> tags;

    public Double effectiveLat() {
      return "node".equals(type) ? lat : (center != null ? center.getLat() : null);
    }

    public Double effectiveLon() {
      return "node".equals(type) ? lon : (center != null ? center.getLon() : null);
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OverpassCenter {

    private Double lat;
    private Double lon;
  }
}
