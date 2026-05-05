package com.microservice.Servicio_TeamReadbull.model;

import java.util.ArrayList;
import java.util.List;

import com.microservice.Servicio_TeamReadbull.model.Notification.ObservableSubject;
import com.microservice.Servicio_TeamReadbull.model.Notification.Observer;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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

    @ElementCollection
    private List<Long> requests = new ArrayList<>();

    @Transient
    private List<Observer> subscribers = new ArrayList<>();

    @Transient
    private final int maxPlayers = 12;

    @Transient
    private final int minPlayers = 7;

    // Constructores
    public Team() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getIdTournament() { return idTournament; }
    public void setIdTournament(Long idTournament) { this.idTournament = idTournament; }

    public Long getIdCaptain() { return idCaptain; }
    public void setIdCaptain(Long idCaptain) { this.idCaptain = idCaptain; }

    public TournamentStatus getTournamentStatus() { return tournamentStatus; }
    public void setTournamentStatus(TournamentStatus tournamentStatus) { this.tournamentStatus = tournamentStatus; }

    public List<Long> getPlayers() { return players; }
    public void setPlayers(List<Long> players) { this.players = players; }

    public int getCurrentPlayers() { return currentPlayers; }
    public void setCurrentPlayers(int currentPlayers) { this.currentPlayers = currentPlayers; }

    public List<Long> getRequests() { return requests; }
    public void setRequests(List<Long> requests) { this.requests = requests; }

    public int getMaxPlayers() { return maxPlayers; }
    public int getMinPlayers() { return minPlayers; }

    // Observer pattern
    @Override
    public void subscribe(Observer observer) { subscribers.add(observer); }

    @Override
    public void unsubscribe(Observer observer) { subscribers.remove(observer); }

    @Override
    public void notifyObservers() { subscribers.forEach(Observer::update); }

    @Override
    public void update() { notifyObservers(); }

    // Lógica de negocio
    public boolean isInActiveTournament() {
        return tournamentStatus == TournamentStatus.ACTIVE
                || tournamentStatus == TournamentStatus.IN_PROGRESS;
    }

    public void addPlayer(Long playerId) {
        if (currentPlayers >= maxPlayers) {
            throw new IllegalStateException("El equipo está lleno, máximo " + maxPlayers + " jugadores.");
        }
        players.add(playerId);
        currentPlayers++;
    }

    public void removePlayer(Long playerId) {
        if (playerId.equals(idCaptain)) {
            throw new IllegalStateException("No se puede eliminar al capitán del equipo.");
        }
        if (!players.remove(playerId)) {
            throw new IllegalStateException("Jugador no encontrado en el equipo.");
        }
        currentPlayers--;
    }

    public boolean hasValidNumberOfPlayers() {
        return currentPlayers >= minPlayers && currentPlayers <= maxPlayers;
    }

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
}