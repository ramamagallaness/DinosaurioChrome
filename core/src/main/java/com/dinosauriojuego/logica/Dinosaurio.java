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

    // Colisión - Hitbox ajustada al sprite real
    private Rectangle bounds;
    private static final float HITBOX_REDUCCION_X = 0.25f; // Reduce 25% en cada lado (50% total)
    private static final float HITBOX_REDUCCION_Y = 0.15f; // Reduce 15% arriba/abajo

    public Dinosaurio(float ancho, float alto) {
        this.ancho = ancho;
        this.alturaOriginal = alto;
        this.alturaAgachado = alto * 0.5f;
        this.alto = alturaOriginal;
        this.x = 50;
        this.alturaSuelo = 60;
        this.y = alturaSuelo;
        this.velocidadY = 0;
        this.saltando = false;
        this.agachado = false;
        this.bounds = new Rectangle();
        actualizarHitbox();
    }

    /**
     * Actualiza la hitbox para que se ajuste al sprite visible
     */
    private void actualizarHitbox() {
        // Calcular reducción en píxeles
        float reduccionX = ancho * HITBOX_REDUCCION_X;
        float reduccionY = alto * HITBOX_REDUCCION_Y;

        // Aplicar hitbox reducida centrada en el sprite
        bounds.x = x + reduccionX;
        bounds.y = y + reduccionY;
        bounds.width = ancho - (reduccionX * 2);
        bounds.height = alto - (reduccionY * 2);
    }

    public void update(float deltaTime, float alturaPantalla) {
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

        // Actualizar hitbox
        actualizarHitbox();
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
            actualizarHitbox();
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
        actualizarHitbox();
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
