package org.example.rsocketserver.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "train")
public class Train {

    // Уникальный идентификатор поезда, генерируемый автоматически
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    // Место отправления поезда
    @Column(name = "from_place")
    private String from;

    // Место назначения поезда
    @Column(name = "to_place")
    private String to;

    // Время отправления поезда
    @Column(name = "departure_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date departureTime;

    // Время прибытия поезда
    @Column(name = "arrival_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date arrivalTime;

    // Связь с билетами по отношению "один ко многим"
    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Ticket> tickets;

    // Метод для добавления билета к поезду
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setTrain(this);
    }

    // Метод для удаления билета из поезда
    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setTrain(null);
    }
}
