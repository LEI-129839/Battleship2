package restserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários da classe GameRegistry.
 * Autor: 129839
 */
class GameRegistryTest {

    private GameRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new GameRegistry();
    }

    @Test
    @DisplayName("createSession devolve uma sessão com os dados do jogador e um id gerado")
    void testCreateSession() {
        GameSession s = registry.createSession("Afonso", "http://localhost:9090");

        assertNotNull(s);
        assertNotNull(s.getGameId());
        assertFalse(s.getGameId().isBlank());
        assertEquals("Afonso", s.getPlayerName());
        assertEquals("http://localhost:9090", s.getCallbackUrl());
    }

    @Test
    @DisplayName("Cada sessão criada recebe um id diferente")
    void testCreateSessionUniqueIds() {
        GameSession a = registry.createSession("A", "http://a");
        GameSession b = registry.createSession("B", "http://b");
        assertNotEquals(a.getGameId(), b.getGameId());
    }

    @Test
    @DisplayName("getSession devolve a mesma sessão que foi criada")
    void testGetSessionAfterCreate() {
        GameSession s = registry.createSession("Afonso", "http://localhost:9090");
        assertSame(s, registry.getSession(s.getGameId()));
    }

    @Test
    @DisplayName("getSession devolve null para um id desconhecido")
    void testGetUnknownSession() {
        assertNull(registry.getSession("nao-existe"));
    }

    @Test
    @DisplayName("removeSession apaga só a sessão indicada")
    void testRemoveSession() {
        GameSession a = registry.createSession("A", "http://a");
        GameSession b = registry.createSession("B", "http://b");

        registry.removeSession(a.getGameId());

        assertNull(registry.getSession(a.getGameId()));
        assertSame(b, registry.getSession(b.getGameId()));
    }

    @Test
    @DisplayName("removeSession com um id desconhecido não faz nada")
    void testRemoveUnknownSession() {
        GameSession a = registry.createSession("A", "http://a");
        assertDoesNotThrow(() -> registry.removeSession("nao-existe"));
        assertSame(a, registry.getSession(a.getGameId()));
    }
}
