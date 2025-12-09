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
    private static final float VELOCIDAD_INICIAL = 200f;
    private static final float VELOCIDAD_MAXIMA = 600f;
    private static final float VELOCIDAD_INCREMENTO = 0.5f;

    // Spawn de obstáculos
    private float tiempoSpawnObstaculo;
    private float tiempoSpawnActual;
    private static final float SPAWN_INICIAL = 1.5f;
    private static final float SPAWN_MINIMO = 0.8f;

    // Puntuación
    private int puntuacion;
    private int highScore;
    private Preferences prefs;

    // Estado del juego
    private boolean gameOver;
    private boolean modoNoche;

    // Dimensiones de pantalla
    private float alturaPantalla;
    private float anchoPantalla;

    public DinosaurioGame(float anchoPantalla, float alturaPantalla) {
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

        // Cargar high score
        prefs = Gdx.app.getPreferences("DinosaurioChrome");
        highScore = prefs.getInteger("highScore", 0);
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

        // Incrementar velocidad gradualmente
        velocidadJuego += VELOCIDAD_INCREMENTO * deltaTime;
        if (velocidadJuego > VELOCIDAD_MAXIMA) {
            velocidadJuego = VELOCIDAD_MAXIMA;
        }

        // Actualizar obstáculos
        for (int i = obstaculos.size() - 1; i >= 0; i--) {
            Obstaculo obs = obstaculos.get(i);
            obs.update(deltaTime, velocidadJuego);

            // Detectar colisión
            if (dinosaurio.colisiona(obs)) {
                gameOver = true;
                if (puntuacion > highScore) {
                    highScore = puntuacion;
                    prefs.putInteger("highScore", highScore);
                    prefs.flush();
                }
            }

            // Eliminar obstáculos fuera de pantalla
            if (obs.getX() + obs.getAncho() < 0) {
                obstaculos.remove(i);
                puntuacion += 10;
            }
        }

        // Spawnear obstáculos
        tiempoSpawnActual += deltaTime;
        if (tiempoSpawnActual >= tiempoSpawnObstaculo) {
            spawnObstaculo();
            tiempoSpawnActual = 0;
            // Reducir tiempo de spawn progresivamente
            if (tiempoSpawnObstaculo > SPAWN_MINIMO) {
                tiempoSpawnObstaculo -= 0.05f;
            }
        }

        // Incrementar puntuación por tiempo
        puntuacion += (int)(velocidadJuego * deltaTime * 0.1f);

        // Cambiar a modo noche cada 700 puntos
        modoNoche = (puntuacion / 700) % 2 == 1;
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
