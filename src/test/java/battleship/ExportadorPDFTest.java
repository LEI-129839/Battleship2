package battleship;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ExportadorPDF.
 * The PDF is written to a temporary directory and read back with PDFBox.
 * Cyclomatic Complexity for each method:
 * - novaPagina (private): 2
 * - escreverLinha (private): 2
 * - descreverTiro (private): 5
 * - exportar: 4
 * - escreverTabuleiro (private): 8
 */
class ExportadorPDFTest {

    @TempDir
    Path tempDir;

    private Game game;

    @BeforeEach
    void setUp() {
        Fleet fleet = new Fleet();
        fleet.addShip(new Barge(Compass.NORTH, new Position(0, 0)));
        fleet.addShip(new Frigate(Compass.NORTH, new Position(5, 5)));
        game = new Game(fleet);
    }

    private static List<IPosition> shots(IPosition a, IPosition b, IPosition c) {
        return List.of(a, b, c);
    }

    private static String readText(Path file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.toFile())) {
            return new PDFTextStripper().getText(document);
        }
    }

    private static int countPages(Path file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.toFile())) {
            return document.getNumberOfPages();
        }
    }

    @Test
    @DisplayName("Jogo sem rajadas gera um PDF de uma página com título e tabuleiro")
    void exportWithoutMoves() throws IOException {
        Path file = tempDir.resolve("vazio.pdf");
        ExportadorPDF.exportar(game, file.toString());

        assertTrue(file.toFile().exists(), "Error: the PDF file should be created.");
        assertEquals(1, countPages(file), "Error: a game without moves should fit in one page.");
        String text = readText(file);
        assertTrue(text.contains("Histórico das jogadas"), "Error: the title should be in the PDF.");
        assertTrue(text.contains("Tabuleiro final"), "Error: the final board should be in the PDF.");
    }

    @Test
    @DisplayName("PDF descreve acertos, afundamentos e tiros na água")
    void exportHitsSunkAndWater() throws IOException {
        game.fireShots(shots(new Position(0, 0), new Position(5, 5), new Position(9, 9)));

        Path file = tempDir.resolve("rajada.pdf");
        ExportadorPDF.exportar(game, file.toString());

        String text = readText(file);
        assertTrue(text.contains("Rajada 1:"), "Error: the first move should be listed.");
        assertTrue(text.contains("A1 (Barca ao fundo)"), "Error: sunk barge should be described.");
        assertTrue(text.contains("F6 (tiro em Fragata)"), "Error: frigate hit should be described.");
        assertTrue(text.contains("J10 (água)"), "Error: water shot should be described.");
    }

    @Test
    @DisplayName("PDF descreve tiros fora do tabuleiro e tiros repetidos")
    void exportOutsideAndRepeated() throws IOException {
        game.fireShots(shots(new Position(0, 0), new Position(5, 5), new Position(9, 9)));
        game.fireShots(shots(new Position(15, 15), new Position(9, 9), new Position(5, 5)));

        Path file = tempDir.resolve("repetidos.pdf");
        ExportadorPDF.exportar(game, file.toString());

        String text = readText(file);
        assertTrue(text.contains("Rajada 2:"), "Error: the second move should be listed.");
        assertTrue(text.contains("P16 (fora do tabuleiro)"), "Error: outside shot should be described.");
        assertTrue(text.contains("J10 (repetido)"), "Error: repeated water shot should be described.");
        assertTrue(text.contains("F6 (repetido)"), "Error: repeated hit should be described.");
    }

    @Test
    @DisplayName("Histórico longo passa para uma segunda página")
    void exportLongHistoryUsesNewPage() throws IOException {
        for (int i = 0; i < 60; i++)
            game.fireShots(shots(new Position(15, 15), new Position(15, 16), new Position(15, 17)));

        Path file = tempDir.resolve("longo.pdf");
        ExportadorPDF.exportar(game, file.toString());

        assertTrue(countPages(file) > 1, "Error: a long history should need more than one page.");
    }

    @Test
    @DisplayName("Exportar para uma pasta que não existe lança IOException")
    void exportToInvalidPathThrows() {
        String invalid = tempDir.resolve("nao_existe").resolve("jogadas.pdf").toString();
        assertThrows(IOException.class, () -> ExportadorPDF.exportar(game, invalid),
                "Error: writing to a missing directory should throw IOException.");
    }
}