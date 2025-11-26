package com.dinosauriojuego.logica;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class DinosaurioGame {
    private Dinosaurio dinosaurio;
    private List<Obstaculo> obstaculos;
    private float velocidadJuego;
    private float velocidadMaxima;
    private float tiempoSpawnObstaculo;
    private float tiempoSpawnActual;
    private int puntuacion;
    private boolean gameOver;
    private float alturaPantalla;
    private float anchoPantalla;
    private float velocidadIncremento;

    public DinosaurioGame(float anchoPantalla, float alturaPantalla) {
        this.anchoPantalla = anchoPantalla;
        this.alturaPantalla = alturaPantalla;
        this.dinosaurio = new Dinosaurio(50, 60);
        this.obstaculos = new ArrayList<>();
        this.velocidadJuego = 200f;
        this.velocidadMaxima = 600f;
        this.tiempoSpawnObstaculo = 1.5f;
        this.tiempoSpawnActual = 0f;
        this.puntuacion = 0;
        this.gameOver = false;
        this.velocidadIncremento = 0.5f;
    }

    public void update(float deltaTime, boolean saltando) {
        if (gameOver) return;

        // Actualizar dinosaurio
        dinosaurio.update(deltaTime, alturaPantalla);
        if (saltando && !dinosaurio.estaSaltando()) {
            dinosaurio.saltar();
        }

        // Incrementar velocidad gradualmente
        velocidadJuego += velocidadIncremento * deltaTime;
        if (velocidadJuego > velocidadMaxima) {
            velocidadJuego = velocidadMaxima;
        }

        // Actualizar obstáculos
        for (int i = obstaculos.size() - 1; i >= 0; i--) {
            Obstaculo obs = obstaculos.get(i);
            obs.update(deltaTime, velocidadJuego);

            // Detectar colisión
            if (dinosaurio.colisiona(obs)) {
                gameOver = true;
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
            if (tiempoSpawnObstaculo > 0.8f) {
                tiempoSpawnObstaculo -= 0.05f;
            }
        }

        puntuacion += (int)(velocidadJuego * deltaTime * 0.1f);
    }

    private void spawnObstaculo() {
        float tipoRandom = MathUtils.random(1f);
        if (tipoRandom < 0.7f) {
            obstaculos.add(new Obstaculo(anchoPantalla, 30, 50, Obstaculo.TIPO_CACTUS));
        } else {
            obstaculos.add(new Obstaculo(anchoPantalla, 25, 50, Obstaculo.TIPO_PAJARO));
        }
    }

    public void reset() {
        dinosaurio.reset();
        obstaculos.clear();
        velocidadJuego = 200f;
        tiempoSpawnObstaculo = 1.5f;
        tiempoSpawnActual = 0f;
        puntuacion = 0;
        gameOver = false;
    }

    public Dinosaurio getDinosaurio() {
        return dinosaurio;
    }

    public List<Obstaculo> getObstaculos() {
        return obstaculos;
    }

    public int getPuntuacion() {
        return puntuacion;
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
}
