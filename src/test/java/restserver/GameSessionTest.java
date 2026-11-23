package restserver;

import battleship.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários da classe GameSession.
 * Autor: 129839
 */
class GameSessionTest {

    private GameSession session;

    @BeforeEach
    void setUp() {
        session = new GameSession("jogo-1", "Afonso", "http://localhost:9090");
    }

    @Test
    @DisplayName("O construtor guarda o id, o nome do jogador e o URL de callback")
    void testConstructorStoresData() {
        assertEquals("jogo-1", session.getGameId());
        assertEquals("Afonso", session.getPlayerName());
        assertEquals("http://localhost:9090", session.getCallbackUrl());
    }

    @Test
    @DisplayName("Uma sessão nova tem um jogo com frota aleatória e 3 tiros por turno")
    void testNewSessionGame() {
        assertNotNull(session.getGame());
        assertEquals(Game.NUMBER_SHOTS, session.getShotsPerTurn());
        assertTrue(session.getGame().getMyFleet().getShips().size() > 0,
                "a frota da IA é colocada logo na criação da sessão");
        assertTrue(session.getGame().getAlienMoves().isEmpty());
    }

    @Test
    @DisplayName("Uma sessão nova ainda não terminou e não tem vencedor")
    void testNewSessionNotOver() {
        assertFalse(session.isGameOver());
        assertNull(session.getWinner());
    }

    @Test
    @DisplayName("markAiWins termina o jogo com vitória da IA")
    void testMarkAiWins() {
        session.markAiWins();
        assertTrue(session.isGameOver());
        assertEquals("AI_WINS", session.getWinner());
    }

    @Test
    @DisplayName("markStudentWins termina o jogo com vitória do aluno")
    void testMarkStudentWins() {
        session.markStudentWins();
        assertTrue(session.isGameOver());
        assertEquals("STUDENT_WINS", session.getWinner());
    }

    @Test
    @DisplayName("Duas sessões têm jogos independentes")
    void testSessionsAreIndependent() {
        GameSession outra = new GameSession("jogo-2", "Outro", "http://localhost:9191");
        assertNotSame(session.getGame(), outra.getGame());
        outra.markAiWins();
        assertFalse(session.isGameOver());
    }
}
