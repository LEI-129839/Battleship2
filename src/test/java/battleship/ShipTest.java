package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Ship.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getCategory: 1
 * - getSize: 1
 * - getBearing: 1
 * - getPositions: 1
 * - stillFloating: 2
 * - shoot: 2
 * - occupies: 2
 * - tooCloseTo (IShip): 2
 * - tooCloseTo (IPosition): 2
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 */
public class ShipTest {

    private Ship ship, shipBorder;

    @BeforeEach
    void setUp() {
        // Since Ship is abstract, instantiate it with a concrete subclass (e.g., Barge)
        ship = new Barge(Compass.NORTH, new Position(5, 5));
        shipBorder = new Barge(Compass.NORTH, new Position(0, 0));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    /**
     * Test for the constructor.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testConstructor() {
        assertNotNull(ship, "Error: Ship instance should not be null.");
        assertEquals("Barca", ship.getCategory(), "Error: Ship category is incorrect.");
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing is incorrect.");
        assertEquals(1, ship.getSize(), "Error: Ship size is incorrect.");
        assertFalse(ship.getPositions().isEmpty(), "Error: Ship positions should not be empty.");
    }

    /**
     * Test for the getCategory method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetCategory() {
        assertEquals("Barca", ship.getCategory(), "Error: Ship category should be 'Barca'.");
    }

    /**
     * Test for the getSize method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetSize() {
        assertEquals(1, ship.getSize(), "Error: Ship size should be 1.");
    }

    /**
     * Test for the getBearing method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing should be NORTH.");
    }

    /**
     * Test for the getPositions method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetPositions() {
        List<IPosition> positions = ship.getPositions();
        assertNotNull(positions, "Error: Ship positions should not be null.");
        assertEquals(1, positions.size(), "Error: Ship should have exactly one position.");
        assertEquals(5, positions.get(0).getRow(), "Error: Position's row should be 5.");
        assertEquals(5, positions.get(0).getColumn(), "Error: Position's column should be 5.");
    }

    /**
     * Test for the stillFloating method (all positions intact).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testStillFloating1() {
        assertTrue(ship.stillFloating(), "Error: Ship should still be floating.");
    }

    /**
     * Test for the stillFloating method (all positions hit).
     */
    @Test
    void testStillFloating2() {
        ship.getPositions().get(0).shoot();
        assertFalse(ship.stillFloating(), "Error: Ship should no longer be floating after being hit.");
    }

    /**
     * Test for the shoot method (valid position).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testShoot1() {
        Position target = new Position(5, 5);
        ship.shoot(target);
        assertTrue(ship.getPositions().get(0).isHit(), "Error: Position should be marked as hit.");
    }

    /**
     * Test for the shoot method (invalid position).
     */
    @Test
    void testShoot2() {
        Position target = new Position(0, 0);
        ship.shoot(target); // No exception expected
        assertFalse(ship.getPositions().get(0).isHit(), "Error: Position should not be marked as hit for an invalid target.");
    }

    /**
     * Test for the occupies method (position occupied).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testOccupies1() {
        Position pos = new Position(5, 5);
        assertTrue(ship.occupies(pos), "Error: Ship should occupy position (5, 5).");
    }

    /**
     * Test for the occupies method (position not occupied).
     */
    @Test
    void testOccupies2() {
        Position pos = new Position(1, 1);
        assertFalse(ship.occupies(pos), "Error: Ship should not occupy position (1, 1).");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships too close).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToShip1() {
        Ship nearbyShip = new Barge(Compass.NORTH, new Position(5, 6));
        assertTrue(ship.tooCloseTo(nearbyShip), "Error: Ships should be too close.");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships not close).
     */
    @Test
    void testTooCloseToShip2() {
        Ship farShip = new Barge(Compass.NORTH, new Position(10, 10));
        assertFalse(ship.tooCloseTo(farShip), "Error: Ships should not be too close.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions adjacent).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToPosition1() {
        Position pos = new Position(5, 6); // Adjacent position
        assertTrue(ship.tooCloseTo(pos), "Error: Ship should be too close to the given position.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions not adjacent).
     */
    @Test
    void testTooCloseToPosition2() {
        Position pos = new Position(7, 7); // Non-adjacent position
        assertFalse(ship.tooCloseTo(pos), "Error: Ship should not be too close to the given position.");
    }

    /**
     * Test for the getTopMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetTopMostPos() {
        assertEquals(5, ship.getTopMostPos(), "Error: The topmost position should be 5.");
    }

    /**
     * Test for the getBottomMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetBottomMostPos() {
        assertEquals(5, ship.getBottomMostPos(), "Error: The bottommost position should be 5.");
    }

    /**
     * Test for the getLeftMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetLeftMostPos() {
        assertEquals(5, ship.getLeftMostPos(), "Error: The leftmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetRightMostPos() {
        assertEquals(5, ship.getRightMostPos(), "Error: The rightmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetAdjacentPositions() {
        assertEquals(8, ship.getAdjacentPositions().size(), "Error: The number of adjacent positions of a barge in the middle of the tray should be 8.");
        assertEquals(3, shipBorder.getAdjacentPositions().size(), "Error: The number of adjacent positions of a barge in the middle of the tray should be 8.");
    }

    // ------------------------------------------------------------------
    // Testes adicionais para maximizar a cobertura de linhas e ramos (129839)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("buildShip cria o tipo de navio certo para cada categoria")
    void testBuildShipAllKinds() {
        Position p = new Position(2, 2);
        assertInstanceOf(Barge.class, Ship.buildShip("barca", Compass.NORTH, p));
        assertInstanceOf(Caravel.class, Ship.buildShip("caravela", Compass.NORTH, p));
        assertInstanceOf(Carrack.class, Ship.buildShip("nau", Compass.NORTH, p));
        assertInstanceOf(Frigate.class, Ship.buildShip("fragata", Compass.NORTH, p));
        assertInstanceOf(Galleon.class, Ship.buildShip("galeao", Compass.NORTH, p));
    }

    @Test
    @DisplayName("buildShip devolve null para uma categoria desconhecida")
    void testBuildShipUnknownKind() {
        assertNull(Ship.buildShip("submarino", Compass.NORTH, new Position(2, 2)));
    }

    @Test
    @DisplayName("buildShip falha a asserção quando algum argumento é null")
    void testBuildShipNullArguments() {
        Position p = new Position(2, 2);
        assertThrows(AssertionError.class, () -> Ship.buildShip(null, Compass.NORTH, p));
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", null, p));
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", Compass.NORTH, null));
    }

    @Test
    @DisplayName("O construtor rejeita categoria, orientação ou posição null")
    void testConstructorNullArguments() {
        Position p = new Position(2, 2);
        assertThrows(NullPointerException.class, () -> new Ship(null, Compass.NORTH, p, 1) { });
        assertThrows(NullPointerException.class, () -> new Ship("Barca", null, p, 1) { });
        assertThrows(NullPointerException.class, () -> new Ship("Barca", Compass.NORTH, null, 1) { });
    }

    @Test
    @DisplayName("getPosition devolve a posição inicial do navio")
    void testGetPosition() {
        assertEquals(new Position(5, 5), ship.getPosition());
    }

    @Test
    @DisplayName("Um navio maior continua a flutuar enquanto houver uma posição intacta")
    void testStillFloatingPartiallyHit() {
        Ship nau = new Carrack(Compass.SOUTH, new Position(2, 2));
        nau.shoot(new Position(2, 2));
        nau.shoot(new Position(3, 2));
        assertTrue(nau.stillFloating());
        nau.shoot(new Position(4, 2));
        assertFalse(nau.stillFloating());
    }

    @Test
    @DisplayName("Os extremos de um galeão são calculados percorrendo todas as posições")
    void testExtremesOfMultiPositionShip() {
        // Galeão a SUL em (4,4): ocupa (4,4),(5,4),(6,3),(6,4),(6,5)
        Ship galeao = new Galleon(Compass.SOUTH, new Position(4, 4));
        assertEquals(4, galeao.getTopMostPos());
        assertEquals(6, galeao.getBottomMostPos());
        assertEquals(3, galeao.getLeftMostPos());
        assertEquals(5, galeao.getRightMostPos());
    }

    @Test
    @DisplayName("Os extremos são calculados quando a posição mais extrema não é a primeira")
    void testExtremesWhenFirstPositionIsNotExtreme() {
        // Galeão a ESTE em (4,4): ocupa (4,4),(5,2),(5,3),(5,4),(6,4)
        Ship galeao = new Galleon(Compass.EAST, new Position(4, 4));
        assertEquals(4, galeao.getTopMostPos());
        assertEquals(6, galeao.getBottomMostPos());
        assertEquals(2, galeao.getLeftMostPos());
        assertEquals(4, galeao.getRightMostPos());
    }

    @Test
    @DisplayName("occupies encontra posições que não são a primeira do navio")
    void testOccupiesLaterPosition() {
        Ship nau = new Carrack(Compass.EAST, new Position(1, 1));
        assertTrue(nau.occupies(new Position(1, 3)));
        assertFalse(nau.occupies(new Position(2, 3)));
    }

    @Test
    @DisplayName("occupies e tooCloseTo falham a asserção com argumento null")
    void testNullArgumentAssertions() {
        assertThrows(AssertionError.class, () -> ship.occupies(null));
        assertThrows(AssertionError.class, () -> ship.tooCloseTo((IShip) null));
        assertThrows(AssertionError.class, () -> ship.tooCloseTo((IPosition) null));
        assertThrows(AssertionError.class, () -> ship.shoot(null));
    }

    @Test
    @DisplayName("shoot falha a asserção para uma posição fora do tabuleiro")
    void testShootOutsideBoard() {
        assertThrows(AssertionError.class, () -> ship.shoot(new Position(-1, 3)));
    }

    @Test
    @DisplayName("shoot num navio grande acerta só na posição indicada")
    void testShootMultiPositionShip() {
        Ship nau = new Carrack(Compass.EAST, new Position(1, 1));
        nau.shoot(new Position(1, 2));
        assertFalse(nau.getPositions().get(0).isHit());
        assertTrue(nau.getPositions().get(1).isHit());
        assertFalse(nau.getPositions().get(2).isHit());
    }

    @Test
    @DisplayName("tooCloseTo(IShip) verifica todas as posições do outro navio")
    void testTooCloseToShipCheckingSeveralPositions() {
        Ship nau = new Carrack(Compass.EAST, new Position(7, 2));
        // a nau em (7,2)-(7,4) fica a 2 linhas da barca em (5,5)
        assertFalse(ship.tooCloseTo(nau));
        Ship nau2 = new Carrack(Compass.EAST, new Position(6, 2));
        // só a última posição, (6,4), é adjacente a (5,5)
        assertTrue(ship.tooCloseTo(nau2));
    }

    @Test
    @DisplayName("getAdjacentPositions não repete posições nem inclui as do próprio navio")
    void testGetAdjacentPositionsMultiPositionShip() {
        Ship caravela = new Caravel(Compass.EAST, new Position(5, 5));
        List<IPosition> adj = caravela.getAdjacentPositions();
        assertEquals(10, adj.size());
        assertFalse(adj.contains(new Position(5, 5)));
        assertFalse(adj.contains(new Position(5, 6)));
        assertEquals(adj.size(), adj.stream().distinct().count());
    }

    @Test
    @DisplayName("isInsideBoard é verdadeiro para um navio dentro do tabuleiro")
    void testIsInsideBoardTrue() {
        assertTrue(ship.isInsideBoard());
        assertTrue(new Carrack(Compass.EAST, new Position(9, 7)).isInsideBoard());
    }

    @Test
    @DisplayName("isInsideBoard é falso quando o navio sai por qualquer um dos lados")
    void testIsInsideBoardFalseOnEachSide() {
        assertFalse(new Barge(Compass.NORTH, new Position(3, -1)).isInsideBoard(), "esquerda");
        assertFalse(new Carrack(Compass.EAST, new Position(3, 8)).isInsideBoard(), "direita");
        assertFalse(new Barge(Compass.NORTH, new Position(-1, 3)).isInsideBoard(), "cima");
        assertFalse(new Carrack(Compass.SOUTH, new Position(8, 3)).isInsideBoard(), "baixo");
    }

    @Test
    @DisplayName("sink acerta em todas as posições do navio")
    void testSink() {
        Ship nau = new Carrack(Compass.SOUTH, new Position(0, 0));
        nau.sink();
        assertTrue(nau.getPositions().stream().allMatch(IPosition::isHit));
        assertFalse(nau.stillFloating());
    }

    @Test
    @DisplayName("getTopMostPos encontra a linha mínima mesmo que não seja a da primeira posição")
    void testTopMostPosNotFirst() {
        Ship custom = new Ship("Teste", Compass.NORTH, new Position(5, 5), 2) { };
        custom.getPositions().add(new Position(5, 5));
        custom.getPositions().add(new Position(4, 5));
        assertEquals(4, custom.getTopMostPos());
        assertEquals(5, custom.getBottomMostPos());
    }

    @Test
    @DisplayName("toString mostra categoria, orientação e posição")
    void testToString() {
        assertEquals("[Barca n " + new Position(5, 5) + "]", ship.toString());
    }
}
