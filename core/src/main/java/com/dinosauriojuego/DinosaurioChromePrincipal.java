package com.dinosauriojuego;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.dinosauriojuego.pantallas.DinosaurioGameScreen;

/**
 * Clase principal de la aplicación
 */
public class DinosaurioChromePrincipal extends ApplicationAdapter {
    private DinosaurioGameScreen gameScreen;
    private Skin skin;

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        gameScreen = new DinosaurioGameScreen(skin);
        gameScreen.show();
    }

    @Override
    public void render() {
        gameScreen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        gameScreen.resize(width, height);
    }

    @Override
    public void dispose() {
        gameScreen.dispose();
        skin.dispose();
    }
}
