package com.dinosauriojuego.logica;

import com.badlogic.gdx.math.Rectangle;

/**
 * Clase que representa al dinosaurio jugador
 */
public class Dinosaurio {
    // Posición y dimensiones
    private float x;
    private float y;
    private float ancho;
    private float alto;

    // Física
    private float velocidadY;
    private static final float GRAVEDAD = -800f;
    private static final float FUERZA_SALTO = 400f;

    // Estados
    private boolean saltando;
    private boolean agachado;

    // Alturas
    private float alturaSuelo;
    private float alturaOriginal;
    private float alturaAgachado;

    // Colisión
    private Rectangle bounds;

    public Dinosaurio(float ancho, float alto) {
        this.ancho = ancho;
        this.alturaOriginal = alto;
        this.alturaAgachado = alto * 0.5f;
        this.alto = alturaOriginal;
        this.x = 50;
        this.alturaSuelo = 60; // Altura fija del suelo
        this.y = alturaSuelo; // El dino empieza justo en el suelo
        this.velocidadY = 0;
        this.saltando = false;
        this.agachado = false;
        this.bounds = new Rectangle(x, y, ancho, alto);
    }

    public void update(float deltaTime, float alturaPantalla) {
        // El suelo está a 60 píxeles del fondo
        this.alturaSuelo = 60;

        // Aplicar gravedad
        velocidadY += GRAVEDAD * deltaTime;
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
        bounds.width = ancho;
        bounds.height = alto;
    }

    public void saltar() {
        if (!saltando && !agachado) {
            velocidadY = FUERZA_SALTO;
            saltando = true;
        }
    }

    public void agacharse(boolean agachar) {
        if (!saltando) {
            agachado = agachar;
            if (agachado) {
                alto = alturaAgachado;
            } else {
                alto = alturaOriginal;
            }
        }
    }

    public boolean colisiona(Obstaculo obstaculo) {
        return bounds.overlaps(obstaculo.getBounds());
    }

    public void reset() {
        y = alturaSuelo;
        velocidadY = 0;
        saltando = false;
        agachado = false;
        alto = alturaOriginal;
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getAncho() { return ancho; }
    public float getAlto() { return alto; }
    public boolean estaSaltando() { return saltando; }
    public boolean estaAgachado() { return agachado; }
    public Rectangle getBounds() { return bounds; }
}
