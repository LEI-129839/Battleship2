package battleship;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class VisualizadorGrafico {

    // Variável estática para impedir que o ecrã abra 2 vezes se escreveres "mapa" repetidamente
    private static boolean isRunning = false;

    public void mostrarTabuleiro(IGame game) {
        if (isRunning) {
            System.out.println("O radar já está ativo numa janela separada!");
            return;
        }

        // Criar uma THREAD: Isto corre em paralelo para não bloquear a consola principal!
        new Thread(() -> {
            try {
                isRunning = true;
                DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
                Screen screen = new TerminalScreen(terminalFactory.createTerminal());
                screen.startScreen();

                while (isRunning) {
                    TerminalSize size = screen.doResizeIfNecessary();
                    if (size == null) {
                        size = screen.getTerminalSize();
                    }

                    TextGraphics textGraphics = screen.newTextGraphics();

                    // Fundo
                    textGraphics.setBackgroundColor(TextColor.ANSI.BLUE);
                    textGraphics.fillRectangle(new com.googlecode.lanterna.TerminalPosition(0, 0), size, ' ');

                    int gridWidth = 10 * 3;
                    int gridHeight = 10;
                    int startCol = Math.max(0, (size.getColumns() - gridWidth) / 2);
                    int startRow = Math.max(0, (size.getRows() - gridHeight) / 2);

                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    String titulo = "=== RADAR TÁTICO (EM DIRETO) ===";
                    textGraphics.putString(Math.max(0, (size.getColumns() - titulo.length()) / 2), Math.max(0, startRow - 4), titulo);

                    String subtitulo = "[ Pressiona ESC na janela gráfica para a fechar ]";
                    textGraphics.putString(Math.max(0, (size.getColumns() - subtitulo.length()) / 2), Math.max(0, startRow - 2), subtitulo);

                    // 1. Água
                    for (int l = 0; l < 10; l++) {
                        for (int c = 0; c < 10; c++) {
                            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
                            textGraphics.putString(startCol + (c * 3), startRow + l, " ~ ");
                        }
                    }

                    if (game != null && game.getMyFleet() != null) {
                        // 2. Desenhar os Navios da Frota
                        for (IShip navio : game.getMyFleet().getShips()) {
                            int top = navio.getTopMostPos();
                            int bottom = navio.getBottomMostPos();
                            int left = navio.getLeftMostPos();
                            int right = navio.getRightMostPos();
                            boolean isHorizontal = (top == bottom);
                            int tamanho = navio.getSize();

                            for (IPosition pos : navio.getPositions()) {
                                int c = pos.getColumn();
                                int l = pos.getRow();
                                String desenho;

                                if (tamanho == 1) desenho = "<■>";
                                else if (isHorizontal) {
                                    if (c == left) desenho = "◄██";
                                    else if (c == right) desenho = "██►";
                                    else desenho = "███";
                                } else {
                                    if (l == top) desenho = " ▲ ";
                                    else if (l == bottom) desenho = " ▼ ";
                                    else desenho = " █ ";
                                }

                                textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
                                textGraphics.putString(startCol + (c * 3), startRow + l, desenho);
                            }
                        }

                        // 3. Desenhar os Tiros e Explosões (Alien Moves)
                        if (game.getAlienMoves() != null) {
                            for (IMove move : game.getAlienMoves()) {
                                for (IPosition tiro : move.getShots()) {
                                    int c = tiro.getColumn();
                                    int l = tiro.getRow();

                                    // Deteta se o tiro do inimigo acertou em algum dos teus navios
                                    boolean acertou = false;
                                    for (IShip navio : game.getMyFleet().getShips()) {
                                        if (navio.occupies(tiro)) {
                                            acertou = true;
                                            break;
                                        }
                                    }

                                    // Pinta por cima do navio ou da água consoante o resultado
                                    if (acertou) {
                                        textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
                                        textGraphics.putString(startCol + (c * 3), startRow + l, "[X]"); // Explosão vermelha
                                    } else {
                                        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                                        textGraphics.putString(startCol + (c * 3), startRow + l, "(o)"); // Tiro na água
                                    }
                                }
                            }
                        }
                    }

                    // Legenda Dinâmica
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    String legenda = "Legenda: [◄██►] Aliado | [X] Fogo Inimigo | (o) Tiro na Água";
                    textGraphics.putString(Math.max(0, (size.getColumns() - legenda.length()) / 2), startRow + 12, legenda);

                    screen.refresh();

                    KeyStroke keyStroke = screen.pollInput();
                    if (keyStroke != null && keyStroke.getKeyType() == KeyType.Escape) {
                        isRunning = false;
                        break; // Sai do loop e fecha a janela
                    }

                    // Atualiza a cada 200ms para apanhar os tiros em tempo real
                    Thread.sleep(200);
                }

                screen.stopScreen();

            } catch (Exception e) {
                System.err.println("Erro ao iniciar a interface gráfica: " + e.getMessage());
                isRunning = false;
            }
        }).start(); // Dá o "tiro de partida" na Thread paralela
    }
}