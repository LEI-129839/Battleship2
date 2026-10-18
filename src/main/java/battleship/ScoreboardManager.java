package battleship;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardManager {
    private static final String SCORE_FILE = "data/scoreboard.json";
    private ObjectMapper mapper;
    private List<PlayerScore> scores;

    public ScoreboardManager() {
        mapper = new ObjectMapper();
        scores = loadScores();
    }

    // Lê as pontuações do ficheiro JSON
    private List<PlayerScore> loadScores() {
        File file = new File(SCORE_FILE);
        // Se o ficheiro já existir, lê e converte para a lista de PlayerScore
        if (file.exists()) {
            try {
                return mapper.readValue(file, new TypeReference<List<PlayerScore>>() {});
            } catch (IOException e) {
                System.out.println("Erro ao ler o ficheiro de pontuações: " + e.getMessage());
            }
        }
        // Se for a primeira vez que o jogo corre, devolve uma lista vazia
        return new ArrayList<>();
    }

    // Guarda as pontuações da memória para o ficheiro JSON
    private void saveScores() {
        try {
            mapper.writeValue(new File(SCORE_FILE), scores);
        } catch (IOException e) {
            System.out.println("Erro ao guardar as pontuações: " + e.getMessage());
        }
    }

    // Método que deves chamar sempre que um jogador ganhar o jogo
    public void registerWin(String playerName) {
        PlayerScore player = null;

        // Procura se o jogador já existe na lista
        for (PlayerScore p : scores) {
            if (p.getPlayerName().equals(playerName)) {
                player = p;
                break;
            }
        }

        // Se o jogador não existir, cria-o com 0 vitórias iniciais
        if (player == null) {
            player = new PlayerScore(playerName, 0);
            scores.add(player);
        }

        // Adiciona a vitória e guarda o ficheiro atualizado
        player.addWin();
        saveScores();
    }

    // Devolve a lista de pontuações ordenada por número de vitórias (do maior para o menor)
    public List<PlayerScore> getSortedScores() {
        scores.sort((p1, p2) -> Integer.compare(p2.getWins(), p1.getWins()));
        return scores;
    }
}