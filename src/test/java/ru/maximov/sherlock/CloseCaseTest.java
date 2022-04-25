package ru.maximov.sherlock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
        final var closeTime = LocalDateTime.now().withNano(0);
        when(timeProvider.now()).thenReturn(closeTime);

        var caseEntity = new CaseEntity();
        caseEntity.setCaseId("CASE_1");
        caseEntity.setDescription("The Hound of the Baskervilles");
        caseEntity.setStatus(CaseStatus.NEW);
        caseEntity = repository.save(caseEntity);

        final var updateCaseOutput = new UpdateCaseOutput();
        updateCaseOutput.setCode("001");
        updateCaseOutput.setMessage("SUCCESS");
        when(caseReportsDepartment.updateCase(any(UpdateCaseInput.class))).thenReturn(updateCaseOutput);

        final var request = new CloseCaseRequest(CaseResult.SUCCESS);

        final var responseEntity =
            restTemplate.postForEntity("/api/v1/case/" + caseEntity.getId() + "/close", request, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(caseReportsDepartment).updateCase(caseInputArgumentCaptor.capture());
        final var expectedInput = caseInputArgumentCaptor.getValue();
        assertThat(expectedInput.getCaseId()).isEqualTo(caseEntity.getCaseId());
        assertThat(expectedInput.getResult()).isEqualTo("Successfully");
        assertThat(expectedInput.getStatus()).isEqualTo("Close");
        assertThat(expectedInput.getCompletedTime()).isEqualTo(closeTime);

        final var resultCaseEntity = repository.findById(caseEntity.getId()).get();
        assertThat(resultCaseEntity.getStatus()).isEqualTo(CaseStatus.COMPLETED);
        assertThat(resultCaseEntity.getResult()).isEqualTo(CaseResult.SUCCESS);
        assertThat(resultCaseEntity.getCompletedTime()).isEqualTo(closeTime);
    }

    @Test
    void closeFail() {
        final var closeTime = LocalDateTime.now().withNano(0);
        when(timeProvider.now()).thenReturn(closeTime);

        var caseEntity = new CaseEntity();
        caseEntity.setCaseId("CASE_1");
        caseEntity.setDescription("The Reichenbach Fall");
        caseEntity.setStatus(CaseStatus.NEW);
        caseEntity = repository.save(caseEntity);

        final var updateCaseOutput = new UpdateCaseOutput();
        updateCaseOutput.setCode("001");
        updateCaseOutput.setMessage("SUCCESS");
        when(caseReportsDepartment.updateCase(any(UpdateCaseInput.class))).thenReturn(updateCaseOutput);

        final var request = new CloseCaseRequest(CaseResult.FAIL, "Code is a fake");

        final var responseEntity =
            restTemplate.postForEntity("/api/v1/case/" + caseEntity.getId() + "/close", request, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(caseReportsDepartment).updateCase(caseInputArgumentCaptor.capture());
        final var expectedInput = caseInputArgumentCaptor.getValue();
        assertThat(expectedInput.getCaseId()).isEqualTo(caseEntity.getCaseId());
        assertThat(expectedInput.getResult()).isEqualTo("Failure");
        assertThat(expectedInput.getResultComment()).isEqualTo("Code is a fake");
        assertThat(expectedInput.getStatus()).isEqualTo("Close");
        assertThat(expectedInput.getCompletedTime()).isEqualTo(closeTime);

        final var resultCaseEntity = repository.findById(caseEntity.getId()).get();
        assertThat(resultCaseEntity.getStatus()).isEqualTo(CaseStatus.COMPLETED);
        assertThat(resultCaseEntity.getResult()).isEqualTo(CaseResult.FAIL);
        assertThat(resultCaseEntity.getCompletedTime()).isEqualTo(closeTime);
    }
}
