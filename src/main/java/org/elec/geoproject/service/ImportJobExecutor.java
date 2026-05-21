package org.elec.geoproject.service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elec.geoproject.client.OverpassClient;
import org.elec.geoproject.client.OverpassResponse;
import org.elec.geoproject.dto.ImportRequest;
import org.elec.geoproject.entity.ImportJob;
import org.elec.geoproject.entity.ImportJobStatus;
import org.elec.geoproject.entity.Place;
import org.elec.geoproject.mapper.PlaceMapper;
import org.elec.geoproject.repository.ImportJobRepository;
import org.elec.geoproject.repository.PlaceRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportJobExecutor {

  private final ImportJobRepository importJobRepository;
  private final PlaceRepository placeRepository;
  private final OverpassClient overpassClient;

  @Async
  @Transactional
  public CompletableFuture<Void> execute(Long jobId, ImportRequest request) {
    log.info("Starting import job id={} city={} types={}", jobId, request.getCity(), request.getAmenities());

    ImportJob job = importJobRepository.findById(jobId).orElseThrow();
    job.setStatus(ImportJobStatus.RUNNING);
    importJobRepository.save(job);

    try {
      OverpassResponse response = overpassClient
          .fetchPlaces(request.getCity(), request.getAmenities())
          .block();

      if (response == null || response.getElements() == null) {
        return fail(job, "Overpass returned empty response");
      }

      int fetched = response.getElements().size();
      int saved = 0;

      for (OverpassResponse.OverpassElement el : response.getElements()) {
        Double lat = el.effectiveLat();
        Double lon = el.effectiveLon();
        if (lat == null || lon == null) continue;
        if (el.getId() != null && placeRepository.existsByOsmId(el.getId())) continue;

        String name = el.getTags() != null
            ? el.getTags().getOrDefault("name", "Unknown") : "Unknown";
        String description = el.getTags() != null
            ? el.getTags().get("description") : null;
        String type = resolveType(el, request.getAmenities());

        placeRepository.save(Place.builder()
            .name(name)
            .type(type)
            .description(description)
            .location(PlaceMapper.buildPoint(lon, lat))
            .osmId(el.getId())
            .build());
        saved++;
      }

      job.setTotalFetched(fetched);
      job.setTotalSaved(saved);
      job.setStatus(ImportJobStatus.DONE);
      job.setCompletedAt(LocalDateTime.now());
      importJobRepository.save(job);
      log.info("Import job id={} done: fetched={} saved={}", jobId, fetched, saved);

    } catch (Exception e) {
      log.error("Import job id={} failed: {}", jobId, e.getMessage());
      return fail(job, e.getMessage());
    }

    return CompletableFuture.completedFuture(null);
  }

  private String resolveType(OverpassResponse.OverpassElement el,
      java.util.List<String> requestedTypes) {
    if (el.getTags() == null) return "unknown";
    String amenity = el.getTags().get("amenity");
    String leisure = el.getTags().get("leisure");
    String tourism = el.getTags().get("tourism");

    for (String t : requestedTypes) {
      if (t.equalsIgnoreCase(amenity) || t.equalsIgnoreCase(leisure) || t.equalsIgnoreCase(tourism)) {
        return t;
      }
    }
    return amenity != null ? amenity : (leisure != null ? leisure : "unknown");
  }

  private CompletableFuture<Void> fail(ImportJob job, String message) {
    job.setStatus(ImportJobStatus.FAILED);
    job.setErrorMessage(message);
    job.setCompletedAt(LocalDateTime.now());
    importJobRepository.save(job);
    return CompletableFuture.completedFuture(null);
  }
}
