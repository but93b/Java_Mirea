package RSocketShellClient;

import ClientHandler.ClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.rsocket.annotation.MessageMapping;
import org.springframework.messaging.rsocket.annotation.RSocketMessageHandler;
import org.springframework.messaging.rsocket.annotation.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@ShellComponent
public class RSocketShellClient {

    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private static Disposable disposable;
    private final RSocketRequester rsocketRequester;

    // Конструктор, принимающий Builder и стратегии RSocket для создания клиента
    @Autowired
    public RSocketShellClient(RSocketRequester.Builder builder,
                              @Qualifier("rSocketStrategies") RSocketStrategies strategies) {
        // Создание акцептора RSocketMessageHandler
        SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());

        // Подключение к серверу RSocket
        this.rsocketRequester = builder
                .setupRoute("shell-client")
                .setupData(CLIENT_ID)
                .rsocketConnector(connector -> connector.acceptor(responder))
                .connectTcp("localhost", 7000)
                .block();

        // Обработка событий закрытия соединения
        this.rsocketRequester.rsocket()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("Client DISCONNECTED"))
                .subscribe();
    }

    // Метод, вызываемый при завершении приложения, отключает клиента от сервера
    @PreDestroy
    @ShellMethod("Logout and close your connection")
    public void logout() {
        if (isConnection()) {
            this.s();
            this.rsocketRequester.rsocket().dispose();
            log.info("Logged out.");
        }
    }

    // Проверка наличия соединения
    private boolean isConnection() {
        if (null == this.rsocketRequester || this.rsocketRequester.rsocket().isDisposed()) {
            log.info("No connection.");
            return false;
        }
        return true;
    }

    // Метод для бронирования места в поезде
    @ShellMethod("Book")
    public void book(int ticketId, String passengerId) throws InterruptedException {
        if (isConnection()) {
            log.info("\nSending one request. Waiting for one response...");
            String status = this.rsocketRequester
                    .route("request-response")
                    .data(new BookDataDTO(ticketId, passengerId))
                    .retrieveMono(String.class)
                    .block();
            log.info("\nResponse was: {}", status);
        }
    }

    // Метод для отмены бронирования места в поезде
    @ShellMethod("Delete a reservation")
    public void deleteReservation(int ticketId) throws InterruptedException {
        if (isConnection()) {
            log.info("\nFire-And-Forget. Sending one request. Expect no response (check server console log)...");
            this.rsocketRequester
                    .route("fire-and-forget")
                    .data(ticketId)
                    .send()
                    .block();
            log.info("Delete a reservation by ticket id: {}", ticketId);
        }
    }

    // Метод для получения списка всех поездов
    @ShellMethod("Get all trains")
    public void trains() {
        if (isConnection()) {
            log.info("\n\n**** Request-Stream\n**** Send one request.\n**** Log responses.\n**** Type 's' to stop.");
            disposable = this.rsocketRequester
                    .route("request-stream")
                    .retrieveFlux(TrainDTO.class)
                    .doOnComplete(() -> log.info("Finish get all trains"))
                    .subscribe(trainDTO -> log.info("\nResponse: {} \n(Type 's' to stop.)", trainDTO.toString()));
        }
    }

    // Метод для получения информации о билетах по id поездов
    @ShellMethod("Get tickets by train id")
    public void tickets(String trainIds) {
        if (isConnection()) {
            log.info("\n\n***** Channel (bi-directional streams)\n***** Asking for a stream of messages.\n***** Type 's' to stop.\n\n");

            List<String> arrTrainId = Arrays.asList(trainIds.split(" "));
            if (arrTrainId.isEmpty()) {
                log.info("Empty train-ids parameter");
                return;
            }

            Flux<Integer> fluxTrainIds = Flux.fromIterable(arrTrainId).map(Integer::valueOf);

            disposable = this.rsocketRequester
                    .route("channel")
                    .data(fluxTrainIds)
                    .retrieveFlux(TicketDTO.class)
                    .subscribe(ticketDTO -> log.info("\nReceived: {} \n(Type 's' to stop.)", ticketDTO));
        }
    }

    // Метод для остановки текущего потока данных
    @ShellMethod("Stops Streams or Channels.")
    public void s() {
        if (isConnection() && null != disposable) {
            log.info("Stopping the current stream.");
            disposable.dispose();
            log.info("Stream stopped.");
        }
    }
}
