package ru.maximov.sherlock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import ru.maximov.sherlock.controller.dto.CloseCaseRequest;
import ru.maximov.sherlock.entity.CaseEntity;
import ru.maximov.sherlock.entity.CaseResult;
import ru.maximov.sherlock.entity.CaseStatus;
import ru.maximov.sherlock.integration.newscotlandyard.UpdateCaseInput;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CloseCaseTest extends BaseTest {

    @Captor
    private ArgumentCaptor<UpdateCaseInput> caseInputArgumentCaptor;

    @Test
    void closeSuccess() {
        final LocalDateTime closeTime = mockTime();

        CaseEntity caseEntity = createCaseEntity("The Hound of the Baskervilles");

        mockUpdate();

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
        final LocalDateTime closeTime = mockTime();

        CaseEntity caseEntity = createCaseEntity("The Reichenbach Fall");

        mockUpdate();

        final var request = new CloseCaseRequest(CaseResult.FAIL);

        final var responseEntity =
            restTemplate.postForEntity("/api/v1/case/" + caseEntity.getId() + "/close", request, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(caseReportsDepartment).updateCase(caseInputArgumentCaptor.capture());
        final var expectedInput = caseInputArgumentCaptor.getValue();
        assertThat(expectedInput.getCaseId()).isEqualTo(caseEntity.getCaseId());
        assertThat(expectedInput.getResult()).isEqualTo("Failure");
        assertThat(expectedInput.getResultComment()).isEqualTo(request.resultComment());
        assertThat(expectedInput.getStatus()).isEqualTo("Close");
        assertThat(expectedInput.getCompletedTime()).isEqualTo(closeTime);

        final var resultCaseEntity = repository.findById(caseEntity.getId()).get();
        assertThat(resultCaseEntity.getStatus()).isEqualTo(CaseStatus.COMPLETED);
        assertThat(resultCaseEntity.getResult()).isEqualTo(CaseResult.FAIL);
        assertThat(resultCaseEntity.getCompletedTime()).isEqualTo(closeTime);
    }

}
