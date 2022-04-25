package ru.maximov.sherlock.integration.newscotlandyard;

import org.springframework.stereotype.Component;

@Component
public class StubReportsDepartment
    implements CaseReportsDepartment {

    @Override
    public UpdateCaseOutput updateCase(UpdateCaseInput input) {
        return null;
    }
}
