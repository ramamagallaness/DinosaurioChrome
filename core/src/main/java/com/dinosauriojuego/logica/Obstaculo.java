package com.dinosauriojuego.logica;

import com.badlogic.gdx.math.Rectangle;

public class Obstaculo {
    public static final int TIPO_CACTUS = 0;
    public static final int TIPO_PAJARO = 1;

    private float x;
    private float y;
    private float ancho;
    private float alto;
    private int tipo;
    private Rectangle bounds;

    public Obstaculo(float x, float alto, float ancho, int tipo) {
        this.x = x;
        this.ancho = ancho;
        this.alto = alto;
        this.tipo = tipo;

        // Los pájaros vuelan más alto
        if (tipo == TIPO_PAJARO) {
            this.y = 120;
        } else {
            this.y = 60;
        }

        this.bounds = new Rectangle(x, y, ancho, alto);
    }

    public void update(float deltaTime, float velocidad) {
        x -= velocidad * deltaTime;
        bounds.x = x;
    }

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
