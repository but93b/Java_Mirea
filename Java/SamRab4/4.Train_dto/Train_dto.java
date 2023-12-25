package Train_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainDTO {

    // Идентификатор поезда
    private int id;

    // Пункт назначения
    private String to;

    // Пункт отправления
    private String from;

    // Отформатированное время отправления
    private String departureFormattedTime;

    // Отформатированное время прибытия
    private String arrivalFormattedTime;
}
