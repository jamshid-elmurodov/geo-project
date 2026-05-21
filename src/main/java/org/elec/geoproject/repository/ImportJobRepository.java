package org.elec.geoproject.repository;

import java.util.Optional;
import org.elec.geoproject.entity.ImportJob;
import org.elec.geoproject.entity.ImportJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportJobRepository extends JpaRepository<ImportJob, Long> {

  Optional<ImportJob> findTopByStatusOrderByCompletedAtDesc(ImportJobStatus status);
}
