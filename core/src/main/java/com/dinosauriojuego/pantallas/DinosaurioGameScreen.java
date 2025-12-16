package com.dinosauriojuego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private static final float GAME_WIDTH = 1200;
    private static final float GAME_HEIGHT = 360;

    private DinosaurioGame gameJugador1;
    private DinosaurioGame gameJugador2;

    private OrthographicCamera cameraJugador1;
    private OrthographicCamera cameraJugador2;
    private Viewport viewportJugador1;
    private Viewport viewportJugador2;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;

    // Texturas - Dinosaurios con animación
    private Texture dinoCyan1;
    private Texture dinoCyan2;
    private Texture dinoOrange1;
    private Texture dinoOrange2;
    private Texture dinoAgachado1;
    private Texture dinoAgachado2;

    // Texturas - Obstáculos
    private Texture cactusTexture;
    private Texture pajaro1Texture;
    private Texture pajaro2Texture;

    // Textura de fondo
    private Texture fondoTexture;
    private float fondoOffsetJ1;
    private float fondoOffsetJ2;

    // Textura del botón reiniciar
    private Texture botonReiniciarTexture;

    // UI
    private Stage stageJugador1;
    private Stage stageJugador2;
    private Stage stageGlobal;
    private Skin skin;

    private Label puntuacionJ1Label;
    private Label jugador1Label;
    private Label puntuacionJ2Label;
    private Label jugador2Label;
    private Label instruccionesJ1Label;
    private Label instruccionesJ2Label;
    private Label ganadorLabel;
    private Label reiniciarLabel;

    // Rectángulo del botón reiniciar para detectar clics
    private float botonReiniciarX;
    private float botonReiniciarY;
    private float botonReiniciarAncho;
    private float botonReiniciarAlto;

    private float tiempoInstrucciones;
    private static final float TIEMPO_MOSTRAR_INSTRUCCIONES = 3.0f;

    private boolean saltoJ1Pendiente;
    private boolean agachadoJ1Pendiente;
    private boolean saltoJ2Pendiente;
    private boolean agachadoJ2Pendiente;
    private boolean juegoTerminado;

    public DinosaurioGameScreen(Skin skin) {
        this.skin = skin;

        this.gameJugador1 = new DinosaurioGame(GAME_WIDTH, GAME_HEIGHT);
        this.gameJugador2 = new DinosaurioGame(GAME_WIDTH, GAME_HEIGHT);

        this.cameraJugador1 = new OrthographicCamera();
        this.cameraJugador1.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        this.cameraJugador2 = new OrthographicCamera();
        this.cameraJugador2.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        this.viewportJugador1 = new FitViewport(GAME_WIDTH, GAME_HEIGHT, cameraJugador1);
        this.viewportJugador2 = new FitViewport(GAME_WIDTH, GAME_HEIGHT, cameraJugador2);

        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();

        this.stageJugador1 = new Stage(viewportJugador1);
        this.stageJugador2 = new Stage(viewportJugador2);
        this.stageGlobal = new Stage(new FitViewport(GAME_WIDTH, 720));

        this.saltoJ1Pendiente = false;
        this.agachadoJ1Pendiente = false;
        this.saltoJ2Pendiente = false;
        this.agachadoJ2Pendiente = false;

        this.tiempoInstrucciones = 0;
        this.juegoTerminado = false;

        this.fondoOffsetJ1 = 0;
        this.fondoOffsetJ2 = 0;

        cargarTexturas();
        setupUI();
        Gdx.input.setInputProcessor(stageGlobal);
    }

    private void cargarTexturas() {
        try {
            dinoCyan1 = new Texture(Gdx.files.internal("dino1.png"));
            dinoCyan2 = new Texture(Gdx.files.internal("dino2.png"));
            dinoOrange1 = new Texture(Gdx.files.internal("dino1.png"));
            dinoOrange2 = new Texture(Gdx.files.internal("dino2.png"));
            dinoAgachado1 = new Texture(Gdx.files.internal("dinoAgachado1.png"));
            dinoAgachado2 = new Texture(Gdx.files.internal("dinoAgachado2.png"));
            cactusTexture = new Texture(Gdx.files.internal("cactus.png"));
            pajaro1Texture = new Texture(Gdx.files.internal("pajaro1.png"));
            pajaro2Texture = new Texture(Gdx.files.internal("pajaro2.png"));
            fondoTexture = new Texture(Gdx.files.internal("fondo.png"));
            fondoTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            botonReiniciarTexture = new Texture(Gdx.files.internal("reiniciar.png"));
        } catch (Exception e) {
            System.out.println("No se pudieron cargar algunas texturas, se usarán colores sólidos");
        }
    }

    private void setupUI() {
        jugador1Label = new Label("JUGADOR 1", skin, "default");
        jugador1Label.setFontScale(2.5f);
        jugador1Label.setColor(Color.BLACK);
        jugador1Label.setPosition(20, 20);
        stageJugador1.addActor(jugador1Label);

        puntuacionJ1Label = new Label("Puntos: 0", skin, "default");
        puntuacionJ1Label.setFontScale(2.5f);
        puntuacionJ1Label.setColor(Color.BLACK);
        puntuacionJ1Label.setPosition(350, 20);
        stageJugador1.addActor(puntuacionJ1Label);

        // Instrucciones centradas para Jugador 1
        instruccionesJ1Label = new Label("W/ESPACIO saltar | S agacharse", skin, "default");
        instruccionesJ1Label.setFontScale(2.0f);
        instruccionesJ1Label.setColor(Color.BLACK);
        float anchoJ1 = instruccionesJ1Label.getWidth() * 2.0f;
        instruccionesJ1Label.setPosition((GAME_WIDTH - anchoJ1) / 2, 180);
        instruccionesJ1Label.setVisible(true);
        stageJugador1.addActor(instruccionesJ1Label);

        jugador2Label = new Label("JUGADOR 2", skin, "default");
        jugador2Label.setFontScale(2.5f);
        jugador2Label.setColor(Color.BLACK);
        jugador2Label.setPosition(20, 20);
        stageJugador2.addActor(jugador2Label);

        puntuacionJ2Label = new Label("Puntos: 0", skin, "default");
        puntuacionJ2Label.setFontScale(2.5f);
        puntuacionJ2Label.setColor(Color.BLACK);
        puntuacionJ2Label.setPosition(350, 20);
        stageJugador2.addActor(puntuacionJ2Label);

        // Instrucciones centradas para Jugador 2
        instruccionesJ2Label = new Label("FLECHA ARRIBA saltar | FLECHA ABAJO agacharse", skin, "default");
        instruccionesJ2Label.setFontScale(2.0f);
        instruccionesJ2Label.setColor(Color.BLACK);
        float anchoJ2 = instruccionesJ2Label.getWidth() * 2.0f;
        instruccionesJ2Label.setPosition((GAME_WIDTH - anchoJ2) / 2, 180);
        instruccionesJ2Label.setVisible(true);
        stageJugador2.addActor(instruccionesJ2Label);

        ganadorLabel = new Label("", skin, "default");
        ganadorLabel.setFontScale(5.0f);
        ganadorLabel.setColor(Color.BLACK);
        ganadorLabel.setVisible(false);
        stageGlobal.addActor(ganadorLabel);

        reiniciarLabel = new Label("Presiona ESPACIO para jugar de nuevo", skin, "default");
        reiniciarLabel.setFontScale(2.8f);
        reiniciarLabel.setColor(Color.BLACK);
        reiniciarLabel.setVisible(false);
        stageGlobal.addActor(reiniciarLabel);

        // Tamaño y posición del botón reiniciar
        botonReiniciarAncho = 200;
        botonReiniciarAlto = 200;
        botonReiniciarX = (GAME_WIDTH - botonReiniciarAncho) / 2;
        botonReiniciarY = 250;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        handleInput();

        if (tiempoInstrucciones < TIEMPO_MOSTRAR_INSTRUCCIONES && !juegoTerminado) {
            tiempoInstrucciones += delta;

            if (tiempoInstrucciones >= TIEMPO_MOSTRAR_INSTRUCCIONES - 0.5f) {
                float fadeTime = TIEMPO_MOSTRAR_INSTRUCCIONES - tiempoInstrucciones;
                float alpha = fadeTime / 0.5f;

                Color colorJ1 = instruccionesJ1Label.getColor();
                colorJ1.a = alpha;
                instruccionesJ1Label.setColor(colorJ1);

                Color colorJ2 = instruccionesJ2Label.getColor();
                colorJ2.a = alpha;
                instruccionesJ2Label.setColor(colorJ2);
            }
        } else if (!juegoTerminado) {
            instruccionesJ1Label.setVisible(false);
            instruccionesJ2Label.setVisible(false);
        }

        if (!juegoTerminado) {
            gameJugador1.update(delta, saltoJ1Pendiente, agachadoJ1Pendiente);
            gameJugador2.update(delta, saltoJ2Pendiente, agachadoJ2Pendiente);

            // Actualizar offset del fondo basado en la velocidad del juego
            fondoOffsetJ1 += gameJugador1.getVelocidadJuego() * delta;
            fondoOffsetJ2 += gameJugador2.getVelocidadJuego() * delta;

            if (gameJugador1.isGameOver() || gameJugador2.isGameOver()) {
                juegoTerminado = true;
            }
        }

        saltoJ1Pendiente = false;
        saltoJ2Pendiente = false;

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderGame(gameJugador1, viewportJugador1, cameraJugador1, stageJugador1, 0, 360, Color.CYAN, dinoCyan1, dinoCyan2, fondoOffsetJ1);
        renderGame(gameJugador2, viewportJugador2, cameraJugador2, stageJugador2, 0, 0, Color.ORANGE, dinoOrange1, dinoOrange2, fondoOffsetJ2);

        puntuacionJ1Label.setText("Puntos: " + gameJugador1.getPuntuacion());
        puntuacionJ1Label.setColor(Color.BLACK);

        puntuacionJ2Label.setText("Puntos: " + gameJugador2.getPuntuacion());
        puntuacionJ2Label.setColor(Color.BLACK);

        if (!juegoTerminado && tiempoInstrucciones < TIEMPO_MOSTRAR_INSTRUCCIONES) {
            stageGlobal.act(delta);
            stageGlobal.draw();
        }

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.setProjectionMatrix(stageGlobal.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(0, 358, GAME_WIDTH, 4);
        shapeRenderer.end();

        if (juegoTerminado) {
            mostrarPantallaGanador();
        }
    }

    private void mostrarPantallaGanador() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
        ganadorLabel.setColor(Color.BLACK);
        ganadorLabel.setVisible(true);
        ganadorLabel.setPosition(
            (GAME_WIDTH - ganadorLabel.getWidth() * 5.0f) / 2,
            500
        );

        stageGlobal.getViewport().apply();
        stageGlobal.draw();

        // Dibujar botón de reiniciar
        batch.setProjectionMatrix(stageGlobal.getCamera().combined);
        batch.begin();
        if (botonReiniciarTexture != null) {
            batch.draw(botonReiniciarTexture, botonReiniciarX, botonReiniciarY, botonReiniciarAncho, botonReiniciarAlto);
        }
        batch.end();

        // Detectar clic en el botón reiniciar
        if (Gdx.input.justTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Convertir coordenadas de pantalla a coordenadas del juego
            float factorX = GAME_WIDTH / (float) Gdx.graphics.getWidth();
            float factorY = 720 / (float) Gdx.graphics.getHeight();
            mouseX *= factorX;
            mouseY *= factorY;

            if (mouseX >= botonReiniciarX && mouseX <= botonReiniciarX + botonReiniciarAncho &&
                mouseY >= botonReiniciarY && mouseY <= botonReiniciarY + botonReiniciarAlto) {
                reiniciarJuego();
            }
        }

        // También permitir reiniciar con ESPACIO
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            reiniciarJuego();
        }
    }

    private void reiniciarJuego() {
        gameJugador1.reset();
        gameJugador2.reset();
        ganadorLabel.setVisible(false);
        reiniciarLabel.setVisible(false);
        juegoTerminado = false;
        fondoOffsetJ1 = 0;
        fondoOffsetJ2 = 0;

        tiempoInstrucciones = 0;
        instruccionesJ1Label.setVisible(true);
        instruccionesJ2Label.setVisible(true);

        Color colorInstruccionesJ1 = new Color(Color.BLACK);
        colorInstruccionesJ1.a = 1.0f;
        instruccionesJ1Label.setColor(colorInstruccionesJ1);

        Color colorInstruccionesJ2 = new Color(Color.BLACK);
        colorInstruccionesJ2.a = 1.0f;
        instruccionesJ2Label.setColor(colorInstruccionesJ2);
    }

    private void renderGame(DinosaurioGame game, Viewport viewport, OrthographicCamera camera,
                            Stage stage, int offsetX, int offsetY, Color playerColor,
                            Texture dinoTexture1, Texture dinoTexture2, float fondoOffset) {
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

        viewport.apply();
        viewport.setScreenBounds(offsetX, offsetY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(offsetX, offsetY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
        Gdx.gl.glClearColor(colorFondo.r, colorFondo.g, colorFondo.b, colorFondo.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Dibujar fondo repetido scrolleando
        if (fondoTexture != null) {
            float fondoAncho = fondoTexture.getWidth();
            float fondoAlto = 60; // Altura del suelo

            // Calcular cuántas veces repetir el fondo
            int repeticiones = (int) Math.ceil(GAME_WIDTH / fondoAncho) + 2;

            // Calcular offset normalizado
            float offsetNormalizado = fondoOffset % fondoAncho;

            // Dibujar el fondo repetido
            for (int i = -1; i < repeticiones; i++) {
                float x = i * fondoAncho - offsetNormalizado;
                batch.draw(fondoTexture, x, 22, fondoAncho, fondoAlto);
            }
        } else {
            // Si no hay textura de fondo, dibujar el rectángulo de color
            batch.end();
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(colorSuelo);
            shapeRenderer.rect(0, 0, GAME_WIDTH, 60);
            shapeRenderer.end();
            batch.begin();
        }

        Dinosaurio dino = game.getDinosaurio();

        // Dibujar dinosaurio con sprite animado
        if (dinoTexture1 != null && dinoTexture2 != null && dinoAgachado1 != null && dinoAgachado2 != null) {
            batch.setColor(playerColor);

            Texture texturaDino;
            if (dino.estaAgachado()) {
                texturaDino = (dino.getSpriteActual() == 0) ? dinoAgachado1 : dinoAgachado2;
            } else {
                texturaDino = (dino.getSpriteActual() == 0) ? dinoTexture1 : dinoTexture2;
            }

            batch.draw(texturaDino, dino.getX(), dino.getY(), dino.getAncho(), dino.getAlto());
            batch.setColor(Color.WHITE);
        } else {
            batch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(playerColor);
            shapeRenderer.rect(dino.getX(), dino.getY(), dino.getAncho(), dino.getAlto());
            shapeRenderer.end();
            batch.begin();
        }

        // Dibujar obstáculos
        for (int i = 0; i < game.getObstaculos().size(); i++) {
            Obstaculo obs = game.getObstaculos().get(i);

            if (obs.getTipo() == Obstaculo.TIPO_CACTUS && cactusTexture != null) {
                batch.draw(cactusTexture, obs.getX(), obs.getY(), obs.getAncho(), obs.getAlto());
            } else if (obs.getTipo() == Obstaculo.TIPO_PAJARO) {
                Texture pajaroTexture = (obs.getSpriteActual() == 0) ? pajaro1Texture : pajaro2Texture;
                if (pajaroTexture != null) {
                    batch.draw(pajaroTexture, obs.getX(), obs.getY(), obs.getAncho(), obs.getAlto());
                } else {
                    batch.end();
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(0.3f, 0.3f, 0.8f, 1);
                    shapeRenderer.rect(obs.getX(), obs.getY(), obs.getAncho(), obs.getAlto());
                    shapeRenderer.end();
                    batch.begin();
                }
            } else {
                batch.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                if (obs.getTipo() == Obstaculo.TIPO_CACTUS) {
                    shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1);
                } else {
                    shapeRenderer.setColor(0.3f, 0.3f, 0.8f, 1);
                }
                shapeRenderer.rect(obs.getX(), obs.getY(), obs.getAncho(), obs.getAlto());
                shapeRenderer.end();
                batch.begin();
            }
        }

        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) ||
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            saltoJ1Pendiente = true;
        }
        agachadoJ1Pendiente = Gdx.input.isKeyPressed(Input.Keys.S);

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            saltoJ2Pendiente = true;
        }
        agachadoJ2Pendiente = Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    @Override
    public void resize(int width, int height) {
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
        batch.dispose();
        stageJugador1.dispose();
        stageJugador2.dispose();
        stageGlobal.dispose();

        if (dinoCyan1 != null) dinoCyan1.dispose();
        if (dinoCyan2 != null) dinoCyan2.dispose();
        if (dinoOrange1 != null) dinoOrange1.dispose();
        if (dinoOrange2 != null) dinoOrange2.dispose();
        if (dinoAgachado1 != null) dinoAgachado1.dispose();
        if (dinoAgachado2 != null) dinoAgachado2.dispose();
        if (cactusTexture != null) cactusTexture.dispose();
        if (pajaro1Texture != null) pajaro1Texture.dispose();
        if (pajaro2Texture != null) pajaro2Texture.dispose();
        if (fondoTexture != null) fondoTexture.dispose();
        if (botonReiniciarTexture != null) botonReiniciarTexture.dispose();
    }
}
