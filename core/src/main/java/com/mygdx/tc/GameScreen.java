package com.mygdx.tc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

import java.util.List;
import java.util.ArrayList;

public class GameScreen implements Screen {

    SpriteBatch batch;
    BitmapFont font;
    Texture background;
    Texture enemyTexture;

    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

    public static LevelManager levelManager;
    private Stage stage;
    private Skin skin;
    private TextButton towerButton1, towerButton2, towerButton3;
    private int selectedTowerType = 1;
    private GameMain game;

    // Constructor que recibe GameMain
    public GameScreen(GameMain game) {
        this.game = game; // Guardar la instancia de GameMain
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont(Gdx.files.internal("default.fnt"));

        background = new Texture("background.png");
        enemyTexture = new Texture("enemy.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 360);

        levelManager = new LevelManager();
        levelManager.startLevel();
        levelManager.startNextWave();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear botones con nombres y costos
        towerButton1 = createTowerButton(1);
        towerButton2 = createTowerButton(2);
        towerButton3 = createTowerButton(3);

        // Agrupar botones para selección única
        ButtonGroup<Button> towerGroup = new ButtonGroup<>(towerButton1, towerButton2, towerButton3);
        towerGroup.setMaxCheckCount(1);
        towerGroup.setMinCheckCount(1);
        towerGroup.setUncheckLast(true);
        towerButton1.setChecked(true); // Selección inicial

        // Crear tabla para los botones
        Table table = new Table();
        table.bottom().right().pad(20);
        table.setFillParent(true);
        table.add(towerButton1).width(220).height(70).pad(8).row();
        table.add(towerButton2).width(220).height(70).pad(8).row();
        table.add(towerButton3).width(220).height(70).pad(8).row();

        stage.addActor(table);

        // Listeners para cambiar tipo de torre
        towerButton1.addListener(e -> {
            selectedTowerType = 1;
            return false;
        });

        towerButton2.addListener(e -> {
            selectedTowerType = 2;
            return false;
        });

        towerButton3.addListener(e -> {
            selectedTowerType = 3;
            return false;
        });
    }

    // Metodo para crear botones de torre con estilo personalizado
    private TextButton createTowerButton(int towerType) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
            skin.get("toggle", TextButton.TextButtonStyle.class));

        // Personalizar colores según el tipo de torre
        switch (towerType) {
            case 1: // Cañón - Rojo oscuro
                style.fontColor = new Color(0.8f, 0.2f, 0.2f, 1);
                style.downFontColor = new Color(1f, 0.5f, 0.5f, 1);
                style.checkedFontColor = new Color(1f, 0.3f, 0.3f, 1);
                break;
            case 2: // Tesla - Azul eléctrico
                style.fontColor = new Color(0.2f, 0.4f, 0.8f, 1);
                style.downFontColor = new Color(0.5f, 0.7f, 1f, 1);
                style.checkedFontColor = new Color(0.3f, 0.6f, 1f, 1);
                break;
            case 3: // Mago - Púrpura
                style.fontColor = new Color(0.6f, 0.2f, 0.8f, 1);
                style.downFontColor = new Color(0.8f, 0.5f, 1f, 1);
                style.checkedFontColor = new Color(0.7f, 0.3f, 1f, 1);
                break;
        }

        TextButton button = new TextButton(Tower.getNameForType(towerType) + "\n" + Tower.getCostForType(towerType) + " monedas", style);
        button.getLabel().setFontScale(2.2f);
        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        List<Vector2> points = levelManager.currentPath.waypoints;

        // Manejar colocación de torres solo si no se tocó UI
        if (Gdx.input.justTouched()) {
            Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            if (stage.hit(stageCoords.x, stageCoords.y, true) == null) {
                Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touch);
                Vector2 towerPos = new Vector2(touch.x, touch.y);

                boolean isOnPath = false;
                for (int i = 0; i < points.size() - 1; i++) {
                    Vector2 a = points.get(i);
                    Vector2 b = points.get(i + 1);
                    float dist = distanceToSegment(a, b, towerPos);
                    if (dist < 40) {
                        isOnPath = true;
                        break;
                    }
                }

                // Obtener el costo de la torre seleccionada
                int towerCost = Tower.getCostForType(selectedTowerType);

                if (!isOnPath && LevelManager.money >= towerCost) {
                    Tower newTower = new Tower(towerPos, selectedTowerType);
                    levelManager.towersThisLevel.add(newTower);
                    LevelManager.money -= towerCost;
                }
            }
        }

        // Actualizar el generador de enemigos
        levelManager.update(delta);

        // Actualizar torres
        for (Tower tower : levelManager.towersThisLevel) {
            tower.update(delta, levelManager.currentEnemies);
        }

        // Actualizar todos los enemigos
        for (Enemy enemy : new ArrayList<>(levelManager.currentEnemies)) {
            enemy.update(delta);
        }

        // Verificar game over
        if (levelManager.lives <= 0) {
            System.out.println("¡Game Over!");
            game.showGameOverScreen();
            return;
        }

        // Verificar si la oleada ha terminado
        if (levelManager.isWaveFinished()) {
            levelManager.startNextWave();
        }

        // IMPORTANTE: Eliminar enemigos muertos DESPUÉS de que se hayan actualizado
        levelManager.removeDeadEnemies();

        // Dibujo en orden: fondo, camino, torres, enemigos, interfaz
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, 640, 360);
        batch.end();

        // Dibujar el camino
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        for (int i = 0; i < points.size() - 1; i++) {
            shapeRenderer.rectLine(points.get(i), points.get(i + 1), 40);
        }
        shapeRenderer.end();

        // Dibujar las torres y enemigos encima del camino
        batch.begin();
        font.draw(batch, "Vidas: " + levelManager.lives, 10, 330);
        font.draw(batch, "Dinero: " + LevelManager.money, 10, 350);
        font.draw(batch, "Oleada: " + levelManager.currentWave, 10, 310);
        font.draw(batch, "Enemigos: " + levelManager.currentEnemies.size() +
            " (+" + levelManager.enemiesToSpawn + " por generar)", 10, 290);

        // Renderizar torres
        for (Tower tower : levelManager.towersThisLevel) {
            tower.render(batch);
        }

        // Renderizar enemigos
        for (Enemy enemy : levelManager.currentEnemies) {
            enemy.render(batch, enemyTexture);
        }
        batch.end();

        // Barra de vida de enemigos
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : levelManager.currentEnemies) {
            enemy.renderHealthBar(shapeRenderer);
        }
        shapeRenderer.end();

        // Dibujar interfaz
        stage.act(delta);
        stage.draw();
    }

    public float distanceToSegment(Vector2 A, Vector2 B, Vector2 P) {
        Vector2 AB = new Vector2(B).sub(A);
        Vector2 AP = new Vector2(P).sub(A);
        float t = AP.dot(AB) / AB.len2();
        t = Math.max(0, Math.min(1, t));
        Vector2 projection = new Vector2(A).add(AB.scl(t));
        return P.dst(projection);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        background.dispose();
        enemyTexture.dispose();
        font.dispose();
        stage.dispose();
    }
}
