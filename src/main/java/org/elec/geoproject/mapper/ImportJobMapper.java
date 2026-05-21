package org.elec.geoproject.mapper;

import org.elec.geoproject.dto.ImportJobResponse;
import org.elec.geoproject.entity.ImportJob;
import org.springframework.stereotype.Component;

@Component
public class ImportJobMapper {

    public ImportJobResponse toResponse(ImportJob job) {
        return ImportJobResponse.builder()
                .id(job.getId())
                .city(job.getCity())
                .placeTypes(job.getPlaceTypes())
                .status(job.getStatus())
                .totalFetched(job.getTotalFetched())
                .totalSaved(job.getTotalSaved())
                .totalEnriched(job.getTotalEnriched())
                .errorMessage(job.getErrorMessage())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .completedAt(job.getCompletedAt())
                .build();
    }
}
