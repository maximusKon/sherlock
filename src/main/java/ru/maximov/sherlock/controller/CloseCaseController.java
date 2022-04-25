package ru.maximov.sherlock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.maximov.sherlock.controller.dto.CloseCaseRequest;
import ru.maximov.sherlock.service.CloseCaseService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CloseCaseController {

    private final CloseCaseService service;

    @RequestMapping("/case/{id}/close")
    public void closeCase(@PathVariable("id") Long id, @RequestBody CloseCaseRequest request) {
        service.closeCase(id, request);
    }

}
