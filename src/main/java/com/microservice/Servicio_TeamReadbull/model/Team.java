package com.microservice.Servicio_TeamReadbull.model;

import java.util.ArrayList;
import java.util.List;

import com.microservice.Servicio_TeamReadbull.model.Notification.ObservableSubject;
import com.microservice.Servicio_TeamReadbull.model.Notification.Observer;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teams")
public class Team implements ObservableSubject, Observer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Transient
    private List<Observer> subscribers = new ArrayList<>();

    @Column(nullable = true)
    private Long idTournament; 

    @Column(nullable = false)
    private Long idCaptain; 

    @ElementCollection
    private List<Long> players = new ArrayList<>();

    @Column(nullable = false)
    private int currentPlayers;

    @Transient
    private int maxPlayers = 12;

    @Transient
    private int minPlayers = 7;

    @Transient
    private boolean isValidTeam = false;

    @ElementCollection
    private List<Long> requests = new ArrayList<>();




    @Override
    public void subscribe(Observer observer) {
        subscribers.add(observer);
    }

    @Override
    public void unsubscribe(Observer observer) {
        subscribers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        subscribers.forEach(Observer::update);
    }

    @Override
    public void update() {
        notifyObservers();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Observer> getSubscribers() { return subscribers; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id != null && id.equals(team.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    public boolean hasValidNumberOfPlayers() {
        if(currentPlayers<= maxPlayers && currentPlayers >= minPlayers) {
            return true;
        }
        return false;
    }

    public void addPlayer(Long playerId) {
        if (currentPlayers < maxPlayers) {
            players.add(playerId);
            currentPlayers++;
        } else {
            throw new IllegalStateException("No es posible agregar más jugadores, el equipo está lleno.");
        }
    }

    public void removePlayer(Long playerId) {
        if (players.remove(playerId) && playerId != idCaptain) {
            currentPlayers--;
        } else if (playerId == idCaptain) {
            throw new IllegalStateException("No es posible eliminar al capitán del equipo.");
        } else {
            throw new IllegalStateException("Jugador no encontrado en el equipo.");
        }
    }

    

    public boolean playersWithDiferentDorsal() {
        return true;
    }

    public boolean halfOfStudentsAreOfAllowedPrograms() {
        return true;
    }

    public void validateTeam() {
        if (!this.hasValidNumberOfPlayers() 
            || !this.playersWithDiferentDorsal() 
            || !this.halfOfStudentsAreOfAllowedPrograms()) {
                
            this.isValidTeam = false;
        } else {
            this.isValidTeam = true;
        }

    }
}
