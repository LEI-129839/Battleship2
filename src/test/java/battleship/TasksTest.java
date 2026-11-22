package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários da classe Tasks")
class TasksTest {

	private InputStream originalIn;

	@BeforeEach
	void setUp() {
		originalIn = System.in;
	}

	@AfterEach
	void tearDown() {
		System.setIn(originalIn);
	}

	@Test
	@DisplayName("menuHelp deve executar sem lançar exceções")
	void testMenuHelp() {
		assertDoesNotThrow(Tasks::menuHelp);
	}

	@Test
	@DisplayName("readPosition deve ler linha e coluna corretamente e validar assert")
	void testReadPosition() {
		Scanner sc = new Scanner("3 5");
		Position pos = Tasks.readPosition(sc);
		assertEquals(new Position(3, 5), pos);

		assertThrows(AssertionError.class, () -> Tasks.readPosition(null));
	}

	@Test
	@DisplayName("readShip deve criar navio válido, devolver null para tipo desconhecido e validar assert")
	void testReadShip() {
		Scanner scValid = new Scanner("barca 1 2 n");
		Ship ship = Tasks.readShip(scValid);
		assertNotNull(ship);
		assertEquals("Barca", ship.getCategory());
		assertEquals(new Position(1, 2), ship.getPosition());

		Scanner scUnknown = new Scanner("submarino 1 2 n");
		assertNull(Tasks.readShip(scUnknown));

		assertThrows(AssertionError.class, () -> Tasks.readShip(null));
	}

	@Test
	@DisplayName("readClassicPosition deve aceitar formatos compactos, com espaço e minúsculas, e rejeitar inválidos")
	void testReadClassicPosition() {
		// Formato compacto maiúsculo
		IPosition p1 = Tasks.readClassicPosition(new Scanner("A3"));
		assertNotNull(p1);

		// Formato compacto minúsculo
		IPosition p2 = Tasks.readClassicPosition(new Scanner("b5"));
		assertNotNull(p2);

		// Formato com espaço entre letra e número
		IPosition p3 = Tasks.readClassicPosition(new Scanner("C 4"));
		assertNotNull(p3);

		// Sem tokens disponíveis
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner("")));

		// Formato inválido
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner("123")));
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner("AA")));
	}

	@Test
	@DisplayName("buildFleet deve construir frota completa ignorando navios desconhecidos e colisões")
	void testBuildFleet() {
		String input = String.join("\n",
				"submarino 0 0 n", // navio desconhecido (s == null)
				"barca 0 0 n",     // válido #1
				"barca 0 0 n",     // colisão (success == false)
				"barca 0 2 n",     // válido #2
				"barca 0 4 n",     // válido #3
				"barca 0 6 n",     // válido #4
				"barca 0 8 n",     // válido #5
				"barca 2 0 n",     // válido #6
				"barca 2 2 n",     // válido #7
				"barca 2 4 n",     // válido #8
				"barca 2 6 n",     // válido #9
				"barca 2 8 n",     // válido #10
				"barca 4 0 n"      // válido #11
		);

		Fleet fleet = Tasks.buildFleet(new Scanner(input));
		assertNotNull(fleet);
		assertEquals(Fleet.FLEET_SIZE, fleet.getShips().size());

		assertThrows(AssertionError.class, () -> Tasks.buildFleet(null));
	}

	@Test
	@DisplayName("menu deve processar todos os comandos sem frota e com frota criada até desistir")
	void testMenuCommands() {
		String commands = String.join("\n",
				// Comandos antes de existir frota/jogo (testa ramos null)
				"estado",
				"mapa",
				"rajada",
				"simula",
				"tiros",
				"ajuda",
				"scores",
				"comando_invalido",
				// Criar frota aleatória e testar comandos com frota/jogo ativos
				"gerafrota",
				"estado",
				"mapa",
				"tiros",
				// Carregar frota manualmente via lefrota
				"lefrota",
				"barca 0 0 n",
				"barca 0 2 n",
				"barca 0 4 n",
				"barca 0 6 n",
				"barca 0 8 n",
				"barca 2 0 n",
				"barca 2 2 n",
				"barca 2 4 n",
				"barca 2 6 n",
				"barca 2 8 n",
				"barca 4 0 n",
				// Terminar
				"desisto"
		) + "\n";

		System.setIn(new ByteArrayInputStream(commands.getBytes()));
		assertDoesNotThrow(Tasks::menu);
	}
}