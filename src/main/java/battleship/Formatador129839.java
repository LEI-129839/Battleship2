package battleship;

/**
 * 129839 - Afonso Ribeiro. Classe criada para PROVOCAR o cheiro Shotgun Surgery (guião 3, passo 9):
 * a formatação das jogadas está espalhada por muitas classes clientes, por isso qualquer
 * alteração a este formato obriga a mexer em todas elas.
 */
public class Formatador129839 {
    public static String formatar(String etiqueta, Object valor) {
        return etiqueta + ": " + valor;
    }
}
