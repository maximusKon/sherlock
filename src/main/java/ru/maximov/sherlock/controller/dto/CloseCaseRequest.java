package ru.maximov.sherlock.controller.dto;

import ru.maximov.sherlock.entity.CaseResult;

public record CloseCaseRequest(
    CaseResult result,
    String resultComment
) {

    public CloseCaseRequest(CaseResult result) {
        this(result, null);
    }
}