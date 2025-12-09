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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dinosauriojuego.logica.DinosaurioGame;
import com.dinosauriojuego.logica.Dinosaurio;
import com.dinosauriojuego.logica.Obstaculo;

/**
 * Pantalla principal del juego
 */
public class DinosaurioGameScreen implements Screen {
    // Lógica del juego
    private DinosaurioGame game;

    // Renderizado
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    // UI
    private Stage stage;
    private Skin skin;
    private Label puntuacionLabel;
    private Label highScoreLabel;
    private Label gameOverLabel;
    private Label instruccionesLabel;

    // Input
    private boolean saltoPendiente;
    private boolean agachadoPendiente;

    public DinosaurioGameScreen(Skin skin) {
        this.skin = skin;
        this.game = new DinosaurioGame(800, 480);
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 480);
        this.shapeRenderer = new ShapeRenderer();
        this.stage = new Stage(new FitViewport(800, 480));
        this.saltoPendiente = false;
        this.agachadoPendiente = false;

        setupUI();
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Configura los elementos de la interfaz
     */
    private void setupUI() {
        // Tabla superior con puntuaciones
        Table table = new Table();
        table.setFillParent(true);
        table.top().left();
        table.padLeft(10).padTop(10);

        highScoreLabel = new Label("HI: 0", skin);
        puntuacionLabel = new Label("Puntos: 0", skin);

        table.add(highScoreLabel).padRight(20);
        table.add(puntuacionLabel);

        stage.addActor(table);

        // Game Over label (inicialmente oculto)
        gameOverLabel = new Label("GAME OVER", skin, "window");
        gameOverLabel.setPosition(
            (800 - gameOverLabel.getWidth()) / 2,
            300
        );
        gameOverLabel.setVisible(false);
        stage.addActor(gameOverLabel);

        // Instrucciones para reiniciar
        instruccionesLabel = new Label("Presiona ESPACIO para reiniciar", skin);
        instruccionesLabel.setPosition(
            (800 - instruccionesLabel.getWidth()) / 2,
            250
        );
        instruccionesLabel.setVisible(false);
        stage.addActor(instruccionesLabel);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        handleInput();

        game.update(delta, saltoPendiente, agachadoPendiente);
        saltoPendiente = false;

        // Colores según modo día/noche
        Color colorFondo;
        Color colorSuelo;
        Color colorTexto;
        Color colorDino;

        if (game.isModoNoche()) {
            colorFondo = new Color(0.1f, 0.1f, 0.1f, 1);
            colorSuelo = new Color(0.3f, 0.3f, 0.3f, 1);
            colorTexto = Color.WHITE;
            colorDino = new Color(0.8f, 0.8f, 0.8f, 1);
        } else {
            colorFondo = Color.WHITE;
            colorSuelo = new Color(0.5f, 0.5f, 0.5f, 1);
            colorTexto = Color.BLACK;
            colorDino = new Color(0.2f, 0.6f, 0.2f, 1);
        }

        // Limpiar pantalla
        Gdx.gl.glClearColor(colorFondo.r, colorFondo.g, colorFondo.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Renderizar juego
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar suelo
        shapeRenderer.setColor(colorSuelo);
        shapeRenderer.rect(0, 0, 800, 60);

        // Línea del suelo
        shapeRenderer.setColor(colorTexto);
        shapeRenderer.rectLine(0, 60, 800, 60, 2);

        // Dibujar dinosaurio
        shapeRenderer.setColor(colorDino);
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

        // Actualizar y renderizar UI
        puntuacionLabel.setText("Puntos: " + game.getPuntuacion());
        highScoreLabel.setText("HI: " + game.getHighScore());

        // Cambiar color de texto según modo
        puntuacionLabel.setColor(colorTexto);
        highScoreLabel.setColor(colorTexto);

        stage.act(delta);
        stage.draw();

        // Mostrar Game Over
        if (game.isGameOver()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(0, 0, 800, 480);
            shapeRenderer.end();

            gameOverLabel.setVisible(true);
            instruccionesLabel.setVisible(true);

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                game.reset();
                gameOverLabel.setVisible(false);
                instruccionesLabel.setVisible(false);
            }
        } else {
            gameOverLabel.setVisible(false);
            instruccionesLabel.setVisible(false);
        }
    }

    /**
     * Maneja la entrada del usuario
     */
    private void handleInput() {
        // Saltar con ESPACIO o FLECHA ARRIBA
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            saltoPendiente = true;
        }

        // Agacharse con FLECHA ABAJO
        agachadoPendiente = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        // Touch para móviles
        if (Gdx.input.justTouched()) {
            saltoPendiente = true;
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }
}
