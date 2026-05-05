package com.microservice.Servicio_TeamReadbull.model;

import java.util.ArrayList;
import java.util.List;

import com.microservice.Servicio_TeamReadbull.model.Notification.ObservableSubject;
import com.microservice.Servicio_TeamReadbull.model.Notification.Observer;

import jakarta.persistence.*;
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

    public enum TournamentStatus {
        NONE, DRAFT, ACTIVE, IN_PROGRESS, FINISHED
    }

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus tournamentStatus = TournamentStatus.NONE;

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
    public void subscribe(Observer observer) { subscribers.add(observer); }

    @Override
    public void unsubscribe(Observer observer) { subscribers.remove(observer); }

    @Override
    public void notifyObservers() { subscribers.forEach(Observer::update); }

    @Override
    public void update() { notifyObservers(); }

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

    // HU-02
    public boolean isInActiveTournament() {
        return tournamentStatus == TournamentStatus.ACTIVE
                || tournamentStatus == TournamentStatus.IN_PROGRESS;
    }

    public boolean hasValidNumberOfPlayers() {
        return currentPlayers >= minPlayers && currentPlayers <= maxPlayers;
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
        if (playerId.equals(idCaptain)) {
            throw new IllegalStateException("No es posible eliminar al capitán del equipo.");
        }
        if (!players.remove(playerId)) {
            throw new IllegalStateException("Jugador no encontrado en el equipo.");
        }
        currentPlayers--;
    }

    public boolean playersWithDiferentDorsal() {
        return true;
    }

    public boolean halfOfStudentsAreOfAllowedPrograms() {
        return true;
    }

    public void validateTeam() {
        this.isValidTeam = this.hasValidNumberOfPlayers()
                && this.playersWithDiferentDorsal()
                && this.halfOfStudentsAreOfAllowedPrograms();
    }
}