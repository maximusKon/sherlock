package ru.maximov.sherlock.testassistants;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Component;
import ru.maximov.sherlock.utils.TimeProvider;

@Component
public class TimeProviderTestAssistant {

    @MockBean
    protected TimeProvider timeProvider;

    @NotNull
    public LocalDateTime mockCurrentTime() {
        final var closeTime = LocalDateTime.now().withNano(0);
        when(timeProvider.now()).thenReturn(closeTime);
        return closeTime;
    }

}
