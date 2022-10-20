package learn.kafka.dechristo.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import learn.kafka.dechristo.event.LibraryEvent;
import learn.kafka.dechristo.model.Book;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibraryEventPublisherTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplateMock;

    @Spy
    ObjectMapper objectMapperSpy = new ObjectMapper();

    @InjectMocks
    LibraryEventPublisher libraryEventPublisher;

    @Test
    void producerSendFailureThrowsException() throws JsonProcessingException, ExecutionException, InterruptedException {
        Book newBook = Book.builder()
            .name("Ocean Prey")
            .author("John Sandford")
            .id(7)
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .eventId(1001)
            .book(newBook)
            .build();

        SettableListenableFuture<SendResult<Integer, String>> futureResult = new SettableListenableFuture<SendResult<Integer, String>>();
        futureResult.setException(new RuntimeException("Exception calling Kafka."));

        when(kafkaTemplateMock
            .send(isA(ProducerRecord.class)))
            .thenReturn(futureResult);

        assertThrows(Exception.class, () ->
            libraryEventPublisher.send(libraryEvent).get());

    }

    @Test
    void producerSendMessageSuccessfully() throws JsonProcessingException, ExecutionException, InterruptedException {
        Book newBook = Book.builder()
            .name("Ocean Prey")
            .author("John Sandford")
            .id(99)
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .eventId(1007)
            .book(newBook)
            .build();

        SettableListenableFuture<SendResult<Integer, String>> futureResult = new SettableListenableFuture<>();
        String record = objectMapperSpy.writeValueAsString(libraryEvent);
        ProducerRecord<Integer, String> producerRecord =
            new ProducerRecord<>("library.events", libraryEvent.getEventId(), record);
        RecordMetadata recordMetadata =
            new RecordMetadata(new TopicPartition("library.events", 1),
                1, 1, 342, System.currentTimeMillis(),1, 2);

        SendResult<Integer, String> sendResultExpected = new SendResult<Integer, String>(producerRecord, recordMetadata);

        futureResult.set(sendResultExpected);

        when(kafkaTemplateMock
            .send(eq("library.events"), anyInt(), anyString()))
            .thenReturn(futureResult);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = libraryEventPublisher.send(libraryEvent);
        SendResult<Integer, String> sendResult = listenableFuture.get();

        assertEquals(1, sendResult.getRecordMetadata().partition());
    }
}
