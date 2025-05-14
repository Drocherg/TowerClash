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

    public void startGame() {
        // Al iniciar el juego o reiniciar, pasa la instancia de GameMain a GameScreen
        this.setScreen(new GameScreen(this)); // Asegúrate de pasar la instancia de GameMain
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void showGameOverScreen() {
        this.setScreen(new GameOverScreen(this)); // Puedes cambiar a GameOverScreen cuando sea necesario
    }
}
