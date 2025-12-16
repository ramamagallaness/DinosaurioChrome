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

    // Colisión - Hitbox ajustada
    private Rectangle bounds;

    // Reducción de hitbox según el tipo
    private static final float HITBOX_CACTUS_X = 0.20f;
    private static final float HITBOX_CACTUS_Y = 0.10f;
    private static final float HITBOX_PAJARO_X = 0.25f;
    private static final float HITBOX_PAJARO_Y = 0.20f;

    // Animación de pájaros
    private float tiempoAnimacion;
    private static final float TIEMPO_CAMBIO_SPRITE = 0.15f; // Cambiar alas cada 0.15 segundos
    private int spriteActual; // 0 = alas arriba, 1 = alas abajo

    public Obstaculo(float x, float alto, float ancho, int tipo) {
        this.x = x;
        this.ancho = ancho;
        this.alto = alto;
        this.tipo = tipo;
        this.tiempoAnimacion = 0;
        this.spriteActual = 0;

        // Los pájaros vuelan más alto y a diferentes alturas
        if (tipo == TIPO_PAJARO) {
            float[] alturasVuelo = {80f, 110f, 140f};
            int indiceAleatorio = (int)(Math.random() * alturasVuelo.length);
            this.y = alturasVuelo[indiceAleatorio];
        } else {
            // Cactus siempre en el suelo
            this.y = 60;
        }

        this.bounds = new Rectangle();
        actualizarHitbox();
    }

    /**
     * Actualiza la hitbox según el tipo de obstáculo
     */
    private void actualizarHitbox() {
        float reduccionX, reduccionY;

        if (tipo == TIPO_CACTUS) {
            reduccionX = ancho * HITBOX_CACTUS_X;
            reduccionY = alto * HITBOX_CACTUS_Y;
        } else {
            reduccionX = ancho * HITBOX_PAJARO_X;
            reduccionY = alto * HITBOX_PAJARO_Y;
        }

        bounds.x = x + reduccionX;
        bounds.y = y + reduccionY;
        bounds.width = ancho - (reduccionX * 2);
        bounds.height = alto - (reduccionY * 2);
    }

    /**
     * Actualiza la posición del obstáculo
     */
    public void update(float deltaTime, float velocidad) {
        x -= velocidad * deltaTime;

        // Actualizar animación de pájaros
        if (tipo == TIPO_PAJARO) {
            tiempoAnimacion += deltaTime;
            if (tiempoAnimacion >= TIEMPO_CAMBIO_SPRITE) {
                spriteActual = (spriteActual + 1) % 2; // Alterna entre 0 y 1
                tiempoAnimacion = 0;
            }
        }

        actualizarHitbox();
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

    public int getSpriteActual() {
        return spriteActual;
    }
}
