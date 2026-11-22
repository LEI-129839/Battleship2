package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

@DisplayName("Testes unitários da classe Fleet")
public class FleetTest {

	private Fleet fleet;

	@BeforeEach
	void setUp() {
		fleet = new Fleet();
	}

	@AfterEach
	void tearDown() {
		fleet = null;
	}

	@Test
	@DisplayName("Construtor deve inicializar a frota com lista de navios vazia")
	void testConstructor() {
		assertNotNull(fleet, "Error: Instance of Fleet should not be null.");
		assertTrue(fleet.getShips().isEmpty(), "Error: Fleet should be initialized with empty ships list.");
	}

	@Test
	@DisplayName("createRandom deve criar uma frota válida com o tamanho esperado")
	void testCreateRandom() {
		IFleet r = Fleet.createRandom();
		assertNotNull(r, "Error: createRandom should return a non-null fleet instance.");
		assertEquals(Fleet.FLEET_SIZE, r.getShips().size(), "Error: createRandom should create a fleet with the correct number of ships.");
	}

	@Test
	@DisplayName("addShip deve adicionar navio válido dentro do tabuleiro e sem colisão")
	void testAddShip1() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		assertTrue(fleet.addShip(ship), "Error: Valid ship should be added successfully.");
		assertEquals(1, fleet.getShips().size(), "Error: Fleet should contain one ship after addition.");
	}

	@Test
	@DisplayName("addShip deve devolver false quando o limite máximo da frota é ultrapassado")
	void testAddShip2() {
		// Colocar barcas espaçadas de 2 em 2 para evitar colisões e encher até FLEET_SIZE + 1
		int added = 0;
		for (int row = 0; row <= 8 && added <= Fleet.FLEET_SIZE; row += 2) {
			for (int col = 0; col <= 8 && added <= Fleet.FLEET_SIZE; col += 2) {
				assertTrue(fleet.addShip(new Barge(Compass.NORTH, new Position(row, col))));
				added++;
			}
		}
		IShip anotherShip = new Barge(Compass.NORTH, new Position(8, 8));
		assertFalse(fleet.addShip(anotherShip), "Error: Should not add ship when fleet size limit is reached.");
	}

	@Test
	@DisplayName("addShip deve devolver false quando o navio está fora do tabuleiro")
	void testAddShip3() {
		IShip shipOutside = new Barge(Compass.NORTH, new Position(99, 99));
		assertFalse(fleet.addShip(shipOutside), "Error: Should not add ship outside the board.");
	}

	@Test
	@DisplayName("addShip deve devolver false quando há risco de colisão com outro navio")
	void testAddShip4() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship1);
		assertFalse(fleet.addShip(ship2), "Error: Should not add ship with a collision risk.");
	}

	@Test
	@DisplayName("getShips deve devolver a lista atual de navios da frota")
	void testGetShips() {
		assertTrue(fleet.getShips().isEmpty(), "Error: Fleet's ships list should initially be empty.");
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		assertEquals(1, fleet.getShips().size(), "Error: Fleet should have size 1 after adding a ship.");
		assertEquals(ship, fleet.getShips().get(0), "Error: Fleet's first ship should match the added ship.");
	}

	@Test
	@DisplayName("getShipsLike deve devolver apenas os navios da categoria indicada")
	void testGetShipsLike() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		List<IShip> barges = fleet.getShipsLike("Barca");
		assertEquals(1, barges.size(), "Error: There should be exactly one ship of category 'Barca'.");
		assertEquals(ship1, barges.get(0), "Error: The ship of category 'Barca' does not match.");
	}

	@Test
	@DisplayName("getFloatingShips deve devolver apenas os navios que ainda flutuam")
	void testGetFloatingShips() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		List<IShip> floatingShips = fleet.getFloatingShips();
		assertEquals(2, floatingShips.size(), "Error: All ships should be floating initially.");

		ship1.getPositions().get(0).shoot();
		floatingShips = fleet.getFloatingShips();
		assertEquals(1, floatingShips.size(), "Error: Only one ship should be floating after sinking one.");
		assertEquals(ship2, floatingShips.get(0), "Error: The floating ship should match the expected result.");
	}

	@Test
	@DisplayName("getSunkShips deve devolver apenas os navios afundados")
	void testGetSunkShips() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		assertTrue(fleet.getSunkShips().isEmpty(), "Error: No ships should be sunk initially.");

		ship1.getPositions().get(0).shoot();
		List<IShip> sunkShips = fleet.getSunkShips();
		assertEquals(1, sunkShips.size(), "Error: Exactly one ship should be sunk.");
		assertEquals(ship1, sunkShips.get(0), "Error: The sunk ship should be ship1.");
	}

	@Test
	@DisplayName("shipAt deve devolver o navio na posição ocupada ou null se estiver vazia")
	void testShipAt() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		assertEquals(ship, fleet.shipAt(new Position(1, 1)), "Error: Should return the correct ship at the position.");
		assertNull(fleet.shipAt(new Position(5, 5)), "Error: Should return null for empty positions in the fleet.");
	}

	@Test
	@DisplayName("colisionRisk deve detetar navios sobrepostos e ignorar navios afastados")
	void testColisionRisk() throws Exception {
		var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
		method.setAccessible(true);

		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship1);

		assertTrue((Boolean) method.invoke(fleet, ship2), "Error: Overlapping ships should be at collision risk.");
		assertFalse((Boolean) method.invoke(fleet, new Barge(Compass.NORTH, new Position(5, 5))),
				"Error: Ships at non-overlapping positions should not have a collision risk.");
	}

	@Test
	@DisplayName("printStatus deve imprimir o estado da frota sem lançar exceções")
	void testPrintStatus() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		assertDoesNotThrow(fleet::printStatus, "Error: printStatus should not throw any exceptions.");
	}

	@Test
	@DisplayName("printShips, printShipsByCategory, printFloatingShips e printAllShips devem executar sem exceções")
	void testPrintMethods() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		assertDoesNotThrow(() -> fleet.printShips(Collections.emptyList()));
		assertDoesNotThrow(() -> fleet.printShips(fleet.getShips()));
		assertDoesNotThrow(() -> fleet.printShipsByCategory("Barca"));
		assertDoesNotThrow(fleet::printFloatingShips);
		assertDoesNotThrow(fleet::printAllShips);
	}

	@Test
	@DisplayName("Métodos com assert devem lançar AssertionError quando recebem null")
	void testNullAssertions() {
		assertThrows(AssertionError.class, () -> fleet.addShip(null));
		assertThrows(AssertionError.class, () -> fleet.getShipsLike(null));
		assertThrows(AssertionError.class, () -> fleet.shipAt(null));
		assertThrows(AssertionError.class, () -> fleet.printShips(null));
		assertThrows(AssertionError.class, () -> fleet.printShipsByCategory(null));
	}
}