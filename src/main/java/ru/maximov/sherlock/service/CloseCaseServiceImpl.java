package ru.maximov.sherlock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maximov.sherlock.controller.dto.CloseCaseRequest;
import ru.maximov.sherlock.entity.CaseResult;
import ru.maximov.sherlock.entity.CaseStatus;
import ru.maximov.sherlock.integration.newscotlandyard.CaseReportsDepartment;
import ru.maximov.sherlock.integration.newscotlandyard.UpdateCaseInput;
import ru.maximov.sherlock.repository.CaseRepository;
import ru.maximov.sherlock.utils.TimeProvider;

@Service
@RequiredArgsConstructor
public class CloseCaseServiceImpl implements CloseCaseService {

    private final CaseRepository repository;

    private final CaseReportsDepartment caseReportsDepartment;

    private final TimeProvider timeProvider;

    @Override
    @Transactional
    public void closeCase(Long id, CloseCaseRequest request) {
        final var caseEntity = repository.findById(id).get();

        caseEntity.setStatus(CaseStatus.COMPLETED);
        caseEntity.setCompletedTime(timeProvider.now());
        caseEntity.setResult(request.result());
        caseEntity.setResultComment(request.resultComment());

        repository.save(caseEntity);

        final var updateCaseInput = new UpdateCaseInput();
        updateCaseInput.setCaseId(caseEntity.getCaseId());
        updateCaseInput.setResult(mapCaseResult(request.result()));
        updateCaseInput.setStatus("Close");
        updateCaseInput.setResultComment(request.resultComment());
        updateCaseInput.setCompletedTime(caseEntity.getCompletedTime());
        caseReportsDepartment.updateCase(updateCaseInput);
    }

    private String mapCaseResult(CaseResult result) {
        return switch (result) {
            case SUCCESS -> "Successfully";
            case FAIL -> "Failure";
        };
    }
}
