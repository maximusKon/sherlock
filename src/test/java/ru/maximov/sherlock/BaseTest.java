package ru.maximov.sherlock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

public abstract class BaseTest {

    @Autowired
    protected TestRestTemplate restTemplate;

}
