package RSocketService;

import lombok.RequiredArgsConstructor;
import org.example.rsocketserver.dto.BookArgsDTO;
import org.example.rsocketserver.dto.TrainDTO;
import org.example.rsocketserver.dto.TicketDTO;
import org.example.rsocketserver.mappers.MapperTrainDTO;
import org.example.rsocketserver.mappers.MapperTicketDTO;
import org.example.rsocketserver.model.Train;
import org.example.rsocketserver.model.Ticket;
import org.example.rsocketserver.model.TicketStatus;
import org.example.rsocketserver.repositories.TrainRepository;
import org.example.rsocketserver.repositories.TicketRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RSocketService {
    
    // Репозиторий поездов
    private final TrainRepository trainRepository;
    
    // Репозиторий билетов
    private final TicketRepository ticketRepository;

    // Метод для бронирования билета
    public Mono<String> book(final BookArgsDTO bookArgsDTO) {
        Ticket ticket = ticketRepository.findById(bookArgsDTO.getTicketId()).get();

        if (ticket.getStatus().equals(TicketStatus.BOOKED)) {
            return Mono.just("The place is occupied");
        }

        ticket.setStatus(TicketStatus.BOOKED);
        ticket.setPassengerId(bookArgsDTO.getPassengerId());

        ticketRepository.save(ticket);

        return Mono.just("Success");
    }

    // Метод для отмены бронирования билета
    public Mono<Void> deleteReservation(final int ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).get();

        ticket.setStatus(TicketStatus.FREE);
        ticket.setPassengerId(null);

        ticketRepository.save(ticket);

        return Mono.empty();
    }

    // Метод для получения Flux с информацией о всех поездах
    public Flux<TrainDTO> getAllTrains() {
        return Flux
                .fromIterable(trainRepository.findAll())
                .map(MapperTrainDTO::mapTrainDTO);
    }

    // Метод для получения Flux с информацией о билетах для указанных поездов
    public Flux<TicketDTO> getTickets(final Flux<Integer> fluxTrainsId) {
        return fluxTrainsId
                .switchMap(trainId -> {
                    Train train = trainRepository.findById(trainId).get();

                    return Flux.fromIterable(train.getTickets())
                            .map(MapperTicketDTO::mapTicketDTO);
                });
    }
}
