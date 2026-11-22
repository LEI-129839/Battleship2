package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários da classe Move")
class MoveTest {

    @Test
    @DisplayName("Construtor, getters e toString devem devolver os valores corretos")
    void testGettersAndToString() {
        List<IPosition> shots = List.of(new Position(0, 0), new Position(1, 1));
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(1, shots, results);

        assertEquals(1, move.getNumber());
        assertEquals(shots, move.getShots());
        assertEquals(results, move.getShotResults());
        assertEquals("Move{number=1, shots=2, results=2}", move.toString());
    }

    @Test
    @DisplayName("processEnemyFire com verbose=false deve gerar JSON correto sem imprimir resumo")
    void testProcessEnemyFireNonVerbose() {
        IShip barca = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
        IShip fragata = Ship.buildShip("fragata", Compass.NORTH, new Position(2, 2));

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, barca, true),
                new IGame.ShotResult(true, false, fragata, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(1, Collections.emptyList(), results);
        String json = move.processEnemyFire(false);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 3"));
        assertTrue(json.contains("\"missedShots\" : 1"));
        assertTrue(json.contains("\"repeatedShots\" : 0"));
        assertTrue(json.contains("\"outsideShots\" : 0"));
        assertTrue(json.contains("\"sunkBoats\""));
        assertTrue(json.contains("\"hitsOnBoats\""));
    }

    @Test
    @DisplayName("processEnemyFire verbose com apenas 1 tiro repetido e tiros exteriores")
    void testProcessEnemyFireSingleRepeatedAndOutside() {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(false, false, null, false),
                new IGame.ShotResult(false, false, null, false)
        );

        Move move = new Move(2, Collections.emptyList(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"validShots\" : 0"));
        assertTrue(json.contains("\"repeatedShots\" : 1"));
        assertTrue(json.contains("\"outsideShots\" : 2"));
    }

    @Test
    @DisplayName("processEnemyFire verbose com múltiplos tiros repetidos e 1 tiro exterior")
    void testProcessEnemyFireMultipleRepeatedAndSingleOutside() {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(false, false, null, false)
        );

        Move move = new Move(3, Collections.emptyList(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"validShots\" : 0"));
        assertTrue(json.contains("\"repeatedShots\" : 2"));
        assertTrue(json.contains("\"outsideShots\" : 1"));
    }

    @Test
    @DisplayName("processEnemyFire verbose com todos os tiros inválidos (exteriores)")
    void testProcessEnemyFireAllInvalid() {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(false, false, null, false),
                new IGame.ShotResult(false, false, null, false),
                new IGame.ShotResult(false, false, null, false)
        );

        Move move = new Move(4, Collections.emptyList(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"validShots\" : 0"));
        assertTrue(json.contains("\"outsideShots\" : 3"));
    }

    @Test
    @DisplayName("processEnemyFire verbose com 1 tiro válido na água e 1 tiro repetido")
    void testProcessEnemyFireSingleMissedAndSingleRepeated() {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(false, false, null, false)
        );

        Move move = new Move(5, Collections.emptyList(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"validShots\" : 1"));
        assertTrue(json.contains("\"missedShots\" : 1"));
        assertTrue(json.contains("\"repeatedShots\" : 1"));
    }

    @Test
    @DisplayName("processEnemyFire verbose com múltiplos tiros na água")
    void testProcessEnemyFireMultipleMissed() {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(6, Collections.emptyList(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"validShots\" : 3"));
        assertTrue(json.contains("\"missedShots\" : 3"));
        assertTrue(json.contains("\"outsideShots\" : 0"));
    }

    @Test
    @DisplayName("processEnemyFire verbose com 1 barco afundado e 1 acerto noutro barco sem água")
    void testProcessEnemyFireSingleSunkAndSingleHitNoMiss() {
        IShip barca = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
        IShip fragata = Ship.buildShip("fragata", Compass.NORTH, new Position(2, 2));

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, barca, true),
                new IGame.ShotResult(true, false, fragata, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(7, Collections.emptyList(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"validShots\" : 2"));
        assertTrue(json.contains("\"repeatedShots\" : 1"));
        assertTrue(json.contains("\"missedShots\" : 0"));
    }

    @Test
    @DisplayName("processEnemyFire verbose com múltiplos barcos do mesmo tipo afundados e múltiplos acertos")
    void testProcessEnemyFireMultipleSunkAndMultipleHits() {
        IShip barca1 = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
        IShip barca2 = Ship.buildShip("barca", Compass.NORTH, new Position(0, 2));
        IShip fragata = Ship.buildShip("fragata", Compass.NORTH, new Position(2, 2));

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, barca1, true),
                new IGame.ShotResult(true, false, barca2, true),
                new IGame.ShotResult(true, false, fragata, false),
                new IGame.ShotResult(true, false, fragata, false),
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(8, Collections.emptyList(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"validShots\" : 4"));
        assertTrue(json.contains("\"repeatedShots\" : 2"));
    }
}