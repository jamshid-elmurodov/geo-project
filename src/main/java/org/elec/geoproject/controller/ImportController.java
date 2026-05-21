package org.elec.geoproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.elec.geoproject.dto.ImportJobResponse;
import org.elec.geoproject.dto.ImportRequest;
import org.elec.geoproject.service.ImportService;
import org.elec.geoproject.service.NominatimEnrichmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
@Tag(name = "Import", description = "OSM import pipeline and Nominatim enrichment")
public class ImportController {

  private final ImportService importService;
  private final NominatimEnrichmentService enrichmentService;

  @PostMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(summary = "Start async import from Overpass API (returns job ID immediately)")
  public ImportJobResponse startImport(@Valid @RequestBody ImportRequest request) {
    return importService.startImport(request);
  }

  @GetMapping("/jobs")
  @Operation(summary = "List all import jobs")
  public List<ImportJobResponse> getAllJobs() {
    return importService.getAllJobs();
  }

  @GetMapping("/jobs/{id}")
  @Operation(summary = "Get import job status by ID")
  public ImportJobResponse getJobStatus(@PathVariable Long id) {
    return importService.getJobStatus(id);
  }

  @PostMapping("/enrich-all")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(summary = "Enrich all un-addressed places via Nominatim (async, 1 req/sec)")
  public Map<String, String> enrichAll() {
    enrichmentService.enrichAll();
    return Map.of("message", "Nominatim enrichment started asynchronously");
  }
}
