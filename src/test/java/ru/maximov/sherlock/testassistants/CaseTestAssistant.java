package ru.maximov.sherlock.testassistants;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.maximov.sherlock.entity.CaseEntity;
import ru.maximov.sherlock.entity.CaseStatus;
import ru.maximov.sherlock.repository.CaseRepository;

@Component
public class CaseTestAssistant {

    private final CaseRepository repository;

    public CaseTestAssistant(CaseRepository repository) {
        this.repository = repository;
    }

    public CaseEntity.CaseEntityBuilder newCaseEntity() {
        return CaseEntity.builder()
            .caseId("CASE_1")
            .status(CaseStatus.NEW)
            .description("Test Description");
    }

    @NotNull
    public CaseEntity save(CaseEntity entity) {
        return repository.save(entity);
    }

    public CaseEntity findById(Long id) {
        return repository.findById(id).get();
    }
}
