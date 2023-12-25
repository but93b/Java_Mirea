package ClientHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class ClientHandler {

    /**
     * Обработчик сообщений о статусе клиента.
     *
     * @param status Статус подключения клиента.
     * @return Поток строк, представляющих собой обновления статуса клиента.
     */
    @MessageMapping("client-status")
    public Flux<String> statusUpdate(String status) {
        // Логгирование информации о подключении клиента
        log.info("Connection {}", status);

        // Возвращение потока строк с обновлениями статуса клиента с интервалом в 5 секунд
        return Flux.interval(Duration.ofSeconds(5)).map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
    }
}