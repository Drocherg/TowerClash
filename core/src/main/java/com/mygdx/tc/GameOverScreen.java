package com.mygdx.tc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen implements Screen {

    private final GameMain game;
    private Stage stage;
    private Skin skin;

    public GameOverScreen(GameMain game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear fuente grande
        BitmapFont font = new BitmapFont(Gdx.files.internal("default.fnt"));
        font.getData().setScale(2.5f); // Aumentamos el tamaño de fuente

        // Estilo personalizado para el texto Game Over
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label gameOverLabel = new Label("¡Game Over!", labelStyle);

        // Crear botones grandes
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable("default-round");
        buttonStyle.down = skin.getDrawable("default-round-down");
        buttonStyle.font = font;

        TextButton retryButton = new TextButton("Volver a jugar", buttonStyle);
        TextButton exitButton = new TextButton("Salir", buttonStyle);

        // Listeners con ChangeListener en lugar de lambda
        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Botón Volver a jugar presionado");
                // Reiniciar valores estáticos
                LevelManager.lives = 20;
                LevelManager.money = 100;
                // Crear nueva pantalla de juego
                game.startGame();
                dispose(); // Importante: liberar recursos
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Tabla para organizar elementos
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(gameOverLabel).padBottom(60).row();
        table.add(retryButton).width(300).height(100).padBottom(30).row();
        table.add(exitButton).width(300).height(100).row();

        stage.addActor(table);
    }

    @Override public void show() {}
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.85f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
