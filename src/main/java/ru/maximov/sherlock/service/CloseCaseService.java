package ru.maximov.sherlock.service;

import ru.maximov.sherlock.controller.dto.CloseCaseRequest;

public interface CloseCaseService {

    void closeCase(Long id, CloseCaseRequest request);

}
