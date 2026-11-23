package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários do método estático IMove.readMove.
 * Autor: 129839
 */
class IMoveTest {

    @Test
    @DisplayName("readMove lê o número de tiros e as coordenadas de cada tiro")
    void testReadMoveWithShots() {
        Move move = IMove.readMove(4, new Scanner("3 1 2 3 4 9 9"));

        assertEquals(4, move.getNumber());
        assertEquals(List.of(new Position(1, 2), new Position(3, 4), new Position(9, 9)), move.getShots());
        assertTrue(move.getShotResults().isEmpty(), "uma jogada lida ainda não tem resultados");
    }

    @Test
    @DisplayName("readMove com zero tiros devolve uma jogada sem tiros")
    void testReadMoveWithoutShots() {
        Move move = IMove.readMove(1, new Scanner("0"));

        assertEquals(1, move.getNumber());
        assertTrue(move.getShots().isEmpty());
    }

    @Test
    @DisplayName("readMove só consome os valores da jogada, deixando o resto no Scanner")
    void testReadMoveLeavesRemainingInput() {
        Scanner sc = new Scanner("1 5 6 2 0 0 1 1");
        Move primeira = IMove.readMove(1, sc);
        Move segunda = IMove.readMove(2, sc);

        assertEquals(List.of(new Position(5, 6)), primeira.getShots());
        assertEquals(List.of(new Position(0, 0), new Position(1, 1)), segunda.getShots());
    }
}
