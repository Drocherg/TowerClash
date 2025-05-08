package com.mygdx.tc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameMain extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        this.setScreen(new MainMenuScreen(this)); // inicia con el menú
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
