// 129824 - Rodrigo
package battleship;

/**
 * Classe de demonstração para o cheiro Long Parameter List
 * (Ficha 3 - Tarefa 2A, passo 9).
 * Versão corrigida: refabricação "Introduce Parameter Object" (Fowler).
 */
public class LongParamDemo129824 {

    /**
     * Objeto que agrupa os dados de uma jogada (substitui os 6 parâmetros).
     */
    public static class Jogada {
        public String jogador;
        public int linha;
        public int coluna;
        public boolean acertou;
        public boolean afundou;
        public long tempoMs;
    }

    /**
     * Agora recebe apenas 1 parâmetro.
     */
    public static String descreverJogada(Jogada j) {
        return j.jogador + " -> (" + j.linha + "," + j.coluna + ") acertou=" + j.acertou
                + " afundou=" + j.afundou + " tempo=" + j.tempoMs + "ms";
    }
}
