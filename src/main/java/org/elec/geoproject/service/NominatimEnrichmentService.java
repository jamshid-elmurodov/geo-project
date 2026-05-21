package org.elec.geoproject.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elec.geoproject.client.NominatimClient;
import org.elec.geoproject.client.NominatimResponse;
import org.elec.geoproject.entity.Place;
import org.elec.geoproject.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NominatimEnrichmentService {

  private final NominatimClient nominatimClient;
  private final PlaceRepository placeRepository;

  @Value("${geo.nominatim.rate-limit-ms:1000}")
  private long rateLimitMs;

  @Async
  @Transactional
  public void enrichAll() {
    List<Place> unenriched = placeRepository.findAllByAddressIsNullAndDeletedAtIsNull();
    log.info("Starting Nominatim enrichment for {} places", unenriched.size());

    int enriched = 0;
    for (Place place : unenriched) {
      try {
        double lon = place.getLocation().getX();
        double lat = place.getLocation().getY();

        NominatimResponse response = nominatimClient.reverseGeocode(lat, lon).block();
        if (response != null && response.getDisplayName() != null) {
          place.setAddress(response.getDisplayName());
          place.setAddressDetails(response.getAddress());
          placeRepository.save(place);
          enriched++;
          log.debug("Enriched place id={} address={}", place.getId(), response.getDisplayName());
        }

        Thread.sleep(rateLimitMs);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.warn("Enrichment interrupted at place id={}", place.getId());
        break;
      } catch (Exception e) {
        log.warn("Enrichment failed for place id={}: {}", place.getId(), e.getMessage());
      }
    }

    log.info("Enrichment complete: {}/{} places enriched", enriched, unenriched.size());
    CompletableFuture.completedFuture(enriched);
  }

  public NominatimResponse enrichSingle(double lat, double lon) {
    return nominatimClient.reverseGeocode(lat, lon).block();
  }
}
