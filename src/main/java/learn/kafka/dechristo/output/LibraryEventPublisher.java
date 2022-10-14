package learn.kafka.dechristo.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import learn.kafka.dechristo.callback.LibraryEventCallback;
import learn.kafka.dechristo.event.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Component
public class LibraryEventPublisher {

    private static final String topic = "library.event";
    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LibraryEventCallback libraryEventCallback = new LibraryEventCallback();

    public void send(LibraryEvent libraryEvent) throws JsonProcessingException {
       ListenableFuture<SendResult<Integer, String>> futureResult = kafkaTemplate.send(topic, libraryEvent.getEventId(), objectMapper.writeValueAsString(libraryEvent));
        futureResult.addCallback(libraryEventCallback);
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Successfully sent message with key {}", key);
    }

    private void handleError(Integer key, String value, Throwable error) {
        log.error("Error sending message with key {}: {}",key, error.getMessage());
    }



}
