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

    private static final String topic = "library.events";
    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LibraryEventCallback libraryEventCallback = new LibraryEventCallback();

    public ListenableFuture<SendResult<Integer, String>> send(LibraryEvent libraryEvent) throws JsonProcessingException {
       ListenableFuture<SendResult<Integer, String>> futureResult =
           kafkaTemplate.send(topic, libraryEvent.getEventId(), objectMapper.writeValueAsString(libraryEvent));
        futureResult.addCallback(libraryEventCallback);

        return futureResult;
    }
}
