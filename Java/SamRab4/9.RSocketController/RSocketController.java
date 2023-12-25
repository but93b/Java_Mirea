package RSocketController;

import BookArgs_dto.BookArgsDTO;
import RSocketService.RSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RSocketController {

    private final RSocketService rSocketService;
    private final List<RSocketRequester> CLIENTS = new ArrayList<>();

    // Метод, вызываемый при завершении приложения
    @PreDestroy
    void shutdown() {
        log.info("Detaching all remaining clients...");
        CLIENTS.forEach(requester -> requester.rsocket().dispose());
        log.info("Shutting down.");
    }

    // Метод для установления соединения с клиентом по запросу "shell-client"
    @ConnectMapping("shell-client")
    void connectShellClientAndAskForTelemetry(RSocketRequester requester, @Payload String client) {
        requester.rsocket()
                .onClose()
                .doFirst(() -> {
                    // Добавление новых клиентов в список клиентов
                    log.info("Client: {} CONNECTED.", client);
                    CLIENTS.add(requester);
                })
                .doOnError(error -> {
                    // Предупреждение при закрытии каналов клиентами
                    log.warn("Channel to client {} CLOSED", client);
                })
                .doFinally(consumer -> {
                    // Удаление отключенных клиентов из списка клиентов
                    CLIENTS.remove(requester);
                    log.info("Client {} DISCONNECTED", client);
                })
                .subscribe();

        // Callback клиенту для подтверждения соединения
        requester.route("client-status")
                .data("OPEN")
                .retrieveFlux(String.class)
                .doOnNext(s -> log.info("Client: {} Free Memory: {}.", client, s))
                .subscribe();
    }

    // Метод для бронирования места в поезде по запросу "request-response"
    @MessageMapping("request-response")
    Mono<String> book(final BookArgsDTO bookArgsDTO) {
        log.info("Received request-response ticketId: {}", bookArgsDTO.getTicketId());
        return rSocketService.book(bookArgsDTO);
    }

    // Метод для отмены бронирования места в поезде по запросу "fire-and-forget"
    @MessageMapping("fire-and-forget")
    public Mono<Void> deleteReservation(final int ticketId) {
        log.info("Received fire-and-forget ticketId: {}", ticketId);
        return rSocketService.deleteReservation(ticketId);
    }

    // Метод для получения списка всех поездов по запросу "request-stream"
    @MessageMapping("request-stream")
    Flux<TrainDTO> getAllTrains() {
        return rSocketService.getAllTrains();
    }

    // Метод для получения информации о билетах по id поездов по запросу "channel"
    @MessageMapping("channel")
    Flux<TicketDTO> getTickets(final Flux<Integer> fluxTrainsId) {
        Flux<Integer> loggedFluxTrainsId = fluxTrainsId
                .doOnCancel(() -> log.warn("The client cancelled the channel."))
                .doOnNext(trainId -> log.info("Channel trainId: {}", trainId));

        return rSocketService.getTickets(loggedFluxTrainsId);
    }
}
