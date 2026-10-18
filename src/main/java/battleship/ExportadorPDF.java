package battleship;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ExportadorPDF {
    private static final float MARGEM = 50;
    private static final float ALTURA_LINHA = 14;

    private static final PDFont NEGRITO = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDFont MONO = new PDType1Font(Standard14Fonts.FontName.COURIER);

    private final PDDocument documento;
    private PDPageContentStream conteudo;
    private float y;


    private ExportadorPDF(PDDocument documento) {
        this.documento = documento;
    }

    // cria uma página A4 nova e coloca a margem de topo
    private void novaPagina() throws IOException {
        if (conteudo != null)
            conteudo.close();
        PDPage pagina = new PDPage(PDRectangle.A4);
        documento.addPage(pagina);
        conteudo = new PDPageContentStream(documento, pagina);
        y = pagina.getMediaBox().getHeight() - MARGEM;

    }

    private void escreverLinha(String texto, PDFont fonte, float tamanho) throws IOException {
        if (y < MARGEM)
            novaPagina();
        conteudo.beginText();
        conteudo.setFont(fonte, tamanho);
        conteudo.newLineAtOffset(MARGEM, y);
        conteudo.showText(texto);
        conteudo.endText();
        y -= ALTURA_LINHA;

    }

    // converte o resultado de um tiro para texto
    private static String descreverTiro(IGame.ShotResult resultado) {
        if (!resultado.valid())
            return "fora do tabuleiro";
        if (resultado.repeated())
            return "repetido";
        if (resultado.ship() == null)
            return "água";
        if (resultado.sunk())
            return resultado.ship().getCategory() + " ao fundo";
        return "tiro em " + resultado.ship().getCategory();
    }

    public static void exportar(IGame game, String ficheiro) throws IOException {
        try (PDDocument documento = new PDDocument()) {
            ExportadorPDF pdf = new ExportadorPDF(documento);
            pdf.novaPagina();

            pdf.escreverLinha("Batalha Naval - Histórico das jogadas", NEGRITO, 16);
            pdf.y -= ALTURA_LINHA;

            for (IMove jogada : game.getAlienMoves()) {
                String linha = "Rajada " + jogada.getNumber() + ":";
                for (int i = 0; i < jogada.getShots().size(); i++) {
                    IPosition tiro = jogada.getShots().get(i);
                    IGame.ShotResult resultado = jogada.getShotResults().get(i);
                    linha += "  " + tiro + " (" + descreverTiro(resultado) + ")";
                }
                pdf.escreverLinha(linha, MONO, 10);
            }

            pdf.y -= ALTURA_LINHA;
            pdf.escreverTabuleiro(game);

            pdf.conteudo.close();
            documento.save(ficheiro);
        }
    }

    // desenha o tabuleiro final com os navios e os tiros
    private void escreverTabuleiro(IGame game) throws IOException {
        char[][] mapa = new char[Game.BOARD_SIZE][Game.BOARD_SIZE];
        for (int r = 0; r < Game.BOARD_SIZE; r++)
            for (int c = 0; c < Game.BOARD_SIZE; c++)
                mapa[r][c] = '.';

        for (IShip navio : game.getMyFleet().getShips())
            for (IPosition pos : navio.getPositions())
                mapa[pos.getRow()][pos.getColumn()] = '#';

        for (IMove jogada : game.getAlienMoves())
            for (IPosition tiro : jogada.getShots())
                if (tiro.isInside()) {
                    int r = tiro.getRow();
                    int c = tiro.getColumn();
                    if (mapa[r][c] == '#')
                        mapa[r][c] = '*';
                    else if (mapa[r][c] == '.')
                        mapa[r][c] = 'o';
                }

        escreverLinha("Tabuleiro final", NEGRITO, 12);
        String cabecalho = "  ";
        for (int c = 0; c < Game.BOARD_SIZE; c++)
            cabecalho += String.format("%3d", c + 1);
        escreverLinha(cabecalho, MONO, 10);

        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            String linha = (char) ('A' + r) + " ";
            for (int c = 0; c < Game.BOARD_SIZE; c++)
                linha += "  " + mapa[r][c];
            escreverLinha(linha, MONO, 10);
        }

        y -= ALTURA_LINHA;
        escreverLinha("# navio   * tiro certeiro   o tiro na água", MONO, 10);
    }




}
