package ru.maximov.sherlock;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import ru.maximov.sherlock.controller.dto.CloseCaseRequest;
import ru.maximov.sherlock.entity.CaseEntity;
import ru.maximov.sherlock.entity.CaseResult;
import ru.maximov.sherlock.entity.CaseStatus;
import ru.maximov.sherlock.testassistants.CaseReportsDepartmentTestAssistant;
import ru.maximov.sherlock.testassistants.CaseTestAssistant;
import ru.maximov.sherlock.testassistants.TimeProviderTestAssistant;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CloseCaseTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private CaseTestAssistant caseTestAssistant;

    @Autowired
    private CaseReportsDepartmentTestAssistant caseReportsDepartmentTestAssistant;

    @Autowired
    private TimeProviderTestAssistant timeProviderTestAssistant;

    @Test
    void closeSuccess() {
        final LocalDateTime closeTime = timeProviderTestAssistant.mockCurrentTime();

        final CaseEntity caseEntity = caseTestAssistant.save(
            caseTestAssistant.newCaseEntity()
                .description("The Hound of the Baskervilles")
                .build()
        );

        caseReportsDepartmentTestAssistant.mockSuccessUpdate();

        final var request = new CloseCaseRequest(CaseResult.SUCCESS);

        final var responseEntity =
            restTemplate.postForEntity("/api/v1/case/" + caseEntity.getId() + "/close", request, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        caseReportsDepartmentTestAssistant.assertInteraction(expectedInput -> {
            assertThat(expectedInput.getCaseId()).isEqualTo(caseEntity.getCaseId());
            assertThat(expectedInput.getResult()).isEqualTo("Successfully");
            assertThat(expectedInput.getStatus()).isEqualTo("Close");
            assertThat(expectedInput.getCompletedTime()).isEqualTo(closeTime);
        });

        final var resultCaseEntity = caseTestAssistant.findById(caseEntity.getId());
        assertThat(resultCaseEntity.getStatus()).isEqualTo(CaseStatus.COMPLETED);
        assertThat(resultCaseEntity.getResult()).isEqualTo(CaseResult.SUCCESS);
        assertThat(resultCaseEntity.getCompletedTime()).isEqualTo(closeTime);
    }

    @Test
    void closeFail() {
        final LocalDateTime closeTime = timeProviderTestAssistant.mockCurrentTime();

        final CaseEntity caseEntity = caseTestAssistant.save(
            caseTestAssistant.newCaseEntity()
                .description("The Reichenbach Fall")
                .build()
        );

        caseReportsDepartmentTestAssistant.mockSuccessUpdate();

        final var request = new CloseCaseRequest(CaseResult.FAIL);

        final var responseEntity =
            restTemplate.postForEntity("/api/v1/case/" + caseEntity.getId() + "/close", request, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        caseReportsDepartmentTestAssistant.assertInteraction(expectedInput -> {
            assertThat(expectedInput.getCaseId()).isEqualTo(caseEntity.getCaseId());
            assertThat(expectedInput.getResult()).isEqualTo("Failure");
            assertThat(expectedInput.getResultComment()).isEqualTo(request.resultComment());
            assertThat(expectedInput.getStatus()).isEqualTo("Close");
            assertThat(expectedInput.getCompletedTime()).isEqualTo(closeTime);
        });

        final var resultCaseEntity = caseTestAssistant.findById(caseEntity.getId());
        assertThat(resultCaseEntity.getStatus()).isEqualTo(CaseStatus.COMPLETED);
        assertThat(resultCaseEntity.getResult()).isEqualTo(CaseResult.FAIL);
        assertThat(resultCaseEntity.getCompletedTime()).isEqualTo(closeTime);
    }

    @AfterEach
    void tearDown() {
        caseTestAssistant.clean();
    }
}
