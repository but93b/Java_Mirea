package mapperTicket_dto;

import org.example.rsocketserver.dto.TicketDTO;
import org.example.rsocketserver.model.Ticket;

public class MapperTicketDTO {

    /**
     * Преобразует объект Ticket в объект TicketDTO.
     *
     * @param ticket Объект Ticket для преобразования.
     * @return Объект TicketDTO, полученный из объекта Ticket.
     */
    public static TicketDTO mapTicketDTO(Ticket ticket) {
        // Создаем новый объект TicketDTO
        TicketDTO ticketDTO = new TicketDTO();

        // Устанавливаем значения полей из объекта Ticket
        ticketDTO.setId(ticket.getId());
        ticketDTO.setSeat(ticket.getSeat());
        ticketDTO.setStatus(ticket.getStatus());
        ticketDTO.setPassengerId(ticket.getPassengerId());

        // Устанавливаем идентификатор поезда из объекта Train, связанного с билетом
        if (ticket.getTrain() != null) {
            ticketDTO.setTrainId(ticket.getTrain().getId());
        }

        // Возвращаем созданный объект TicketDTO
        return ticketDTO;
    }
}