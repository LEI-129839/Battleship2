package battleship;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Game.
 * Author: britoeabreu
 * Date: 2024-03-19
 * Time: 15:30
 * Cyclomatic Complexity for each method:
 * - Game (constructor): 1
 * - fire: 7
 * - getShots: 1
 * - getRepeatedShots: 1
 * - getInvalidShots: 1
 * - getHits: 1
 * - getSunkShips: 1
 * - getRemainingShips: 1
 * - validShot: 3
 * - repeatedShot: 2
 * - printBoard: 1
 * - printValidShots: 1
 * - printFleet: 1
 */
public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet()); // Assuming Fleet is a concrete implementation of IFleet
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	// ------------------------------------------------------------------
	// Métodos auxiliares (não são testes)
	// ------------------------------------------------------------------

	/**
	 * Dispara sobre todas as posições do tabuleiro exceto as últimas 'left'
	 * (por ordem linha a linha), em rajadas de 3 tiros.
	 * Se a última rajada não tiver 3 posições novas, repete a última posição.
	 */
	private static void shootAllExcept(Game g, int left) {
		List<IPosition> all = allPositions();
		List<IPosition> toShoot = all.subList(0, all.size() - left);
		for (int i = 0; i < toShoot.size(); i += 3) {
			List<IPosition> move = new ArrayList<>();
			for (int j = i; j < i + 3; j++)
				move.add(toShoot.get(Math.min(j, toShoot.size() - 1)));
			g.fireShots(move);
		}
	}

	/** Devolve as 100 posições do tabuleiro, linha a linha. */
	private static List<IPosition> allPositions() {
		List<IPosition> all = new ArrayList<>();
		for (int r = 0; r < Game.BOARD_SIZE; r++)
			for (int c = 0; c < Game.BOARD_SIZE; c++)
				all.add(new Position(r, c));
		return all;
	}

	/** Executa o código dado e devolve tudo o que ele escreveu no System.out. */
	private static String captureOutput(Runnable code) {
		PrintStream original = System.out;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		System.setOut(new PrintStream(buffer));
		try {
			code.run();
		} finally {
			System.setOut(original);
		}
		return buffer.toString();
	}

	/** Devolve os tiros da última jogada do inimigo. */
	private List<IPosition> lastAlienShots() {
		List<IMove> moves = game.getAlienMoves();
		return moves.get(moves.size() - 1).getShots();
	}

	// ------------------------------------------------------------------
	// Testes originais
	// ------------------------------------------------------------------

	@Test
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "Shots list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "Shots list should be empty upon initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	void fire2() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	void fire3() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	void repeatedShot1() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		Position position = new Position(2, 3);
		assertTrue(game.repeatedShot(position), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	void repeatedShot2() {
		Position position = new Position(2, 3);
		assertFalse(game.repeatedShot(position), "Position (2,3) should not be marked as repeated before firing.");
	}

	@Test
	void getAlienMoves() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		assertEquals(1, game.getAlienMoves().size(), "Shots list should contain one shot after firing once.");
	}

	@Test
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		fleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}

	// ------------------------------------------------------------------
	// Novos testes: getters
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Frota inimiga e jogadas próprias começam vazias")
	void gettersStartEmpty() {
		assertNotNull(game.getAlienFleet(), "Alien fleet should not be null");
		assertTrue(game.getAlienFleet().getShips().isEmpty(), "Alien fleet should start empty");
		assertNotNull(game.getMyMoves(), "My moves should not be null");
		assertTrue(game.getMyMoves().isEmpty(), "My moves should start empty");
	}

	// ------------------------------------------------------------------
	// Novos testes: fireSingleShot
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Tiro fora do tabuleiro é inválido")
	void fireSingleShotOutside() {
		IGame.ShotResult result = game.fireSingleShot(new Position(10, 0), false);
		assertFalse(result.valid(), "Shot outside the board should be invalid");
		assertEquals(1, game.getInvalidShots());
	}

	@Test
	@DisplayName("Tiro numa posição já disparada numa jogada anterior conta como repetido")
	void fireSingleShotRepeatedFromPreviousMove() {
		game.fireShots(List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5)));
		IGame.ShotResult result = game.fireSingleShot(new Position(2, 3), false);
		assertTrue(result.valid());
		assertTrue(result.repeated(), "Shot already fired in a previous move should be repeated");
		assertEquals(1, game.getRepeatedShots());
	}

	@Test
	@DisplayName("Tiro na água é válido mas não acerta em navio")
	void fireSingleShotWater() {
		IGame.ShotResult result = game.fireSingleShot(new Position(4, 4), false);
		assertTrue(result.valid());
		assertFalse(result.repeated());
		assertNull(result.ship(), "Shot in the water should not hit any ship");
		assertFalse(result.sunk());
		assertEquals(0, game.getHits());
	}

	@Test
	@DisplayName("Tiro que acerta numa fragata sem a afundar")
	void fireSingleShotHitNotSunk() {
		Ship frigate = new Frigate(Compass.EAST, new Position(5, 5));
		game.getMyFleet().addShip(frigate);

		IGame.ShotResult result = game.fireSingleShot(new Position(5, 6), false);
		assertTrue(result.valid());
		assertSame(frigate, result.ship(), "Shot should hit the frigate");
		assertFalse(result.sunk(), "Frigate should not sink with one hit");
		assertEquals(1, game.getHits());
		assertEquals(0, game.getSunkShips());
	}

	@Test
	@DisplayName("Tiro que acerta numa barca e a afunda")
	void fireSingleShotHitAndSink() {
		Ship barge = new Barge(Compass.NORTH, new Position(1, 1));
		game.getMyFleet().addShip(barge);

		IGame.ShotResult result = game.fireSingleShot(new Position(1, 1), false);
		assertSame(barge, result.ship());
		assertTrue(result.sunk(), "Barge should sink with one hit");
		assertEquals(1, game.getHits());
		assertEquals(1, game.getSunkShips());
		assertEquals(0, game.getRemainingShips());
	}

	// ------------------------------------------------------------------
	// Novos testes: fireShots
	// ------------------------------------------------------------------

	@Test
	@DisplayName("fireShots com número errado de tiros lança exceção")
	void fireShotsWrongNumber() {
		List<IPosition> twoShots = List.of(new Position(0, 0), new Position(0, 1));
		assertThrows(IllegalArgumentException.class, () -> game.fireShots(twoShots));
		assertTrue(game.getAlienMoves().isEmpty(), "No move should be registered");
	}

	@Test
	@DisplayName("Tiro repetido dentro da mesma rajada conta como repetido")
	void fireShotsRepeatedInSameMove() {
		game.fireShots(List.of(new Position(0, 0), new Position(0, 0), new Position(0, 1)));
		assertEquals(1, game.getRepeatedShots());
		assertEquals(1, game.getAlienMoves().size());
	}

	// ------------------------------------------------------------------
	// Novos testes: readEnemyFire
	// ------------------------------------------------------------------

	@Test
	@DisplayName("readEnemyFire aceita posições compactas (A1 B2 C3)")
	void readEnemyFireCompact() {
		String json = game.readEnemyFire(new Scanner("A1 B2 C3"));
		assertNotNull(json);
		assertTrue(json.contains("\"row\""), "JSON should contain the row field");
		assertEquals(List.of(new Position('A', 1), new Position('B', 2), new Position('C', 3)), lastAlienShots());
	}

	@Test
	@DisplayName("readEnemyFire aceita letra e número separados e minúsculas (a 1 b 2 c 3)")
	void readEnemyFireSeparated() {
		game.readEnemyFire(new Scanner("a 1 b 2 c 3"));
		assertEquals(List.of(new Position('A', 1), new Position('B', 2), new Position('C', 3)), lastAlienShots());
	}

	@Test
	@DisplayName("readEnemyFire ignora posições a mais depois das 3 primeiras")
	void readEnemyFireMoreThanThree() {
		game.readEnemyFire(new Scanner("A1 B2 C3 D4"));
		assertEquals(3, lastAlienShots().size());
		assertFalse(lastAlienShots().contains(new Position('D', 4)), "Fourth position should be ignored");
	}

	@Test
	@DisplayName("readEnemyFire com menos de 3 posições lança exceção")
	void readEnemyFireTooFew() {
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(new Scanner("A1 B2")));
		assertTrue(game.getAlienMoves().isEmpty());
	}

	@Test
	@DisplayName("readEnemyFire com letra sem número a seguir lança exceção")
	void readEnemyFireIncomplete() {
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(new Scanner("A B2 C3")));
	}

	// ------------------------------------------------------------------
	// Novos testes: randomEnemyFire
	// ------------------------------------------------------------------

	@Test
	@DisplayName("randomEnemyFire num tabuleiro vazio dispara 3 tiros diferentes dentro do tabuleiro")
	void randomEnemyFireEmptyBoard() {
		String json = game.randomEnemyFire();
		assertNotNull(json);
		assertEquals(1, game.getAlienMoves().size());
		List<IPosition> shots = lastAlienShots();
		assertEquals(3, shots.size());
		assertEquals(3, new HashSet<>(shots).size(), "The 3 shots should be different");
		shots.forEach(s -> assertTrue(s.isInside(), "Shot should be inside the board: " + s));
	}

	@Test
	@DisplayName("randomEnemyFire não dispara à volta de navios já afundados")
	void randomEnemyFireAvoidsSunkShipSurroundings() {
		Ship barge = new Barge(Compass.NORTH, new Position(0, 0));
		game.getMyFleet().addShip(barge);
		barge.sink();

		for (int i = 0; i < 20; i++) {
			game.randomEnemyFire();
			for (IPosition shot : lastAlienShots())
				assertFalse(barge.getAdjacentPositions().contains(shot),
						"Should not shoot next to a sunk ship: " + shot);
		}
	}

	@Test
	@DisplayName("randomEnemyFire com exatamente 3 posições livres dispara nessas 3")
	void randomEnemyFireThreeLeft() {
		// repetido várias vezes porque a escolha é aleatória
		for (int i = 0; i < 10; i++) {
			game = new Game(new Fleet());
			shootAllExcept(game, 3);
			game.randomEnemyFire();
			List<IPosition> expected = allPositions().subList(97, 100);
			assertEquals(new HashSet<>(expected), new HashSet<>(lastAlienShots()));
		}
	}

	@Test
	@DisplayName("randomEnemyFire com só 2 posições livres dispara nelas e repete uma")
	void randomEnemyFireTwoLeft() {
		for (int i = 0; i < 10; i++) {
			game = new Game(new Fleet());
			shootAllExcept(game, 2);
			game.randomEnemyFire();
			List<IPosition> expected = allPositions().subList(98, 100);
			assertEquals(3, lastAlienShots().size(), "There should still be 3 shots");
			assertEquals(new HashSet<>(expected), new HashSet<>(lastAlienShots()));
		}
	}

	@Test
	@DisplayName("randomEnemyFire com só 1 posição livre dispara 3 vezes nela")
	void randomEnemyFireOneLeft() {
		shootAllExcept(game, 1);
		game.randomEnemyFire();
		IPosition last = new Position(9, 9);
		assertEquals(List.of(last, last, last), lastAlienShots());
	}

	// ------------------------------------------------------------------
	// Novos testes: impressão do tabuleiro
	// ------------------------------------------------------------------

	@Test
	@DisplayName("printMyBoard mostra navios, tiros certeiros, tiros na água e legenda")
	void printMyBoardWithShotsAndLegend() {
		game.getMyFleet().addShip(new Barge(Compass.NORTH, new Position(1, 1)));
		game.getMyFleet().addShip(new Frigate(Compass.EAST, new Position(5, 5)));
		// afunda a barca, tiro ao lado dela e tiro na água
		game.fireShots(List.of(new Position(1, 1), new Position(0, 0), new Position(9, 9)));
		// tiro fora do tabuleiro, tiro na fragata e tiro repetido
		game.fireShots(List.of(new Position(-1, 0), new Position(5, 5), new Position(1, 1)));

		String out = captureOutput(() -> game.printMyBoard(true, true));

		assertTrue(out.contains("LEGENDA"), "Legend should be printed");
		assertTrue(out.contains(" A | o - - . . . . . . . |"), "Row A: water shot next to sunk barge");
		assertTrue(out.contains(" B | - * - . . . . . . . |"), "Row B: sunk barge");
		assertTrue(out.contains(" F | . . . . . * # # # . |"), "Row F: frigate with one hit");
		assertTrue(out.contains(" J | . . . . . . . . . o |"), "Row J: water shot");
	}

	@Test
	@DisplayName("printMyBoard sem tiros nem legenda mostra só os navios")
	void printMyBoardWithoutShotsAndLegend() {
		game.getMyFleet().addShip(new Barge(Compass.NORTH, new Position(1, 1)));
		game.fireShots(List.of(new Position(1, 1), new Position(0, 0), new Position(9, 9)));

		String out = captureOutput(() -> game.printMyBoard(false, false));

		assertFalse(out.contains("LEGENDA"), "Legend should not be printed");
		assertTrue(out.contains(" A | - - - . . . . . . . |"), "Row A: only adjacent markers, no shots");
		assertTrue(out.contains(" B | - # - . . . . . . . |"), "Row B: ship without shot marker");
	}

	@Test
	@DisplayName("printAlienBoard mostra um tabuleiro vazio")
	void printAlienBoardEmpty() {
		String out = captureOutput(() -> game.printAlienBoard(true, false));
		assertTrue(out.contains(" A | . . . . . . . . . . |"));
		assertFalse(out.contains("LEGENDA"));
	}

	@Test
	@DisplayName("over mostra a mensagem de fim de jogo")
	void overPrintsMessage() {
		String out = captureOutput(() -> game.over());
		assertTrue(out.contains("Java Sparrow"));
	}

	// ------------------------------------------------------------------
	// Novos testes: asserts com argumentos null
	// (o IntelliJ e o Maven correm os testes com asserts ativos, -ea)
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Métodos recusam argumentos null (assert)")
	void nullArgumentsAreRejected() {
		assertThrows(AssertionError.class, () -> Game.printBoard(null, new ArrayList<>(), true, true));
		assertThrows(AssertionError.class, () -> Game.printBoard(new Fleet(), null, true, true));
		assertThrows(AssertionError.class, () -> Game.jsonShots(null));
		assertThrows(AssertionError.class, () -> game.readEnemyFire(null));
		assertThrows(AssertionError.class, () -> game.fireShots(null));
		assertThrows(AssertionError.class, () -> game.fireSingleShot(null, false));
		assertThrows(AssertionError.class, () -> game.repeatedShot(null));
	}
}
