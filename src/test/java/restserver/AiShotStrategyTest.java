package restserver;

import battleship.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários da classe AiShotStrategy (estratégia Hunt and Target).
 * Autor: 129839
 */
class AiShotStrategyTest {

    private AiShotStrategy strategy;
    private IGame game;
    private IShip barca;

    @BeforeEach
    void setUp() {
        strategy = new AiShotStrategy();
        // o jogo vem de uma GameSession, tal como acontece no servidor REST
        game = new GameSession("jogo-ia", "Aluno", "http://localhost:9090").getGame();
        barca = new Barge(Compass.NORTH, new Position(5, 5));
    }

    /** Regista na lista de jogadas da IA uma jogada com os tiros e resultados indicados. */
    private void addAlienMove(List<IPosition> shots, List<IGame.ShotResult> results) {
        game.getAlienMoves().add(new Move(game.getAlienMoves().size() + 1, shots, results));
    }

    private static IGame.ShotResult hit(IShip ship) {
        return new IGame.ShotResult(true, false, ship, false);
    }

    private static IGame.ShotResult miss() {
        return new IGame.ShotResult(true, false, null, false);
    }

    /** Marca como já disparadas todas as casas do tabuleiro, exceto as indicadas. */
    private void shootEverythingExcept(Set<IPosition> free) {
        List<IPosition> shots = new ArrayList<>();
        for (int r = 0; r < Game.BOARD_SIZE; r++)
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                Position p = new Position(r, c);
                if (!free.contains(p))
                    shots.add(p);
            }
        addAlienMove(shots, new ArrayList<>());
    }

    private static boolean isCheckerboard(IPosition p) {
        return (p.getRow() + p.getColumn()) % 2 == 0;
    }

    @Test
    @DisplayName("Sem jogadas anteriores devolve 3 tiros distintos do padrão em xadrez")
    void testHuntPhaseWithoutHistory() {
        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(Game.NUMBER_SHOTS, shots.size());
        assertEquals(Game.NUMBER_SHOTS, new HashSet<>(shots).size(), "os tiros não se podem repetir");
        assertTrue(shots.stream().allMatch(AiShotStrategyTest::isCheckerboard));
        assertTrue(shots.stream().allMatch(IPosition::isInside));
    }

    @Test
    @DisplayName("Depois de um acerto sem afundar, ataca as casas vizinhas desse acerto")
    void testTargetPhaseAfterHit() {
        Position alvo = new Position(5, 5);
        addAlienMove(List.of(alvo), List.of(hit(barca)));

        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(Game.NUMBER_SHOTS, shots.size());
        assertTrue(shots.stream().allMatch(alvo::isAdjacentTo));
        assertFalse(shots.contains(alvo), "não volta a disparar na casa já atingida");
    }

    @Test
    @DisplayName("Um navio já afundado não gera alvos, voltando à fase de caça")
    void testSunkShipIsNotATarget() {
        addAlienMove(List.of(new Position(0, 1)), List.of(new IGame.ShotResult(true, false, barca, true)));

        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(Game.NUMBER_SHOTS, shots.size());
        assertTrue(shots.stream().allMatch(AiShotStrategyTest::isCheckerboard));
        assertFalse(shots.contains(new Position(0, 1)));
    }

    @Test
    @DisplayName("Tiros inválidos, repetidos ou na água não são considerados acertos")
    void testInvalidRepeatedAndMissedShotsAreIgnored() {
        addAlienMove(
                List.of(new Position(0, 1), new Position(2, 3), new Position(4, 5)),
                List.of(new IGame.ShotResult(false, false, barca, false),
                        new IGame.ShotResult(true, true, barca, false),
                        miss()));

        List<IPosition> shots = strategy.computeShots(game);

        assertTrue(shots.stream().allMatch(AiShotStrategyTest::isCheckerboard),
                "sem acertos válidos a IA continua em modo de caça");
    }

    @Test
    @DisplayName("Jogadas sem resultados (ou com menos resultados do que tiros) não rebentam")
    void testMovesWithMissingResults() {
        addAlienMove(List.of(new Position(1, 0)), new ArrayList<>());
        addAlienMove(List.of(new Position(5, 5), new Position(3, 0)), List.of(hit(barca)));

        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(Game.NUMBER_SHOTS, shots.size());
        assertTrue(shots.stream().allMatch(new Position(5, 5)::isAdjacentTo),
                "só o tiro com resultado (5,5) conta como alvo");
    }

    @Test
    @DisplayName("Com vários acertos, para de procurar vizinhos assim que tem os 3 tiros")
    void testTargetPhaseStopsWhenFull() {
        // (0,0) tem exatamente 3 vizinhos: enche a jogada e o segundo acerto já não é usado
        addAlienMove(List.of(new Position(0, 0), new Position(7, 7)), List.of(hit(barca), hit(barca)));

        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(Set.of(new Position(0, 1), new Position(1, 0), new Position(1, 1)), new HashSet<>(shots));
    }

    @Test
    @DisplayName("Vizinhos já disparados ou já escolhidos não são repetidos")
    void testNeighboursAlreadyShotOrChosen() {
        // (0,1) e (1,0) já foram disparados, por isso o acerto (0,0) só dá (1,1);
        // o primeiro vizinho do acerto (2,1) é também (1,1), que já está escolhido
        addAlienMove(List.of(new Position(0, 1), new Position(1, 0)), List.of(miss(), miss()));
        addAlienMove(List.of(new Position(0, 0), new Position(2, 1)), List.of(hit(barca), hit(barca)));

        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(List.of(new Position(1, 1), new Position(2, 2), new Position(3, 1)), shots);
    }

    @Test
    @DisplayName("Se os vizinhos não chegam, completa a jogada com tiros de caça")
    void testTargetPhaseCompletedWithHunt() {
        // (0,0) só tem (1,1) livre: os outros 2 tiros vêm do padrão em xadrez
        addAlienMove(List.of(new Position(0, 1), new Position(1, 0)), List.of(miss(), miss()));
        addAlienMove(List.of(new Position(0, 0)), List.of(hit(barca)));

        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(Game.NUMBER_SHOTS, new HashSet<>(shots).size());
        assertEquals(new Position(1, 1), shots.get(0));
        assertTrue(shots.stream().allMatch(AiShotStrategyTest::isCheckerboard));
    }

    @Test
    @DisplayName("Quando o padrão em xadrez se esgota usa as restantes casas livres")
    void testFallbackWhenHuntPoolIsExhausted() {
        // Livres: 1 casa do xadrez (9,9) e 3 fora dele
        Set<IPosition> free = Set.of(new Position(9, 9), new Position(0, 1), new Position(9, 8), new Position(8, 9));
        shootEverythingExcept(free);

        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(Game.NUMBER_SHOTS, new HashSet<>(shots).size());
        assertEquals(new Position(9, 9), shots.get(0), "primeiro esgota o padrão em xadrez");
        assertTrue(free.containsAll(shots));
        assertEquals(new Position(0, 1), shots.get(1), "o fallback percorre o tabuleiro por ordem");
    }

    @Test
    @DisplayName("O fallback salta casas que a fase de caça já escolheu")
    void testFallbackSkipsCellsChosenByHunt() {
        // (0,0) é do xadrez e é escolhida na caça; o fallback começa em (0,0) e tem de a saltar
        shootEverythingExcept(Set.of(new Position(0, 0), new Position(0, 1), new Position(9, 8)));

        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(List.of(new Position(0, 0), new Position(0, 1), new Position(9, 8)), shots);
    }

    @Test
    @DisplayName("Com menos casas livres do que tiros devolve só as que restam")
    void testAlmostFullBoard() {
        shootEverythingExcept(Set.of(new Position(9, 8)));

        List<IPosition> shots = strategy.computeShots(game);

        assertEquals(List.of(new Position(9, 8)), shots);
    }

    @Test
    @DisplayName("Com o tabuleiro todo disparado devolve uma lista vazia")
    void testFullBoard() {
        shootEverythingExcept(Set.of());

        assertTrue(strategy.computeShots(game).isEmpty());
    }
}
