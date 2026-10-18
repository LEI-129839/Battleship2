package battleship;

public class PlayerScore {
    private String playerName;
    private int wins;

    // Construtor vazio (importante se decidirmos usar o Jackson para JSON)
    public PlayerScore() {
    }

    public PlayerScore(String playerName, int wins) {
        this.playerName = playerName;
        this.wins = wins;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    // Método para adicionar uma vitória quando o jogador ganha o jogo
    public void addWin() {
        this.wins++;
    }

    @Override
    public String toString() {
        return playerName + " - Vitórias: " + wins;
    }
}
