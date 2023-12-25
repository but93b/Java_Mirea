package BookArgs_dto;

import lombok.Data;

@Data
public class BookArgsDTO {

    // Идентификатор билета для бронирования
    private int ticketId;

    // Идентификатор пассажира, который бронирует билет
    private String passengerId;
}
