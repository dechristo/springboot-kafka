package learn.kafka.dechristo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import learn.kafka.dechristo.event.LibraryEvent;
import learn.kafka.dechristo.output.LibraryEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibraryService {

    @Autowired
    LibraryEventPublisher libraryEventPublisher;

    public void send(LibraryEvent libraryEvent) throws JsonProcessingException {
        libraryEventPublisher.send(libraryEvent);
    }
}
