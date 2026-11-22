package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PlayerScore.
 * Cyclomatic Complexity for each method:
 * - Constructor (empty): 1
 * - Constructor (name, wins): 1
 * - getPlayerName: 1
 * - setPlayerName: 1
 * - getWins: 1
 * - setWins: 1
 * - addWin: 1
 * - toString: 1
 */
class PlayerScoreTest {

    private PlayerScore score;

    @BeforeEach
    void setUp() {
        score = new PlayerScore("Ana", 3);
    }

    @AfterEach
    void tearDown() {
        score = null;
    }

    @Test
    @DisplayName("Construtor vazio deixa o nome a null e as vitórias a 0")
    void emptyConstructor() {
        PlayerScore empty = new PlayerScore();
        assertNull(empty.getPlayerName(), "Error: player name should be null.");
        assertEquals(0, empty.getWins(), "Error: wins should be 0.");
    }

    @Test
    @DisplayName("Construtor com argumentos guarda o nome e as vitórias")
    void constructorWithArguments() {
        PlayerScore other = new PlayerScore("Rui", 7);
        assertEquals("Rui", other.getPlayerName(), "Error: player name should be 'Rui'.");
        assertEquals(7, other.getWins(), "Error: wins should be 7.");
    }

    @Test
    @DisplayName("getPlayerName devolve o nome passado ao construtor")
    void getPlayerName() {
        assertEquals("Ana", score.getPlayerName(), "Error: player name should be 'Ana'.");
    }

    @Test
    @DisplayName("setPlayerName altera o nome do jogador")
    void setPlayerName() {
        score.setPlayerName("Rui");
        assertEquals("Rui", score.getPlayerName(), "Error: player name should be 'Rui' after set.");
    }

    @Test
    @DisplayName("getWins devolve as vitórias passadas ao construtor")
    void getWins() {
        assertEquals(3, score.getWins(), "Error: wins should be 3.");
    }

    @Test
    @DisplayName("setWins altera o número de vitórias")
    void setWins() {
        score.setWins(10);
        assertEquals(10, score.getWins(), "Error: wins should be 10 after set.");
    }

    @Test
    @DisplayName("addWin incrementa as vitórias em uma unidade de cada vez")
    void addWin() {
        score.addWin();
        assertEquals(4, score.getWins(), "Error: wins should be 4 after one addWin.");
        score.addWin();
        assertEquals(5, score.getWins(), "Error: wins should be 5 after two addWin calls.");
    }

    @Test
    @DisplayName("toString devolve 'nome - Vitórias: N'")
    void testToString() {
        assertEquals("Ana - Vitórias: 3", score.toString(),
                "Error: toString should return 'Ana - Vitórias: 3'.");
    }
}