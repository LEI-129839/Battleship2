package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ScoreboardManager.
 * The manager uses the fixed path data/scoreboard.json, so each test backs up
 * the real file, starts from a clean state and restores the original afterwards.
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - loadScores (private): 3
 * - saveScores (private): 2
 * - registerWin: 4
 * - getSortedScores: 1
 */
class ScoreboardManagerTest {

    private static final Path DATA_DIR = Path.of("data");
    private static final Path SCORE_FILE = Path.of("data/scoreboard.json");

    private byte[] originalContent;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(DATA_DIR);
        originalContent = Files.isRegularFile(SCORE_FILE) ? Files.readAllBytes(SCORE_FILE) : null;
        deleteScoreFile();
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteScoreFile();
        if (originalContent != null)
            Files.write(SCORE_FILE, originalContent);
    }

    // removes the score file, or the directory used to force write errors
    private void deleteScoreFile() throws IOException {
        Files.deleteIfExists(SCORE_FILE);
    }

    @Test
    @DisplayName("Sem ficheiro de pontuações, a lista começa vazia")
    void noFileStartsEmpty() {
        ScoreboardManager manager = new ScoreboardManager();
        assertTrue(manager.getSortedScores().isEmpty(), "Error: scores should be empty without a file.");
    }

    @Test
    @DisplayName("registerWin cria um jogador novo com uma vitória")
    void registerWinNewPlayer() {
        ScoreboardManager manager = new ScoreboardManager();
        manager.registerWin("Ana");
        List<PlayerScore> scores = manager.getSortedScores();
        assertEquals(1, scores.size(), "Error: there should be exactly one player.");
        assertEquals("Ana", scores.get(0).getPlayerName(), "Error: player name should be 'Ana'.");
        assertEquals(1, scores.get(0).getWins(), "Error: a new player should have 1 win.");
    }

    @Test
    @DisplayName("registerWin acumula vitórias de um jogador que já existe")
    void registerWinExistingPlayer() {
        ScoreboardManager manager = new ScoreboardManager();
        manager.registerWin("Ana");
        manager.registerWin("Ana");
        List<PlayerScore> scores = manager.getSortedScores();
        assertEquals(1, scores.size(), "Error: the same player should not be duplicated.");
        assertEquals(2, scores.get(0).getWins(), "Error: wins should be 2.");
    }

    @Test
    @DisplayName("registerWin encontra o jogador mesmo quando não é o primeiro da lista")
    void registerWinPlayerNotFirst() {
        ScoreboardManager manager = new ScoreboardManager();
        manager.registerWin("Ana");
        manager.registerWin("Rui");
        manager.registerWin("Rui");
        List<PlayerScore> scores = manager.getSortedScores();
        assertEquals(2, scores.size(), "Error: there should be two players.");
        assertEquals("Rui", scores.get(0).getPlayerName(), "Error: 'Rui' should be first with 2 wins.");
        assertEquals(2, scores.get(0).getWins(), "Error: 'Rui' should have 2 wins.");
    }

    @Test
    @DisplayName("getSortedScores ordena do maior para o menor número de vitórias")
    void getSortedScoresDescending() {
        ScoreboardManager manager = new ScoreboardManager();
        manager.registerWin("Ana");
        manager.registerWin("Rui");
        manager.registerWin("Rui");
        manager.registerWin("Rui");
        manager.registerWin("Eva");
        manager.registerWin("Eva");
        List<PlayerScore> scores = manager.getSortedScores();
        assertEquals("Rui", scores.get(0).getPlayerName(), "Error: first should be 'Rui'.");
        assertEquals("Eva", scores.get(1).getPlayerName(), "Error: second should be 'Eva'.");
        assertEquals("Ana", scores.get(2).getPlayerName(), "Error: third should be 'Ana'.");
    }

    @Test
    @DisplayName("As pontuações guardadas são lidas por um novo ScoreboardManager")
    void scoresArePersisted() {
        new ScoreboardManager().registerWin("Ana");
        ScoreboardManager reloaded = new ScoreboardManager();
        List<PlayerScore> scores = reloaded.getSortedScores();
        assertEquals(1, scores.size(), "Error: the saved player should be loaded.");
        assertEquals("Ana", scores.get(0).getPlayerName(), "Error: loaded name should be 'Ana'.");
        assertEquals(1, scores.get(0).getWins(), "Error: loaded wins should be 1.");
    }

    @Test
    @DisplayName("Lê pontuações de um ficheiro JSON existente")
    void loadsExistingFile() throws IOException {
        Files.writeString(SCORE_FILE, "[{\"playerName\":\"Eva\",\"wins\":5}]", StandardCharsets.UTF_8);
        ScoreboardManager manager = new ScoreboardManager();
        List<PlayerScore> scores = manager.getSortedScores();
        assertEquals(1, scores.size(), "Error: one player should be loaded.");
        assertEquals("Eva", scores.get(0).getPlayerName(), "Error: loaded name should be 'Eva'.");
        assertEquals(5, scores.get(0).getWins(), "Error: loaded wins should be 5.");
    }

    @Test
    @DisplayName("Ficheiro corrompido resulta numa lista vazia, sem lançar exceção")
    void corruptedFileStartsEmpty() throws IOException {
        Files.writeString(SCORE_FILE, "isto nao e json", StandardCharsets.UTF_8);
        ScoreboardManager manager = assertDoesNotThrow(ScoreboardManager::new,
                "Error: a corrupted file should not throw.");
        assertTrue(manager.getSortedScores().isEmpty(), "Error: scores should be empty for a corrupted file.");
    }

    @Test
    @DisplayName("Falha ao guardar não lança exceção e mantém a pontuação em memória")
    void saveFailureIsHandled() throws IOException {
        // a directory with the file's name makes both reading and writing fail with IOException
        Files.createDirectory(SCORE_FILE);
        ScoreboardManager manager = assertDoesNotThrow(ScoreboardManager::new,
                "Error: a read failure should not throw.");
        assertDoesNotThrow(() -> manager.registerWin("Ana"), "Error: a write failure should not throw.");
        assertEquals(1, manager.getSortedScores().get(0).getWins(),
                "Error: the win should still be kept in memory.");
    }
}