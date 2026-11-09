package battleship;

/**
 * 129839 - Afonso Ribeiro. Correção do Shotgun Surgery (guião 3, passo 9): a formatação que
 * estava espalhada por 8 classes clientes foi movida para um único sítio (Move Method / Inline Class),
 * por isso uma alteração ao formato passa a obrigar a mexer só nesta classe.
 */
public class Formatador129839 {
    public static String formatar(String etiqueta, Object valor) {
        return etiqueta + ": " + valor;
    }

    public static String relatorio(Object rajada, Object tiro) {
        return formatar("Rajada", rajada) + System.lineSeparator() + formatar("Tiro", tiro);
    }
}
