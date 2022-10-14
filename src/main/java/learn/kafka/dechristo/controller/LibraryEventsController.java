package learn.kafka.dechristo.controller;

import learn.kafka.dechristo.event.LibraryEvent;
import learn.kafka.dechristo.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryEventsController {

    @Autowired
    LibraryService service;

    @PostMapping("api/v1/book")
    public ResponseEntity<LibraryEvent> addBook(@RequestBody LibraryEvent libraryEvent) {
        try {
            service.send(libraryEvent);
            return  ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/api/v1/book")
    public ResponseEntity<LibraryEvent> updateBook(LibraryEvent libraryEvent) {
        return ResponseEntity.ok().build();
    }
}
