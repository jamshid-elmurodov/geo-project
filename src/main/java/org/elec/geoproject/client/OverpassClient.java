package org.elec.geoproject.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Component
public class OverpassClient {

  private final WebClient webClient;
  private final ObjectMapper objectMapper;
  private final NominatimClient nominatimClient;

  @Value("${geo.overpass.timeout-seconds:60}")
  private int timeoutSeconds;

  public OverpassClient(
      WebClient.Builder builder,
      ObjectMapper objectMapper,
      @Value("${geo.overpass.base-url}") String baseUrl,
      @Value("${geo.overpass.max-buffer-size-mb:16}") int maxBufferMb,
      NominatimClient nominatimClient
  ) {
    this.objectMapper = objectMapper;
    this.nominatimClient = nominatimClient;
    this.webClient = builder
        .baseUrl(baseUrl)
        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(maxBufferMb * 1024 * 1024))
        .build();
  }

  public Mono<OverpassResponse> fetchPlaces(String city, List<String> placeTypes) {
    return resolveAreaId(city)
        .flatMap(areaId -> execute(buildQuery(areaId, placeTypes)))
        .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)));
  }

  private Mono<Long> resolveAreaId(String city) {
    return nominatimClient.findRelationId(city)
        .map(id -> {
          log.info("City={} relationId={}", city, id);
          return 3600000000L + id;
        });
  }

  private String buildQuery(long areaId, List<String> placeTypes) {

    String filters = placeTypes.stream()
        .map(this::buildOsmFilter)
        .collect(Collectors.joining());

    return """
        [out:json][timeout:%d][maxsize:1073741824];
        
        area(%d)->.searchArea;
        
        (
        %s
        );
        
        out body center;
        """.formatted(timeoutSeconds, areaId, filters);
  }

  private String buildOsmFilter(String type) {
    return """
        node["amenity"="%s"](area.searchArea);
        way["amenity"="%s"](area.searchArea);
        """.formatted(type, type);
  }

  private Mono<OverpassResponse> execute(String query) {
    String body = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

    return webClient.post()
        .uri("/api/interpreter")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .map(this::parse);
  }

  private OverpassResponse parse(String json) {
    try {
      return objectMapper.readValue(json, OverpassResponse.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse Overpass response", e);
    }
  }
}