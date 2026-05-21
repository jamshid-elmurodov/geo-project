package org.elec.geoproject.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elec.geoproject.dto.ImportJobResponse;
import org.elec.geoproject.dto.ImportRequest;
import org.elec.geoproject.entity.ImportJob;
import org.elec.geoproject.entity.ImportJobStatus;
import org.elec.geoproject.exception.ResourceNotFoundException;
import org.elec.geoproject.mapper.ImportJobMapper;
import org.elec.geoproject.repository.ImportJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImportService {

  private final ImportJobRepository importJobRepository;
  private final ImportJobMapper importJobMapper;
  private final ImportJobExecutor importJobExecutor;

  @Transactional
  public ImportJobResponse startImport(ImportRequest request) {
    ImportJob job = ImportJob.builder()
        .city(request.getCity())
        .placeTypes(request.getAmenities())
        .status(ImportJobStatus.PENDING)
        .build();
    job = importJobRepository.save(job);
    log.info("Created import job id={} city={} types={}", job.getId(), job.getCity(), job.getPlaceTypes());

    importJobExecutor.execute(job.getId(), request);
    return importJobMapper.toResponse(job);
  }

  public ImportJobResponse getJobStatus(Long id) {
    return importJobRepository.findById(id)
        .map(importJobMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("ImportJob", id));
  }

  public List<ImportJobResponse> getAllJobs() {
    return importJobRepository.findAll().stream()
        .map(importJobMapper::toResponse)
        .collect(Collectors.toList());
  }
}
