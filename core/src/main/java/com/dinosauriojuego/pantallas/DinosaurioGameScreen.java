package com.dinosauriojuego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dinosauriojuego.logica.DinosaurioGame;
import com.dinosauriojuego.logica.Obstaculo;

public class DinosaurioGameScreen implements Screen {
    private DinosaurioGame game;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private Skin skin;
    private Label puntuacionLabel;
    private boolean saltoPendiente;

    public DinosaurioGameScreen(Skin skin) {
        this.skin = skin;
        this.game = new DinosaurioGame(800, 480);
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 480);
        this.shapeRenderer = new ShapeRenderer();
        this.stage = new Stage(new FitViewport(800, 480));
        this.saltoPendiente = false;

        setupUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.top().left();
        table.padLeft(10).padTop(10);

        puntuacionLabel = new Label("Puntos: 0", skin);
        table.add(puntuacionLabel);

        stage.addActor(table);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        handleInput();

        game.update(delta, saltoPendiente);
        saltoPendiente = false;

        // Limpiar pantalla
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Renderizar juego
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar suelo
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(0, 0, 800, 60);

        // Dibujar dinosaurio
        shapeRenderer.setColor(0.2f, 0.6f, 0.2f, 1);
        var dino = game.getDinosaurio();
        shapeRenderer.rect(dino.getX(), dino.getY(), dino.getAncho(), dino.getAlto());

        // Dibujar obstáculos
        for (Obstaculo obs : game.getObstaculos()) {
            if (obs.getTipo() == Obstaculo.TIPO_CACTUS) {
                shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1);
            } else {
                shapeRenderer.setColor(0.3f, 0.3f, 0.8f, 1);
            }
            shapeRenderer.rect(obs.getX(), obs.getY(), obs.getAncho(), obs.getAlto());
        }

        // Línea de cielo
        shapeRenderer.setColor(0.7f, 0.9f, 1, 1);
        shapeRenderer.rect(0, 60, 800, 420);

        shapeRenderer.end();

        // Actualizar y renderizar UI
        stage.act(delta);
        stage.draw();

        puntuacionLabel.setText("Puntos: " + game.getPuntuacion());

        // Mostrar Game Over
        if (game.isGameOver()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.5f);
            shapeRenderer.rect(0, 0, 800, 480);
            shapeRenderer.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                game.reset();
            }
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            saltoPendiente = true;
        }

        if (Gdx.input.isTouched()) {
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
