package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
/**
 * Test class for Position.
 * Author: britoeabreu
 * Date: 2024-03-19 15:30
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getRow: 1
 * - getColumn: 1
 * - isValid: 4
 * - isAdjacentTo: 4
 * - isOccupied: 1
 * - isHit: 1
 * - occupy: 1
 * - shoot: 1
 * - equals: 3
 * - hashCode: 1
 * - toString: 1
 */
public class PositionTest {
	private Position position;

	@BeforeEach
	void setUp() {
		position = new Position(2, 3);
	//	position = new Position('C', 4);
	}

	@AfterEach
	void tearDown() {
		position = null;
	}

	@Test
	void constructor() {
		Position pos = new Position(1, 1);
		assertNotNull(pos, "Failed to create Position: object is null");
		assertEquals(1, pos.getRow(), "Failed to set row: expected 1 but got " + pos.getRow());
		assertEquals(1, pos.getColumn(), "Failed to set column: expected 1 but got " + pos.getColumn());
		assertFalse(pos.isOccupied(), "New position should not be occupied");
		assertFalse(pos.isHit(), "New position should not be hit");
	}

	@Test
	void getRow() {
		assertEquals(2, position.getRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	void getColumn() {
		assertEquals(3, position.getColumn(), "Failed to get column: expected 3 but got " + position.getColumn());
	}

	@Test
	void getClassicRow() {
		assertEquals('C', position.getClassicRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	@DisplayName("getClassicColumn devolve a coluna a começar em 1")
	void getClassicColumn() {
		assertEquals(4, position.getClassicColumn(), "Failed to get classic column: expected 4 but got " + position.getClassicColumn());
	}

	@Test
	void isValid1() {
		position = new Position(0, 0);
		assertTrue(position.isInside(), "Position (0,0) should be valid");
	}

	@Test
	void isValid2() {
		position = new Position(-1, 5);
		assertFalse(position.isInside(), "Position with negative row should be invalid");
	}

	@Test
	void isValid3() {
		position = new Position(5, -1);
		assertFalse(position.isInside(), "Position with negative column should be invalid");
	}

	@Test
	void isValid4() {
		position = new Position(Game.BOARD_SIZE, 5);
		assertFalse(position.isInside(), "Position with row >= BOARD_SIZE should be invalid");
	}

	@Test
	void isValid5() {
		position = new Position(5, Game.BOARD_SIZE);
		assertFalse(position.isInside(), "Position with column >= BOARD_SIZE should be invalid");
	}

	@Test
	void isAdjacentTo1() {
		Position other = new Position(2, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect horizontally adjacent position");
	}

	@Test
	void isAdjacentTo2() {
		Position other = new Position(3, 3);
		assertTrue(position.isAdjacentTo(other), "Failed to detect vertically adjacent position");
	}

	@Test
	void isAdjacentTo3() {
		Position other = new Position(3, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect diagonally adjacent position");
	}

	@Test
	void isAdjacentTo4() {
		Position other = new Position(4, 5);
		assertFalse(position.isAdjacentTo(other), "Non-adjacent position incorrectly identified as adjacent");
	}

	@Test
	void isAdjacentToWithNull() {
		assertThrows(NullPointerException.class, () -> position.isAdjacentTo(null),
				"isAdjacentTo should throw NullPointerException for null input");
	}

	@Test
	void isOccupied() {
		assertFalse(position.isOccupied(), "New position should not be occupied");
		position.occupy();
		assertTrue(position.isOccupied(), "Position should be occupied after occupy()");
	}

	@Test
	void isHit() {
		assertFalse(position.isHit(), "New position should not be hit");
		position.shoot();
		assertTrue(position.isHit(), "Position should be hit after shoot()");
	}

	@Test
	void equals1() {
		Position same = new Position(2, 3);
		assertTrue(position.equals(same), "Equal positions not identified as equal");
	}

	@Test
	void equals2() {
		assertFalse(position.equals(null), "Position should not equal null");
	}

	@Test
	void equals3() {
		Object other = new Object();
		assertFalse(position.equals(other), "Position should not equal non-Position object");
	}

	@Test
	void equals4() {
		Position other = new Position(2, 4);
		assertFalse(position.equals(other), "Positions with the same row but different column should not be equal");
	}

	@Test
	void equals5() {
		assertTrue(position.equals(position), "A position should be equal to itself");
	}

	@Test
	void hashCodeConsistency() {
		Position same = new Position(2, 3);
		assertEquals(position.hashCode(), same.hashCode(),
				"Hash codes not consistent for equal positions");
	}

	@Test
	void toStringFormat() {
//		String expected = "Row = C, Column = 4";
		String expected = "C4";
		assertEquals(expected, position.toString(),
				"Incorrect string representation: expected '" + expected +
						"' but got '" + position.toString() + "'");
	}
	@Test
	@DisplayName("Construtor clássico converte 'C',4 para linha 2 e coluna 3")
	void classicConstructor() {
		Position pos = new Position('C', 4);
		assertEquals(2, pos.getRow(), "Row should be 2 for 'C'");
		assertEquals(3, pos.getColumn(), "Column should be 3 for classic column 4");
		assertFalse(pos.isOccupied(), "New position should not be occupied");
		assertFalse(pos.isHit(), "New position should not be hit");
	}

	@Test
	@DisplayName("Construtor clássico aceita letra minúscula")
	void classicConstructorLowerCase() {
		Position pos = new Position('c', 4);
		assertEquals(new Position(2, 3), pos, "Lowercase 'c' should be the same as 'C'");
	}

	@Test
	@DisplayName("randomPosition gera sempre posições dentro do tabuleiro")
	void randomPositionIsInside() {
		for (int i = 0; i < 100; i++) {
			Position pos = Position.randomPosition();
			assertNotNull(pos, "Random position should not be null");
			assertTrue(pos.isInside(), "Random position should be inside the board: " + pos);
		}
	}

	@Test
	@DisplayName("Posição no meio do tabuleiro tem 8 adjacentes")
	void adjacentPositionsMiddle() {
		List<IPosition> adj = position.adjacentPositions(); // (2,3)
		assertEquals(8, adj.size(), "A middle position should have 8 adjacent positions");
		assertTrue(adj.contains(new Position(1, 3)), "Should contain the position above");
		assertTrue(adj.contains(new Position(3, 4)), "Should contain the diagonal position");
		assertFalse(adj.contains(position), "Should not contain itself");
	}

	@Test
	@DisplayName("Posição no canto do tabuleiro só tem 3 adjacentes")
	void adjacentPositionsCorner() {
		List<IPosition> adj = new Position(0, 0).adjacentPositions();
		assertEquals(3, adj.size(), "A corner position should have only 3 adjacent positions");
		assertTrue(adj.contains(new Position(0, 1)));
		assertTrue(adj.contains(new Position(1, 0)));
		assertTrue(adj.contains(new Position(1, 1)));
	}
	@Test
	@DisplayName("Mesma linha mas coluna afastada não é adjacente")
	void isAdjacentToSameRowFarColumn() {
		Position other = new Position(2, 6);
		assertFalse(position.isAdjacentTo(other), "Position in same row but 3 columns away should not be adjacent");
	}
}