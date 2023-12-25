package org.example.rsocketserver.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "ticket")
public class Ticket {

    // Уникальный идентификатор билета, генерируемый автоматически
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    // Номер места в поезде
    @Column(name = "seat")
    private String seat;

    // Статус билета (забронирован, свободен и т.д.)
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    // Идентификатор пассажира, если место забронировано
    @Column(name = "passenger_id")
    private String passengerId;

    // Связь с поездом, к которому относится билет
    @ManyToOne
    private Train train;
}
