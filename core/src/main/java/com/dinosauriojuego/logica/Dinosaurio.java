package com.dinosauriojuego.logica;

import com.badlogic.gdx.math.Rectangle;

public class Dinosaurio {
    private float x;
    private float y;
    private float ancho;
    private float alto;
    private float velocidadY;
    private float gravedad = -800f;
    private float fuerzaSalto = 400f;
    private boolean saltando;
    private float alturaSuelo;
    private Rectangle bounds;

    public Dinosaurio(float ancho, float alto) {
        this.ancho = ancho;
        this.alto = alto;
        this.x = 50;
        this.alturaSuelo = 60;
        this.y = alturaSuelo;
        this.velocidadY = 0;
        this.saltando = false;
        this.bounds = new Rectangle(x, y, ancho, alto);
    }

    public void update(float deltaTime, float alturaPantalla) {
        this.alturaSuelo = alturaPantalla - 60;

        // Aplicar gravedad
        velocidadY += gravedad * deltaTime;
        y += velocidadY * deltaTime;

        // Limitar a altura del suelo
        if (y <= alturaSuelo) {
            y = alturaSuelo;
            velocidadY = 0;
            saltando = false;
        }

        // Actualizar bounds
        bounds.x = x;
        bounds.y = y;
    }

    public void saltar() {
        if (!saltando) {
            velocidadY = fuerzaSalto;
            saltando = true;
        }
    }

    public boolean colisiona(Obstaculo obstaculo) {
        return bounds.overlaps(obstaculo.getBounds());
    }

    public void reset() {
        y = alturaSuelo;
        velocidadY = 0;
        saltando = false;
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

    public boolean estaSaltando() {
        return saltando;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
