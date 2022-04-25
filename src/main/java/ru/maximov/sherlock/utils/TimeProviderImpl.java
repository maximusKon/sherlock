package ru.maximov.sherlock.utils;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class TimeProviderImpl implements TimeProvider {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
