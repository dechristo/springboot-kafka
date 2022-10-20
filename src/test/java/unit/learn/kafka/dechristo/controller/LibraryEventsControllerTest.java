package learn.kafka.dechristo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.kafka.dechristo.event.LibraryEvent;
import learn.kafka.dechristo.model.Book;
import learn.kafka.dechristo.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryService libraryService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void addBookCreatesBookAndReturnsHttpCreated() throws Exception {
        Book newBook = Book.builder()
            .name("Revival")
            .author("Stephen King")
            .id(1)
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .eventId(1001)
            .book(newBook)
            .build();

        String json = objectMapper.writeValueAsString(libraryEvent);

        doNothing()
            .when(libraryService)
            .send(isA(LibraryEvent.class));

        mockMvc.perform(post("/api/v1/book")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    void addBookReturnsHttpBadRequestWhenNameIsEmpty() throws Exception {
        Book newBook = Book.builder()
            .name("")
            .author("Stephen King")
            .id(11)
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .eventId(1001)
            .book(newBook)
            .build();

        String json = objectMapper.writeValueAsString(libraryEvent);

        doNothing()
            .when(libraryService)
            .send(isA(LibraryEvent.class));

        String expectedErrorMsg = "book.name: must not be blank";
        mockMvc.perform(post("/api/v1/book")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(expectedErrorMsg));
    }

    @Test
    void addBookReturnsHttpBadRequestWhenAuthorIsEmpty() throws Exception {
        Book newBook = Book.builder()
            .name("Revival")
            .author("")
            .id(2)
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .eventId(1001)
            .book(newBook)
            .build();

        String json = objectMapper.writeValueAsString(libraryEvent);

        doNothing()
            .when(libraryService)
            .send(isA(LibraryEvent.class));

        String expectedErrorMsg = "book.author: must not be blank";
        mockMvc.perform(post("/api/v1/book")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(expectedErrorMsg));
    }

    @Test
    void addBookReturnsHttpBadRequestWhenIdNull() throws Exception {
        Book newBook = Book.builder()
            .name("Revival")
            .author("Stephen King")
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .eventId(1001)
            .book(newBook)
            .build();

        String json = objectMapper.writeValueAsString(libraryEvent);

        doNothing()
            .when(libraryService)
            .send(isA(LibraryEvent.class));

        String expectedErrorMsg = "book.id: must not be null";
        mockMvc.perform(post("/api/v1/book")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(expectedErrorMsg));
    }

    @Test
    void addBookReturnsHttpBadRequestWhenBookIsNull() throws Exception {
        LibraryEvent libraryEvent = LibraryEvent.builder()
            .eventId(1001)
            .book(null)
            .build();

        String json = objectMapper.writeValueAsString(libraryEvent);

        doNothing()
            .when(libraryService)
            .send(isA(LibraryEvent.class));

        String expectedErrorMsg = "book: must not be null";
        mockMvc.perform(post("/api/v1/book")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(expectedErrorMsg));
    }
}
