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
        // Reiniciar valores estáticos
        LevelManager.lives = 20;
        LevelManager.money = 100;

        // Reiniciar contador de IDs de enemigos
        Enemy.resetIDCounter();

        this.setScreen(new GameScreen(this));
        System.out.println("Iniciando nueva partida");
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void showGameOverScreen() {
        this.setScreen(new GameOverScreen(this));
    }
}
