package com.microservice.Servicio_TeamReadbull.model;

import com.microservice.Servicio_TeamReadbull.model.Notification.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TeamModelTest {

    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Test FC");
        team.setIdCaptain(10L);
        team.setTournamentStatus(Team.TournamentStatus.NONE);
        team.setColors("Rojo");
        team.setPhoto("foto.png");
        team.setPlayers(new ArrayList<>(List.of(10L)));
        team.setCurrentPlayers(1);
        team.setRequests(new ArrayList<>());
    }

    // =========================================================
    // isInActiveTournament
    // =========================================================
    @Nested
    @DisplayName("isInActiveTournament")
    class IsInActiveTournament {

        @Test
        @DisplayName("Retorna true cuando status es ACTIVE")
        void returnsTrueWhenActive() {
            team.setTournamentStatus(Team.TournamentStatus.ACTIVE);
            assertTrue(team.isInActiveTournament());
        }

        @Test
        @DisplayName("Retorna true cuando status es IN_PROGRESS")
        void returnsTrueWhenInProgress() {
            team.setTournamentStatus(Team.TournamentStatus.IN_PROGRESS);
            assertTrue(team.isInActiveTournament());
        }

        @Test
        @DisplayName("Retorna false cuando status es NONE")
        void returnsFalseWhenNone() {
            team.setTournamentStatus(Team.TournamentStatus.NONE);
            assertFalse(team.isInActiveTournament());
        }

        @Test
        @DisplayName("Retorna false cuando status es DRAFT")
        void returnsFalseWhenDraft() {
            team.setTournamentStatus(Team.TournamentStatus.DRAFT);
            assertFalse(team.isInActiveTournament());
        }

        @Test
        @DisplayName("Retorna false cuando status es FINISHED")
        void returnsFalseWhenFinished() {
            team.setTournamentStatus(Team.TournamentStatus.FINISHED);
            assertFalse(team.isInActiveTournament());
        }
    }

    // =========================================================
    // hasValidNumberOfPlayers
    // =========================================================
    @Nested
    @DisplayName("hasValidNumberOfPlayers")
    class HasValidNumberOfPlayers {

        @Test
        @DisplayName("Retorna false con menos de 7 jugadores")
        void falseWhenBelowMin() {
            team.setCurrentPlayers(5);
            assertFalse(team.hasValidNumberOfPlayers());
        }

        @Test
        @DisplayName("Retorna true con exactamente 7 jugadores (mínimo)")
        void trueWhenAtMinimum() {
            team.setCurrentPlayers(7);
            assertTrue(team.hasValidNumberOfPlayers());
        }

        @Test
        @DisplayName("Retorna true con exactamente 12 jugadores (máximo)")
        void trueWhenAtMaximum() {
            team.setCurrentPlayers(12);
            assertTrue(team.hasValidNumberOfPlayers());
        }

        @Test
        @DisplayName("Retorna false con más de 12 jugadores")
        void falseWhenAboveMax() {
            team.setCurrentPlayers(13);
            assertFalse(team.hasValidNumberOfPlayers());
        }

        @Test
        @DisplayName("Retorna true con 10 jugadores (valor intermedio)")
        void trueWithTenPlayers() {
            team.setCurrentPlayers(10);
            assertTrue(team.hasValidNumberOfPlayers());
        }
    }

    // =========================================================
    // addPlayer
    // =========================================================
    @Nested
    @DisplayName("addPlayer")
    class AddPlayer {

        @Test
        @DisplayName("Agrega jugador correctamente cuando hay espacio")
        void addPlayer_success() {
            team.setPlayers(new ArrayList<>(List.of(10L)));
            team.setCurrentPlayers(1);

            team.addPlayer(20L);

            assertThat(team.getPlayers()).contains(20L);
            assertEquals(2, team.getCurrentPlayers());
        }

        @Test
        @DisplayName("Lanza IllegalStateException cuando equipo tiene 12 jugadores")
        void addPlayer_fullTeam_throwsException() {
            List<Long> fullPlayers = new ArrayList<>();
            for (long i = 1; i <= 12; i++) fullPlayers.add(i);
            team.setPlayers(fullPlayers);
            team.setCurrentPlayers(12);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> team.addPlayer(99L));

            assertThat(ex.getMessage()).contains("lleno");
        }
    }

    // =========================================================
    // removePlayer
    // =========================================================
    @Nested
    @DisplayName("removePlayer")
    class RemovePlayer {

        @Test
        @DisplayName("Elimina jugador correctamente")
        void removePlayer_success() {
            team.setPlayers(new ArrayList<>(List.of(10L, 20L, 30L)));
            team.setCurrentPlayers(3);

            team.removePlayer(20L);

            assertThat(team.getPlayers()).doesNotContain(20L);
            assertEquals(2, team.getCurrentPlayers());
        }

        @Test
        @DisplayName("Lanza IllegalStateException al intentar eliminar al capitán")
        void removePlayer_captain_throwsException() {
            team.setPlayers(new ArrayList<>(List.of(10L, 20L)));
            team.setCurrentPlayers(2);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> team.removePlayer(10L)); // 10L es el capitán

            assertThat(ex.getMessage()).contains("capitán");
        }

        @Test
        @DisplayName("Lanza IllegalStateException si jugador no está en el equipo")
        void removePlayer_playerNotFound_throwsException() {
            team.setPlayers(new ArrayList<>(List.of(10L, 20L)));
            team.setCurrentPlayers(2);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> team.removePlayer(99L)); // no existe

            assertThat(ex.getMessage()).contains("no encontrado");
        }
    }

    // =========================================================
    // validateTeam
    // =========================================================
    @Nested
    @DisplayName("validateTeam")
    class ValidateTeam {

        @Test
        @DisplayName("Equipo válido con jugadores entre 7 y 12")
        void validateTeam_valid() {
            team.setCurrentPlayers(8);
            team.validateTeam();
            assertTrue(team.isValidTeam());
        }

        @Test
        @DisplayName("Equipo inválido con menos de 7 jugadores")
        void validateTeam_tooFewPlayers() {
            team.setCurrentPlayers(4);
            team.validateTeam();
            assertFalse(team.isValidTeam());
        }
    }

    // =========================================================
    // Notification pattern
    // =========================================================
    @Nested
    @DisplayName("Observer pattern")
    class ObserverPattern {

        @Test
        @DisplayName("subscribe agrega observador")
        void subscribe_addsObserver() {
            Observer mockObserver = () -> {};
            team.subscribe(mockObserver);
            assertThat(team.getSubscribers()).contains(mockObserver);
        }

        @Test
        @DisplayName("unsubscribe elimina observador")
        void unsubscribe_removesObserver() {
            Observer mockObserver = () -> {};
            team.subscribe(mockObserver);
            team.unsubscribe(mockObserver);
            assertThat(team.getSubscribers()).doesNotContain(mockObserver);
        }

        @Test
        @DisplayName("notifyObservers llama update en cada suscriptor")
        void notifyObservers_callsUpdate() {
            int[] counter = {0};
            Observer obs = () -> counter[0]++;
            team.subscribe(obs);

            team.notifyObservers();

            assertEquals(1, counter[0]);
        }

        @Test
        @DisplayName("update delega a notifyObservers")
        void update_delegatesToNotifyObservers() {
            int[] counter = {0};
            Observer obs = () -> counter[0]++;
            team.subscribe(obs);

            team.update();

            assertEquals(1, counter[0]);
        }
    }

    // =========================================================
    // equals / hashCode
    // =========================================================
    @Nested
    @DisplayName("equals y hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("Mismo objeto es igual a sí mismo")
        void equals_sameObject() {
            assertEquals(team, team);
        }

        @Test
        @DisplayName("Dos teams con mismo ID son iguales")
        void equals_sameId() {
            Team other = new Team();
            other.setId(1L);
            assertEquals(team, other);
        }

        @Test
        @DisplayName("Team con ID null no es igual a team con ID")
        void equals_nullId() {
            Team noId = new Team();
            assertNotEquals(team, noId);
        }

        @Test
        @DisplayName("No es igual a null")
        void equals_null() {
            assertNotEquals(team, null);
        }

        @Test
        @DisplayName("No es igual a objeto de otra clase")
        void equals_differentClass() {
            assertNotEquals(team, "string");
        }

        @Test
        @DisplayName("hashCode basado en ID")
        void hashCode_basedOnId() {
            Team other = new Team();
            other.setId(1L);
            assertEquals(team.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("hashCode de team sin ID es 0")
        void hashCode_nullId() {
            Team noId = new Team();
            assertEquals(0, noId.hashCode());
        }
    }

    // =========================================================
    // playersWithDiferentDorsal y halfOfStudentsAreOfAllowedPrograms
    // =========================================================
    @Test
    @DisplayName("playersWithDiferentDorsal retorna true")
    void playersWithDifferentDorsal_returnsTrue() {
        assertTrue(team.playersWithDiferentDorsal());
    }

    @Test
    @DisplayName("halfOfStudentsAreOfAllowedPrograms retorna true")
    void halfOfStudentsAreOfAllowedPrograms_returnsTrue() {
        assertTrue(team.halfOfStudentsAreOfAllowedPrograms());
    }
    @Test
    @DisplayName("generateCode genera código cuando es null")
    void generateCode_whenCodeIsNull_generatesCode() {
        Team newTeam = new Team();
        newTeam.setColors("Rojo");
        newTeam.setPhoto("foto.png");
        newTeam.setName("Test FC");
        newTeam.setIdCaptain(1L);
        newTeam.setPlayers(new ArrayList<>());
        newTeam.setCurrentPlayers(0);
        // Llamar directamente via reflexión
        try {
            java.lang.reflect.Method method = Team.class.getDeclaredMethod("generateCode");
            method.setAccessible(true);
            method.invoke(newTeam);
            assertNotNull(newTeam.getCode());
            assertEquals(8, newTeam.getCode().length());
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("generateCode no sobreescribe código existente")
    void generateCode_whenCodeExists_doesNotOverwrite() {
        Team newTeam = new Team();
        newTeam.setColors("Rojo");
        newTeam.setPhoto("foto.png");
        newTeam.setName("Test FC");
        newTeam.setIdCaptain(1L);
        newTeam.setPlayers(new ArrayList<>());
        newTeam.setCurrentPlayers(0);
        try {
            java.lang.reflect.Method method = Team.class.getDeclaredMethod("generateCode");
            method.setAccessible(true);
            // Primero generamos el código
            method.invoke(newTeam);
            String firstCode = newTeam.getCode();
            // Volvemos a llamar — no debe sobreescribir
            method.invoke(newTeam);
            assertEquals(firstCode, newTeam.getCode());
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("validateTeam con jugadores válidos y dorsales y programas correctos")
    void validateTeam_allConditionsTrue() {
        team.setCurrentPlayers(8);
        team.validateTeam();
        assertTrue(team.isValidTeam());
    }

    @Test
    @DisplayName("equals retorna false cuando se compara con null")
    void equals_withNull_returnsFalse() {
        assertNotEquals(null, team);
    }
}
