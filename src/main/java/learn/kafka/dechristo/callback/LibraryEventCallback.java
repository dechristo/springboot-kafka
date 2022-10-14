package learn.kafka.dechristo.callback;

import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class LibraryEventCallback implements ListenableFutureCallback {
    @Override
    public void onFailure(Throwable ex) {

    }

    @Override
    public void onSuccess(Object result) {

    }
}
