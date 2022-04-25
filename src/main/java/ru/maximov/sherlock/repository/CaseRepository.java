package ru.maximov.sherlock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maximov.sherlock.entity.CaseEntity;

public interface CaseRepository extends JpaRepository<CaseEntity, Long> {
}
