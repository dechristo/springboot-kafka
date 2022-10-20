package learn.kafka.dechristo.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class LibraryEventCallback implements ListenableFutureCallback {
    @Override
    public void onFailure(Throwable ex) {
        handleError(ex);
    }

    @Override
    public void onSuccess(Object result) {
        handleSuccess();
    }

    private void handleSuccess() {
        log.info("Successfully sent message with.");
    }

    private void handleError(Throwable error) {
        log.error("Error sending message: {}", error.getMessage());
    }
}
