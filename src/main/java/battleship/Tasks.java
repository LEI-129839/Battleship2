package battleship;

import java.io.IOException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;


/**
 * The type Tasks.
 */
public class Tasks {
	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The constant GOODBYE_MESSAGE.
	 */
	private static final String GOODBYE_MESSAGE = "Bons ventos!";

	/**
	 * Strings to be used by the user
	 */
	private static final String AJUDA = "ajuda";
	private static final String GERAFROTA = "gerafrota";
	private static final String LEFROTA = "lefrota";
	private static final String DESISTIR = "desisto";
	private static final String RAJADA = "rajada";
	private static final String TIROS = "tiros";
	private static final String MAPA = "mapa";
	private static final String PDF = "pdf";
	private static final String FICHEIRO_PDF = "jogadas.pdf";
	private static final String STATUS = "estado";
	private static final String SIMULA = "simula";

	/**
	 * This task also tests the fighting element of a round of three shots
	 */
	public static void menu() {

		IFleet myFleet = null;
		IGame game = null;
		StopWatch gameClock = null;   // NOVO: relógio do jogo inteiro
		StopWatch moveClock;          // NOVO: relógio de cada jogada
		int moveNumber = 0;           // NOVO: número da jogada atual
		menuHelp();

		System.out.print("> ");
		Scanner in = new Scanner(System.in);
		moveClock = StopWatch.createStarted();   // NOVO: começa a contar a jogada
		String command = in.next();
		while (!command.equals(DESISTIR)) {

			switch (command) {
				case GERAFROTA:
					myFleet = Fleet.createRandom();
					game = new Game(myFleet);
					game.printMyBoard(false, true);
					gameClock = StopWatch.createStarted();   // NOVO: começa o relógio do jogo
					moveNumber = 0;                          // NOVO
					break;
				case LEFROTA:
					myFleet = buildFleet(in);
					game = new Game(myFleet);
					game.printMyBoard(false, true);
					gameClock = StopWatch.createStarted();   // NOVO
					moveNumber = 0;                          // NOVO
					break;
				case STATUS:
					if (myFleet != null)
						myFleet.printStatus();
					break;
				case MAPA:
					if (myFleet != null){
						game.printMyBoard(false, true);
						VisualizadorGrafico visualizador = new VisualizadorGrafico();
						visualizador.mostrarTabuleiro(game);
					}
					break;
				case RAJADA:
					if (game != null) {
						game.readEnemyFire(in);
						moveClock.stop();                    // NOVO: para o relógio da jogada
						moveNumber++;                        // NOVO
						System.out.println("Tempo da jogada " + moveNumber + ": "
								+ formatTime(moveClock.getTime()));   // NOVO
						myFleet.printStatus();
						game.printMyBoard(true, false);

						if (game.getRemainingShips() == 0) {

							exportarPDF(game);
							printTotalTime(gameClock, moveNumber);   // NOVO

							game.over();
							System.exit(0);
						}
					}
					break;
				case SIMULA:
					if (game != null) {
						while (game.getRemainingShips() > 0){
							game.randomEnemyFire();
							moveNumber++;                    // NOVO
							myFleet.printStatus();
							game.printMyBoard(true, false);
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt(); // Best practice: restore interrupt status
							}
						}

						if (game.getRemainingShips() == 0) {

							exportarPDF(game);

							printTotalTime(gameClock, moveNumber);   // NOVO

							game.over();
							System.exit(0);
						}
					}
					break;
				case TIROS:
					if (game != null)
						game.printMyBoard(true, true);
					break;

				case PDF:
					if (game != null)
						exportarPDF(game);
					break;
                case AJUDA:
                    menuHelp();
                    break;

				case AJUDA:
					menuHelp();
					break;

				default:
					System.out.println("Que comando é esse??? Repete ...");
			}
			System.out.print("> ");
			moveClock = StopWatch.createStarted();   // NOVO: recomeça para a próxima jogada
			command = in.next();
		}
		printTotalTime(gameClock, moveNumber);   // NOVO: mostra o total se o jogador desistir
		System.out.println(GOODBYE_MESSAGE);
	}
	/**
	 * Formata um tempo em milissegundos no formato mm:ss.SSS
	 *
	 * @param millis tempo em milissegundos
	 * @return o tempo formatado
	 */
	private static String formatTime(long millis) {
		return DurationFormatUtils.formatDuration(millis, "mm:ss.SSS");
	}

	/**
	 * Para o relógio do jogo e mostra o tempo total e o número de rajadas
	 *
	 * @param gameClock o relógio do jogo
	 * @param moves     o número de rajadas feitas
	 */
	private static void printTotalTime(StopWatch gameClock, int moves) {
		if (gameClock == null)
			return;
		gameClock.stop();
		System.out.println("Tempo total do jogo: " + formatTime(gameClock.getTime())
				+ " (" + moves + " rajadas)");
	}
	/**
	 * This function provides help information about the menu commands.
	 */
	public static void menuHelp() {
		System.out.println("======================= AJUDA DO MENU =========================");
		System.out.println("Digite um dos comandos abaixo para interagir com o jogo:");
		System.out.println("- " + GERAFROTA + ": Gera uma frota aleatória de navios.");
		System.out.println("- " + LEFROTA + ": Permite criar e carregar uma frota personalizada.");
		System.out.println("- " + STATUS + ": Mostra o status atual da frota.)");
		System.out.println("- " + MAPA + ": Exibe o mapa da frota.");
		System.out.println("- " + RAJADA + ": Realiza uma rajada de disparos.");
		System.out.println("- " + SIMULA + ": Simula um jogo completo.");
		System.out.println("- " + TIROS + ": Lista os tiros válidos realizados (* = tiro em navio, o = tiro na água)");
		System.out.println("- " + PDF + ": Exporta o histórico das jogadas para PDF.");
		System.out.println("- " + DESISTIR + ": Encerra o jogo.");
		System.out.println("===============================================================");
	}
	// exporta o histórico das jogadas para PDF (#5)
	private static void exportarPDF(IGame game) {
		try {
			ExportadorPDF.exportar(game, FICHEIRO_PDF);
			System.out.println("Jogadas exportadas para " + FICHEIRO_PDF);
		} catch (IOException e) {
			System.out.println("Não foi possível criar o PDF: " + e.getMessage());
		}
	}

	/**
	 * This operation allows the build up of a fleet, given user data
	 *
	 * @param in The scanner to read from
	 * @return The fleet that has been built
	 */
	public static Fleet buildFleet(Scanner in) {

		assert in != null;

		Fleet fleet = new Fleet();
		int i = 0; // i represents the total of successfully created ships
		while (i < Fleet.FLEET_SIZE) {
			IShip s = readShip(in);
			if (s != null) {
				boolean success = fleet.addShip(s);
				if (success)
					i++;
				else
					LOGGER.info("Falha na criacao de {} {} {}", s.getCategory(), s.getBearing(), s.getPosition());
			} else {
				LOGGER.info("Navio desconhecido!");
			}
		}
		LOGGER.info("{} navios adicionados com sucesso!", i);
		return fleet;
	}

	/**
	 * This operation reads data about a ship, build it and returns it
	 *
	 * @param in The scanner to read from
	 * @return The created ship based on the data that has been read
	 */
	public static Ship readShip(Scanner in) {

		assert in != null;

		String shipKind = in.next();
		Position pos = readPosition(in);
		char c = in.next().charAt(0);
		Compass bearing = Compass.charToCompass(c);
		return Ship.buildShip(shipKind, bearing, pos);
	}

	/**
	 * This operation allows reading a position in the map
	 *
	 * @param in The scanner to read from
	 * @return The position that has been read
	 */
	public static Position readPosition(Scanner in) {

		assert in != null;

		int row = in.nextInt();
		int column = in.nextInt();
		return new Position(row, column);
	}

	/**
	 * This operation allows reading a position in the map
	 *
	 * @param in The scanner to read from
	 * @return The classic position that has been read
	 */
	public static IPosition readClassicPosition(@NotNull Scanner in) {
		// Verifica se ainda há tokens disponíveis
		if (!in.hasNext()) {
			throw new IllegalArgumentException("Nenhuma posição válida encontrada!");
		}

		String part1 = in.next(); // Primeiro token
		String part2 = null;

		if (in.hasNextInt()) {
			part2 = in.next(); // Segundo token, se disponível
		}

		String input = (part2 != null) ? part1 + part2 : part1;

		// Normalizar o input para tratar letras maiúsculas e minúsculas
		input = input.toUpperCase();

		// Verificar os dois formatos possíveis: compactos e com espaço
		if (input.matches("[A-Z]\\d+")) {
			char column = input.charAt(0); // Extrair a coluna
			int row = Integer.parseInt(input.substring(1)); // Extrair a linha
			return new Position(column, row);
		} else if (part2 != null && part1.matches("[A-Z]") && part2.matches("\\d+")) {
			char column = part1.charAt(0); // Extrair a coluna
			int row = Integer.parseInt(part2); // Extrair a linha
			return new Position(column, row);
		} else {
			throw new IllegalArgumentException("Formato inválido. Use 'A3', 'A 3' ou similar.");
		}
	}

}