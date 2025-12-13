package com.dinosauriojuego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dinosauriojuego.logica.DinosaurioGame;
import com.dinosauriojuego.logica.Dinosaurio;
import com.dinosauriojuego.logica.Obstaculo;

/**
 * Pantalla principal del juego con modo 2 jugadores
 */
public class DinosaurioGameScreen implements Screen {
    // Dimensiones de cada viewport
    private static final float GAME_WIDTH = 1200;
    private static final float GAME_HEIGHT = 360;

    // Lógica del juego - dos instancias independientes
    private DinosaurioGame gameJugador1;
    private DinosaurioGame gameJugador2;

    // Renderizado - cámaras y viewports separados
    private OrthographicCamera cameraJugador1;
    private OrthographicCamera cameraJugador2;
    private Viewport viewportJugador1;
    private Viewport viewportJugador2;
    private ShapeRenderer shapeRenderer;

    // UI
    private Stage stageJugador1;
    private Stage stageJugador2;
    private Stage stageGlobal;
    private Skin skin;

    // Labels para cada jugador (dentro del piso)
    private Label puntuacionJ1Label;
    private Label highScoreJ1Label;
    private Label jugador1Label;
    private Label puntuacionJ2Label;
    private Label highScoreJ2Label;
    private Label jugador2Label;

    // Labels globales
    private Label instruccionesLabel;
    private Label ganadorLabel;
    private Label reiniciarLabel;

    // Control de tiempo para instrucciones
    private float tiempoInstrucciones;
    private static final float TIEMPO_MOSTRAR_INSTRUCCIONES = 3.0f;

    // Input
    private boolean saltoJ1Pendiente;
    private boolean agachadoJ1Pendiente;
    private boolean saltoJ2Pendiente;
    private boolean agachadoJ2Pendiente;

    // Control de juego terminado
    private boolean juegoTerminado;

    public DinosaurioGameScreen(Skin skin) {
        this.skin = skin;

        // Crear dos instancias del juego
        this.gameJugador1 = new DinosaurioGame(GAME_WIDTH, GAME_HEIGHT);
        this.gameJugador2 = new DinosaurioGame(GAME_WIDTH, GAME_HEIGHT);

        // Configurar cámaras y viewports
        this.cameraJugador1 = new OrthographicCamera();
        this.cameraJugador1.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        this.cameraJugador2 = new OrthographicCamera();
        this.cameraJugador2.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        // Viewports para manejar el redimensionamiento
        this.viewportJugador1 = new FitViewport(GAME_WIDTH, GAME_HEIGHT, cameraJugador1);
        this.viewportJugador2 = new FitViewport(GAME_WIDTH, GAME_HEIGHT, cameraJugador2);

        this.shapeRenderer = new ShapeRenderer();

        // Stages separados
        this.stageJugador1 = new Stage(viewportJugador1);
        this.stageJugador2 = new Stage(viewportJugador2);
        this.stageGlobal = new Stage(new FitViewport(GAME_WIDTH, 720));

        this.saltoJ1Pendiente = false;
        this.agachadoJ1Pendiente = false;
        this.saltoJ2Pendiente = false;
        this.agachadoJ2Pendiente = false;

        this.tiempoInstrucciones = 0;
        this.juegoTerminado = false;

        setupUI();
        Gdx.input.setInputProcessor(stageGlobal);
    }

    /**
     * Configura los elementos de la interfaz
     */
    private void setupUI() {
        // === Labels para Jugador 1 (dentro del piso) ===
        jugador1Label = new Label("JUGADOR 1", skin, "default");
        jugador1Label.setFontScale(2.0f);
        jugador1Label.setColor(Color.BLACK);
        jugador1Label.setPosition(20, 20);
        stageJugador1.addActor(jugador1Label);

        highScoreJ1Label = new Label("HI: 0", skin, "default");
        highScoreJ1Label.setFontScale(2.0f);
        highScoreJ1Label.setColor(Color.BLACK);
        highScoreJ1Label.setPosition(250, 20);
        stageJugador1.addActor(highScoreJ1Label);

        puntuacionJ1Label = new Label("Puntos: 0", skin, "default");
        puntuacionJ1Label.setFontScale(2.0f);
        puntuacionJ1Label.setColor(Color.BLACK);
        puntuacionJ1Label.setPosition(500, 20);
        stageJugador1.addActor(puntuacionJ1Label);

        // === Labels para Jugador 2 (dentro del piso) ===
        jugador2Label = new Label("JUGADOR 2", skin, "default");
        jugador2Label.setFontScale(2.0f);
        jugador2Label.setColor(Color.BLACK);
        jugador2Label.setPosition(20, 20);
        stageJugador2.addActor(jugador2Label);

        highScoreJ2Label = new Label("HI: 0", skin, "default");
        highScoreJ2Label.setFontScale(2.0f);
        highScoreJ2Label.setColor(Color.BLACK);
        highScoreJ2Label.setPosition(250, 20);
        stageJugador2.addActor(highScoreJ2Label);

        puntuacionJ2Label = new Label("Puntos: 0", skin, "default");
        puntuacionJ2Label.setFontScale(2.0f);
        puntuacionJ2Label.setColor(Color.BLACK);
        puntuacionJ2Label.setPosition(500, 20);
        stageJugador2.addActor(puntuacionJ2Label);

        // === Labels globales ===
        // Instrucciones
        instruccionesLabel = new Label("JUGADOR 1: W/ESPACIO saltar | S agacharse\nJUGADOR 2: FLECHA ARRIBA saltar | FLECHA ABAJO agacharse", skin, "default");
        instruccionesLabel.setFontScale(1.8f);
        instruccionesLabel.setColor(Color.BLACK);
        instruccionesLabel.setPosition(100, 380);
        instruccionesLabel.setVisible(true);
        stageGlobal.addActor(instruccionesLabel);

        // Mensaje de ganador
        ganadorLabel = new Label("", skin, "default");
        ganadorLabel.setFontScale(4.0f);
        ganadorLabel.setColor(Color.WHITE);
        ganadorLabel.setVisible(false);
        stageGlobal.addActor(ganadorLabel);

        // Instrucciones para reiniciar
        reiniciarLabel = new Label("Presiona ESPACIO para jugar de nuevo", skin, "default");
        reiniciarLabel.setFontScale(2.5f);
        reiniciarLabel.setColor(Color.WHITE);
        reiniciarLabel.setVisible(false);
        stageGlobal.addActor(reiniciarLabel);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        handleInput();

        // Actualizar temporizador de instrucciones
        if (tiempoInstrucciones < TIEMPO_MOSTRAR_INSTRUCCIONES && !juegoTerminado) {
            tiempoInstrucciones += delta;

            if (tiempoInstrucciones >= TIEMPO_MOSTRAR_INSTRUCCIONES - 0.5f) {
                float fadeTime = TIEMPO_MOSTRAR_INSTRUCCIONES - tiempoInstrucciones;
                float alpha = fadeTime / 0.5f;
                Color color = instruccionesLabel.getColor();
                color.a = alpha;
                instruccionesLabel.setColor(color);
            }
        } else if (!juegoTerminado) {
            instruccionesLabel.setVisible(false);
        }

        // Actualizar ambos juegos SOLO si no ha terminado
        if (!juegoTerminado) {
            gameJugador1.update(delta, saltoJ1Pendiente, agachadoJ1Pendiente);
            gameJugador2.update(delta, saltoJ2Pendiente, agachadoJ2Pendiente);

            // Verificar si alguno perdió
            if (gameJugador1.isGameOver() || gameJugador2.isGameOver()) {
                juegoTerminado = true;
            }
        }

        saltoJ1Pendiente = false;
        saltoJ2Pendiente = false;

        // Limpiar pantalla
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Renderizar Jugador 1 (mitad superior)
        renderGame(gameJugador1, viewportJugador1, cameraJugador1, stageJugador1, 0, 360, Color.CYAN);

        // Renderizar Jugador 2 (mitad inferior)
        renderGame(gameJugador2, viewportJugador2, cameraJugador2, stageJugador2, 0, 0, Color.ORANGE);

        // Línea divisoria
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(0, 358, GAME_WIDTH, 4);
        shapeRenderer.end();

        // Actualizar puntuaciones
        puntuacionJ1Label.setText("Puntos: " + gameJugador1.getPuntuacion());
        puntuacionJ1Label.setColor(Color.BLACK);
        highScoreJ1Label.setText("HI: " + gameJugador1.getHighScore());
        highScoreJ1Label.setColor(Color.BLACK);

        puntuacionJ2Label.setText("Puntos: " + gameJugador2.getPuntuacion());
        puntuacionJ2Label.setColor(Color.BLACK);
        highScoreJ2Label.setText("HI: " + gameJugador2.getHighScore());
        highScoreJ2Label.setColor(Color.BLACK);

        // Renderizar UI global
        stageGlobal.act(delta);
        stageGlobal.draw();

        // Mostrar pantalla de ganador si el juego terminó
        if (juegoTerminado) {
            mostrarPantallaGanador();
        }
    }

    /**
     * Muestra la pantalla de ganador cuando alguien pierde
     */
    private void mostrarPantallaGanador() {
        // Resetear viewport a pantalla completa para el overlay
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Configurar proyección para pantalla completa
        shapeRenderer.setProjectionMatrix(stageGlobal.getCamera().combined);

        // Overlay oscuro sobre TODA la ventana
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.9f);
        shapeRenderer.rect(0, 0, GAME_WIDTH, 720);
        shapeRenderer.end();

        // Determinar ganador
        String textoGanador;
        if (gameJugador1.isGameOver() && gameJugador2.isGameOver()) {
            if (gameJugador1.getPuntuacion() > gameJugador2.getPuntuacion()) {
                textoGanador = "JUGADOR 1 GANA!";
            } else if (gameJugador2.getPuntuacion() > gameJugador1.getPuntuacion()) {
                textoGanador = "JUGADOR 2 GANA!";
            } else {
                textoGanador = "EMPATE!";
            }
        } else if (gameJugador1.isGameOver()) {
            textoGanador = "JUGADOR 2 GANA!";
        } else {
            textoGanador = "JUGADOR 1 GANA!";
        }

        ganadorLabel.setText(textoGanador);
        ganadorLabel.setColor(Color.WHITE);
        ganadorLabel.setVisible(true);
        ganadorLabel.setPosition(
                (GAME_WIDTH - ganadorLabel.getWidth() * 4.0f) / 2,
                450
        );

        reiniciarLabel.setColor(Color.WHITE);
        reiniciarLabel.setVisible(true);
        reiniciarLabel.setPosition(
                (GAME_WIDTH - reiniciarLabel.getWidth() * 2.5f) / 2,
                320
        );

        // Dibujar los labels de ganador con el stage global
        stageGlobal.getViewport().apply();
        stageGlobal.draw();

        // Reiniciar con ESPACIO
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameJugador1.reset();
            gameJugador2.reset();
            ganadorLabel.setVisible(false);
            reiniciarLabel.setVisible(false);
            juegoTerminado = false;

            // Resetear instrucciones
            tiempoInstrucciones = 0;
            instruccionesLabel.setVisible(true);
            Color colorInstrucciones = new Color(Color.BLACK);
            colorInstrucciones.a = 1.0f;
            instruccionesLabel.setColor(colorInstrucciones);
        }
    }

    /**
     * Renderiza un juego individual en su viewport
     */
    private void renderGame(DinosaurioGame game, Viewport viewport, OrthographicCamera camera,
                            Stage stage, int offsetX, int offsetY, Color playerColor) {
        // Configurar viewport
        viewport.apply();
        viewport.setScreenBounds(offsetX, offsetY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);

        // Colores según modo día/noche
        Color colorFondo;
        Color colorSuelo;
        Color colorTexto;

        if (game.isModoNoche()) {
            colorFondo = new Color(0.05f, 0.05f, 0.1f, 1);
            colorSuelo = new Color(0.2f, 0.2f, 0.3f, 1);
            colorTexto = Color.WHITE;
        } else {
            colorFondo = new Color(0.9f, 0.9f, 0.95f, 1);
            colorSuelo = new Color(0.5f, 0.5f, 0.5f, 1);
            colorTexto = Color.BLACK;
        }

        // Fondo del viewport
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(colorFondo);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
        shapeRenderer.end();

        // Renderizar juego
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar suelo
        shapeRenderer.setColor(colorSuelo);
        shapeRenderer.rect(0, 0, GAME_WIDTH, 60);

        // Línea del suelo
        shapeRenderer.setColor(colorTexto);
        shapeRenderer.rectLine(0, 60, GAME_WIDTH, 60, 2);

        // Dibujar dinosaurio con color del jugador
        shapeRenderer.setColor(playerColor);
        Dinosaurio dino = game.getDinosaurio();
        shapeRenderer.rect(dino.getX(), dino.getY(), dino.getAncho(), dino.getAlto());

        // Indicador visual de agachado
        if (dino.estaAgachado()) {
            shapeRenderer.setColor(1, 1, 0, 0.5f);
            shapeRenderer.rect(dino.getX() - 2, dino.getY() - 2,
                    dino.getAncho() + 4, dino.getAlto() + 4);
        }

        // Dibujar obstáculos
        for (int i = 0; i < game.getObstaculos().size(); i++) {
            Obstaculo obs = game.getObstaculos().get(i);
            if (obs.getTipo() == Obstaculo.TIPO_CACTUS) {
                shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1);
            } else {
                shapeRenderer.setColor(0.3f, 0.3f, 0.8f, 1);
            }
            shapeRenderer.rect(obs.getX(), obs.getY(), obs.getAncho(), obs.getAlto());
        }

        shapeRenderer.end();

        // Dibujar UI del jugador (dentro del piso)
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /**
     * Maneja la entrada del usuario
     */
    private void handleInput() {
        // Jugador 1 - WASD
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) ||
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            saltoJ1Pendiente = true;
        }
        agachadoJ1Pendiente = Gdx.input.isKeyPressed(Input.Keys.S);

        // Jugador 2 - Flechas
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            saltoJ2Pendiente = true;
        }
        agachadoJ2Pendiente = Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    @Override
    public void resize(int width, int height) {
        // Actualizar viewports
        viewportJugador1.update(width, height / 2, true);
        viewportJugador2.update(width, height / 2, true);
        stageGlobal.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        stageJugador1.dispose();
        stageJugador2.dispose();
        stageGlobal.dispose();
    }
}
