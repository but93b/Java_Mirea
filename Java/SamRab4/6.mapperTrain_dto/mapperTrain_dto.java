package mapperTrain_dto;

import Train_dto.TrainDTO;
import org.example.rsocketserver.model.Train;

import java.text.SimpleDateFormat;

public class MapperTrainDTO {

    // Формат для отображения времени
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd-mm-yyyyy");

    /**
     * Преобразует объект Train в объект TrainDTO.
     *
     * @param train Объект Train для преобразования.
     * @return Объект TrainDTO, полученный из объекта Train.
     */
    public static TrainDTO mapTrainDTO(Train train) {
        // Создаем новый объект TrainDTO
        TrainDTO trainDTO = new TrainDTO();

        // Устанавливаем значения полей из объекта Train
        trainDTO.setId(train.getId());
        trainDTO.setFrom(train.getFrom());
        trainDTO.setTo(train.getTo());

        // Форматируем и устанавливаем временные значения, если они присутствуют
        if (train.getDepartureTime() != null) {
            trainDTO.setDepartureFormattedTime(simpleDateFormat.format(train.getDepartureTime()));
        }

        if (train.getArrivalTime() != null) {
            trainDTO.setArrivalFormattedTime(simpleDateFormat.format(train.getArrivalTime()));
        }

        // Возвращаем созданный объект TrainDTO
        return trainDTO;
    }
}
