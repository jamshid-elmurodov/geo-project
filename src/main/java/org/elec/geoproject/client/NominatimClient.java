package org.elec.geoproject.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class NominatimClient {

  private final WebClient webClient;

  public NominatimClient(
      WebClient.Builder builder,
      @Value("${geo.nominatim.base-url}") String baseUrl,
      @Value("${geo.nominatim.user-agent}") String userAgent
  ) {
    this.webClient = builder
        .baseUrl(baseUrl)
        .defaultHeader("User-Agent", userAgent)
        .defaultHeader("Accept-Language", "en")
        .build();
  }

  public Mono<NominatimResponse> reverseGeocode(double lat, double lon) {
    log.debug("Nominatim reverse geocode lat={} lon={}", lat, lon);
    return webClient.get()
        .uri(u -> u.path("/reverse")
            .queryParam("lat", lat)
            .queryParam("lon", lon)
            .queryParam("format", "json")
            .queryParam("addressdetails", 1)
            .build())
        .retrieve()
        .bodyToMono(NominatimResponse.class)
        .doOnError(e -> log.warn("Nominatim error for lat={} lon={}: {}", lat, lon, e.getMessage()))
        .onErrorResume(e -> Mono.empty());
  }

  public Mono<Long> findRelationId(String city) {
    return webClient.get()
        .uri(uri -> uri.path("/search")
            .queryParam("q", city)
            .queryParam("format", "json")
            .queryParam("limit", 1)
            .build())
        .retrieve()
        .bodyToFlux(InternalNominatimResponse.class)
        .next()
        .map(resp -> {
          if (resp.osm_id == null) {
            throw new RuntimeException("No osm_id found");
          }
          return resp.osm_id;
        });
  }

  @Data
  private static class InternalNominatimResponse {

    public String osm_type;
    public Long osm_id;
  }
}
