package learn.kafka.dechristo.controller;

import learn.kafka.dechristo.event.LibraryEvent;
import learn.kafka.dechristo.model.Book;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library.events"}, partitions = 3)
@TestPropertySource(properties = {
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}",
})
public class LibraryEventsControllerIntegrationTest {

    private Consumer<Integer, String> consumer;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerConfig =
            new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<>(consumerConfig, new IntegerDeserializer(), new StringDeserializer())
                .createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(1)
    void addBookSuccessfullyCreatesABook() {
        Book newBook = Book.builder()
            .name("Revival")
            .author("Stephen King")
            .id(1)
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .eventId(1001)
            .book(newBook)
            .build();

        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent);

        ResponseEntity<LibraryEvent> response =
            restTemplate.exchange("/api/v1/book", HttpMethod.POST, request, LibraryEvent.class);

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils
            .getSingleRecord(consumer, "library.events");
        String expectedMessage = "{\"eventId\":1001,\"eventType\":\"CREATE\",\"book\":{\"id\":1,\"name\":\"Revival\",\"author\":\"Stephen King\"}}";
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedMessage, consumerRecord.value());
    }

    @Test
    @Timeout(1)
    void addBookSuccessfullyUpdatesABook() {
        Book newBook = Book.builder()
            .name("Revival (Hardcover) EN/US")
            .author("Stephen King")
            .id(1)
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .eventId(1002)
            .book(newBook)
            .build();

        consumer.seek(new TopicPartition("library.events", 1), 1);

        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent);
        ResponseEntity<LibraryEvent> response =
            restTemplate.exchange("/api/v1/book", HttpMethod.PUT, request, LibraryEvent.class);

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils
            .getSingleRecord(consumer, "library.events");

        String expectedMessage = "{\"eventId\":1002,\"eventType\":\"UPDATE\",\"book\":{\"id\":1,\"name\":\"Revival (Hardcover) EN/US\",\"author\":\"Stephen King\"}}";
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, consumerRecord.value());
        consumer.commitSync();
    }
}
