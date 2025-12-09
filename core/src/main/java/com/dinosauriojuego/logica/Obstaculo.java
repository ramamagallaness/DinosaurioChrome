package com.dinosauriojuego.logica;

import com.badlogic.gdx.math.Rectangle;

/**
 * Clase que representa los obstáculos del juego
 */
public class Obstaculo {
    // Tipos de obstáculos
    public static final int TIPO_CACTUS = 0;
    public static final int TIPO_PAJARO = 1;

    // Posición y dimensiones
    private float x;
    private float y;
    private float ancho;
    private float alto;
    private int tipo;

    // Colisión
    private Rectangle bounds;

    public Obstaculo(float x, float alto, float ancho, int tipo) {
        this.x = x;
        this.ancho = ancho;
        this.alto = alto;
        this.tipo = tipo;

        // Los pájaros vuelan más alto y a diferentes alturas
        if (tipo == TIPO_PAJARO) {
            // Altura aleatoria para pájaros: puede volar bajo, medio o alto
            float[] alturasVuelo = {80f, 110f, 140f};
            int indiceAleatorio = (int)(Math.random() * alturasVuelo.length);
            this.y = alturasVuelo[indiceAleatorio];
        } else {
            // Cactus siempre en el suelo
            this.y = 60;
        }

        this.bounds = new Rectangle(x, y, ancho, alto);
    }

    /**
     * Actualiza la posición del obstáculo
     */
    public void update(float deltaTime, float velocidad) {
        x -= velocidad * deltaTime;
        bounds.x = x;
    }

    // Getters
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }

    public int getTipo() {
        return tipo;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
