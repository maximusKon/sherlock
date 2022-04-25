package ru.maximov.sherlock.integration.newscotlandyard;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UpdateCaseInput {

    private String caseId;

    private String description;

    private String status;

    private LocalDateTime completedTime;

    private String result;

    private String resultComment;

}
