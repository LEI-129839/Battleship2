// 129824 - Rodrigo
package battleship;

/**
 * Classe de demonstração para provocar uma violação da convenção
 * de nomes de atributos (Checkstyle MemberName) - Ficha 3, Tarefa 2B.
 */
public class NamingDemo129824 {

    /** Atributo com nome fora da convenção (devia ser camelCase). */
    private int Numero_De_Navios = 5;

    public int getNumeroDeNavios() {
        return Numero_De_Navios;
    }
}
