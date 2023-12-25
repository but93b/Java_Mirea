package Ticket_dto;

import lombok.Data;
import org.example.rsocketserver.model.TicketStatus;

@Data
public class TicketDTO {

    // Идентификатор билета
    private int id;

    // Номер места
    private String seat;

    // Статус билета (забронирован, свободен и т.д.)
    private TicketStatus status;

    // Идентификатор пассажира
    private String passengerId;

    // Идентификатор поезда
    private int trainId;
}
