package ru.maximov.sherlock.testassistants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Component;
import ru.maximov.sherlock.integration.newscotlandyard.CaseReportsDepartment;
import ru.maximov.sherlock.integration.newscotlandyard.UpdateCaseInput;
import ru.maximov.sherlock.integration.newscotlandyard.UpdateCaseOutput;

@Component
public class CaseReportsDepartmentTestAssistant {

    private final ArgumentCaptor<UpdateCaseInput> caseInputArgumentCaptor = ArgumentCaptor.forClass(
        UpdateCaseInput.class);

    @MockBean
    private CaseReportsDepartment caseReportsDepartmentMock;

    public void mockSuccessUpdate() {
        final var updateCaseOutput = new UpdateCaseOutput();
        updateCaseOutput.setCode("001");
        updateCaseOutput.setMessage("SUCCESS");
        when(caseReportsDepartmentMock.updateCase(any(UpdateCaseInput.class))).thenReturn(updateCaseOutput);
    }

    public void assertInteraction(Consumer<UpdateCaseInput> asserting) {
        verify(caseReportsDepartmentMock).updateCase(caseInputArgumentCaptor.capture());
        final var expectedInput = caseInputArgumentCaptor.getValue();
        asserting.accept(expectedInput);
    }
}
