package restserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários dos DTOs do protocolo REST (classe Dtos).
 * Autor: 129839
 */
class DtosTest {

    @Test
    @DisplayName("A classe contentora Dtos pode ser instanciada")
    void testDtosContainer() {
        assertNotNull(new Dtos());
    }

    @Test
    @DisplayName("RegistrationRequest e RegistrationResponse guardam os campos do registo (m0)")
    void testRegistration() {
        Dtos.RegistrationRequest req = new Dtos.RegistrationRequest();
        req.playerName = "Afonso";
        req.callbackUrl = "http://localhost:9090";
        assertEquals("Afonso", req.playerName);
        assertEquals("http://localhost:9090", req.callbackUrl);

        Dtos.RegistrationResponse res = new Dtos.RegistrationResponse();
        res.gameId = "id";
        res.status = "REGISTERED";
        res.shotsPerTurn = 3;
        res.message = "ok";
        assertEquals("id", res.gameId);
        assertEquals("REGISTERED", res.status);
        assertEquals(3, res.shotsPerTurn);
        assertEquals("ok", res.message);
    }

    @Test
    @DisplayName("ShotRequest contém uma lista de ShotPosition em notação clássica")
    void testShotRequest() {
        Dtos.ShotPosition p = new Dtos.ShotPosition();
        p.row = "B";
        p.column = 3;
        Dtos.ShotRequest req = new Dtos.ShotRequest();
        req.shots = List.of(p);

        assertEquals(1, req.shots.size());
        assertEquals("B", req.shots.get(0).row);
        assertEquals(3, req.shots.get(0).column);
    }

    @Test
    @DisplayName("ShotResponse agrega os ShotResult e o estado do jogo")
    void testShotResponse() {
        Dtos.ShotResult r = new Dtos.ShotResult();
        r.row = "G";
        r.column = 7;
        r.outcome = "HIT";
        r.shipType = "caravela";

        Dtos.ShotResponse res = new Dtos.ShotResponse();
        res.results = List.of(r);
        res.shipsRemaining = 8;
        res.gameStatus = "ONGOING";

        assertEquals("HIT", res.results.get(0).outcome);
        assertEquals("caravela", res.results.get(0).shipType);
        assertEquals(8, res.shipsRemaining);
        assertEquals("ONGOING", res.gameStatus);
        assertNull(res.winner, "o vencedor só é preenchido no fim do jogo");
    }

    @Test
    @DisplayName("ErrorResponse guarda a mensagem passada no construtor")
    void testErrorResponse() {
        assertEquals("Game not found: abc", new Dtos.ErrorResponse("Game not found: abc").error);
    }
}
