package com.dinosauriojuego.logica;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que maneja la lógica del juego
 */
public class DinosaurioGame {
    // Elementos del juego
    private Dinosaurio dinosaurio;
    private List<Obstaculo> obstaculos;

    // Configuración de velocidad
    private float velocidadJuego;
    private static final float VELOCIDAD_INICIAL = 260f;
    private static final float VELOCIDAD_INCREMENTO = 10.0f;

    // Spawn de obstáculos
    private float tiempoSpawnObstaculo;
    private float tiempoSpawnActual;
    private static final float SPAWN_INICIAL = 1.8f;
    private static final float SPAWN_MINIMO = 1.0f;

    // Puntuación
    private int puntuacion;
    private int highScore;
    private Preferences prefs;
    private String playerKey;

    // Estado del juego
    private boolean gameOver;
    private boolean modoNoche;

    // Dimensiones de pantalla
    private float alturaPantalla;
    private float anchoPantalla;

    // Identificador único para cada instancia
    private static int instanceCounter = 0;
    private int instanceId;

    public DinosaurioGame(float anchoPantalla, float alturaPantalla) {
        this.instanceId = instanceCounter++;
        this.anchoPantalla = anchoPantalla;
        this.alturaPantalla = alturaPantalla;
        this.dinosaurio = new Dinosaurio(50, 60);
        this.obstaculos = new ArrayList<Obstaculo>();
        this.velocidadJuego = VELOCIDAD_INICIAL;
        this.tiempoSpawnObstaculo = SPAWN_INICIAL;
        this.tiempoSpawnActual = 0f;
        this.puntuacion = 0;
        this.gameOver = false;
        this.modoNoche = false;

        // Cargar high score específico para cada jugador
        prefs = Gdx.app.getPreferences("DinosaurioChrome");
        playerKey = "highScore_player" + instanceId;
        highScore = prefs.getInteger(playerKey, 0);
    }

    /**
     * Actualiza el estado del juego
     */
    public void update(float deltaTime, boolean saltando, boolean agachando) {
        if (gameOver) {
            return;
        }

        // Actualizar dinosaurio
        dinosaurio.update(deltaTime, alturaPantalla);

        if (saltando && !dinosaurio.estaSaltando() && !dinosaurio.estaAgachado()) {
            dinosaurio.saltar();
        }

        dinosaurio.agacharse(agachando);

        // Incrementar velocidad infinitamente
        velocidadJuego += VELOCIDAD_INCREMENTO * deltaTime;

        // Actualizar obstáculos
        for (int i = obstaculos.size() - 1; i >= 0; i--) {
            Obstaculo obs = obstaculos.get(i);
            obs.update(deltaTime, velocidadJuego);

            // Detectar colisión
            if (dinosaurio.colisiona(obs)) {
                gameOver = true;
                if (puntuacion > highScore) {
                    highScore = puntuacion;
                    prefs.putInteger(playerKey, highScore);
                    prefs.flush();
                }
            }

            // Eliminar obstáculos fuera de pantalla
            if (obs.getX() + obs.getAncho() < 0) {
                obstaculos.remove(i);
            }
        }

        // Calcular tiempo de spawn dinámico basado en la velocidad
        float factorVelocidad = (velocidadJuego - VELOCIDAD_INICIAL) / 500f;
        float tiempoSpawnDinamico = SPAWN_INICIAL + (factorVelocidad * 0.3f);
        if (tiempoSpawnDinamico < SPAWN_MINIMO) {
            tiempoSpawnDinamico = SPAWN_MINIMO;
        }

        // Spawnear obstáculos
        tiempoSpawnActual += deltaTime;
        if (tiempoSpawnActual >= tiempoSpawnDinamico) {
            spawnObstaculo();
            tiempoSpawnActual = 0;
        }

        // Incrementar puntuación CONTINUAMENTE por tiempo
        puntuacion += (int)(100 * deltaTime);

        // Modo noche: se activa a los 200 puntos y se desactiva a los 400
        int ciclo = (puntuacion / 200) % 2;
        modoNoche = (ciclo == 1);
    }

    /**
     * Genera un nuevo obstáculo aleatorio
     */
    private void spawnObstaculo() {
        float tipoRandom = MathUtils.random(1f);
        if (tipoRandom < 0.7f) {
            // Cactus con altura variable
            float alturaObstaculo = MathUtils.random(30f, 50f);
            obstaculos.add(new Obstaculo(anchoPantalla, alturaObstaculo, 30, Obstaculo.TIPO_CACTUS));
        } else {
            // Pterodáctilo
            obstaculos.add(new Obstaculo(anchoPantalla, 25, 50, Obstaculo.TIPO_PAJARO));
        }
    }

    /**
     * Reinicia el juego a su estado inicial
     */
    public void reset() {
        dinosaurio.reset();
        obstaculos.clear();
        velocidadJuego = VELOCIDAD_INICIAL;
        tiempoSpawnObstaculo = SPAWN_INICIAL;
        tiempoSpawnActual = 0f;
        puntuacion = 0;
        gameOver = false;
        modoNoche = false;
    }

    // Getters
    public Dinosaurio getDinosaurio() {
        return dinosaurio;
    }

    public List<Obstaculo> getObstaculos() {
        return obstaculos;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public float getVelocidadJuego() {
        return velocidadJuego;
    }

    public boolean isModoNoche() {
        return modoNoche;
    }
}
