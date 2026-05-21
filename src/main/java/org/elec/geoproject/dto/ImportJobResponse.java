package org.elec.geoproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.elec.geoproject.entity.ImportJobStatus;

@Data
@Builder
@Schema(description = "Import job status and result")
public class ImportJobResponse {

    private Long id;
    private String city;
    private List<String> placeTypes;
    private ImportJobStatus status;
    private Integer totalFetched;
    private Integer totalSaved;
    private Integer totalEnriched;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
