package ru.maximov.sherlock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import ru.maximov.sherlock.integration.newscotlandyard.CaseReportsDepartment;
import ru.maximov.sherlock.integration.newscotlandyard.UpdateCaseInput;
import ru.maximov.sherlock.integration.newscotlandyard.UpdateCaseOutput;
import ru.maximov.sherlock.utils.TimeProvider;

public abstract class BaseTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @MockBean
    protected CaseReportsDepartment caseReportsDepartment;

    @MockBean
    protected TimeProvider timeProvider;

    @NotNull
    protected LocalDateTime mockTime() {
        final var closeTime = LocalDateTime.now().withNano(0);
        when(timeProvider.now()).thenReturn(closeTime);
        return closeTime;
    }

    protected void mockUpdate() {
        final var updateCaseOutput = new UpdateCaseOutput();
        updateCaseOutput.setCode("001");
        updateCaseOutput.setMessage("SUCCESS");
        when(caseReportsDepartment.updateCase(any(UpdateCaseInput.class))).thenReturn(updateCaseOutput);
    }

}
