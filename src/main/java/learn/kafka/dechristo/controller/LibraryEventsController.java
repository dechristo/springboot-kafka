package learn.kafka.dechristo.controller;

import learn.kafka.dechristo.enums.LibraryEventType;
import learn.kafka.dechristo.event.LibraryEvent;
import learn.kafka.dechristo.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class LibraryEventsController {

    @Autowired
    LibraryService service;

    @PostMapping("api/v1/book")
    public ResponseEntity<LibraryEvent> addBook(
        @RequestBody
        @Valid LibraryEvent libraryEvent) {
        try {
            libraryEvent.setEventType(LibraryEventType.CREATE);
            service.send(libraryEvent);
            return  ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/api/v1/book")
    public ResponseEntity<?> updateBook(
        @RequestBody
        @Valid LibraryEvent libraryEvent) {
            try {
                if (libraryEvent.getEventId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("For updating, libraryEventId is mandatory.");
                }

                libraryEvent.setEventType(LibraryEventType.UPDATE);
                service.send(libraryEvent);
                return  ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
            } catch (Exception ex) {
                return ResponseEntity.internalServerError().build();
            }
        }
}
