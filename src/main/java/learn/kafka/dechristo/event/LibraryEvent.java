package learn.kafka.dechristo.event;

import learn.kafka.dechristo.enums.LibraryEventType;
import learn.kafka.dechristo.model.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibraryEvent {
    private Integer eventId;
    private LibraryEventType eventType;
    @NotNull
    @Valid
    private Book book;
}
