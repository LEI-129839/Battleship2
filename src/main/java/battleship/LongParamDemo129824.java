// 129824 - Rodrigo
package battleship;

/**
 * Classe de demonstração para provocar o cheiro Long Parameter List
 * (Ficha 3 - Tarefa 2A, passo 9).
 */
public class LongParamDemo129824 {

    /**
     * Método com demasiados parâmetros (6): deve ser detetado pelo quality gate.
     */
    public static String descreverJogada(String jogador, int linha, int coluna,
                                         boolean acertou, boolean afundou, long tempoMs) {
        return jogador + " -> (" + linha + "," + coluna + ") acertou=" + acertou
                + " afundou=" + afundou + " tempo=" + tempoMs + "ms";
    }
}
