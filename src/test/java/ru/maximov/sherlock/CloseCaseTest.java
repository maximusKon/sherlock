package ru.maximov.sherlock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import ru.maximov.sherlock.controller.dto.CloseCaseRequest;
import ru.maximov.sherlock.entity.CaseEntity;
import ru.maximov.sherlock.entity.CaseResult;
import ru.maximov.sherlock.entity.CaseStatus;
import ru.maximov.sherlock.integration.newscotlandyard.CaseReportsDepartment;
import ru.maximov.sherlock.integration.newscotlandyard.UpdateCaseInput;
import ru.maximov.sherlock.integration.newscotlandyard.UpdateCaseOutput;
import ru.maximov.sherlock.repository.CaseRepository;
import ru.maximov.sherlock.utils.TimeProvider;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CloseCaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CaseRepository repository;

    @MockBean
    private CaseReportsDepartment caseReportsDepartment;

    @MockBean
    private TimeProvider timeProvider;

    @Captor
    private ArgumentCaptor<UpdateCaseInput> caseInputArgumentCaptor;

    @Test
    void closeSuccess() {
        completeTest(CaseResult.SUCCESS, "The Hound of the Baskervilles");
    }

    @Test
    void closeFail() {
        completeTest(CaseResult.FAIL, "The Reichenbach Fall");
    }

    private void completeTest(CaseResult result, String description) {
        final LocalDateTime closeTime = mockTime();

        CaseEntity caseEntity = prepareToComplete(description);

        final var request = new CloseCaseRequest(result);

        invokeComplete(caseEntity, request);

        assertComplete(closeTime, caseEntity, request, result);
    }

    @NotNull
    private CaseEntity prepareToComplete(String description) {
        CaseEntity caseEntity = createCaseEntity(description);

        mockUpdate();
        return caseEntity;
    }

    private void assertComplete(LocalDateTime closeTime, CaseEntity caseEntity, CloseCaseRequest request,
                                CaseResult success) {
        assertUpdateIntegration(closeTime, caseEntity, request);

        assertCaseCompleted(closeTime, caseEntity, success);
    }

    private void invokeComplete(CaseEntity caseEntity, CloseCaseRequest request) {
        final var responseEntity =
            restTemplate.postForEntity("/api/v1/case/" + caseEntity.getId() + "/close", request, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @NotNull
    private LocalDateTime mockTime() {
        final var closeTime = LocalDateTime.now().withNano(0);
        when(timeProvider.now()).thenReturn(closeTime);
        return closeTime;
    }

    private void mockUpdate() {
        final var updateCaseOutput = new UpdateCaseOutput();
        updateCaseOutput.setCode("001");
        updateCaseOutput.setMessage("SUCCESS");
        when(caseReportsDepartment.updateCase(any(UpdateCaseInput.class))).thenReturn(updateCaseOutput);
    }

    @NotNull
    private CaseEntity createCaseEntity(String description) {
        var caseEntity = new CaseEntity();
        caseEntity.setCaseId("CASE_1");
        caseEntity.setDescription(description);
        caseEntity.setStatus(CaseStatus.NEW);
        caseEntity = repository.save(caseEntity);
        return caseEntity;
    }

    private void assertUpdateIntegration(LocalDateTime closeTime, CaseEntity caseEntity, CloseCaseRequest request) {
        verify(caseReportsDepartment).updateCase(caseInputArgumentCaptor.capture());
        final var expectedInput = caseInputArgumentCaptor.getValue();
        assertThat(expectedInput.getCaseId()).isEqualTo(caseEntity.getCaseId());
        if (request.result().equals(CaseResult.SUCCESS)) {
            assertThat(expectedInput.getResult()).isEqualTo("Successfully");
        } else {
            assertThat(expectedInput.getResult()).isEqualTo("Failure");
        }
        assertThat(expectedInput.getResultComment()).isEqualTo(request.resultComment());
        assertThat(expectedInput.getStatus()).isEqualTo("Close");
        assertThat(expectedInput.getCompletedTime()).isEqualTo(closeTime);
    }

    private void assertCaseCompleted(LocalDateTime closeTime, CaseEntity caseEntity, CaseResult success) {
        final var resultCaseEntity = repository.findById(caseEntity.getId()).get();
        assertThat(resultCaseEntity.getStatus()).isEqualTo(CaseStatus.COMPLETED);
        assertThat(resultCaseEntity.getResult()).isEqualTo(success);
        assertThat(resultCaseEntity.getCompletedTime()).isEqualTo(closeTime);
    }
}
