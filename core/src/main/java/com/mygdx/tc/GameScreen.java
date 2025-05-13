package com.mygdx.tc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

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

        // Crear botones
        towerButton1 = new TextButton("Cannon", skin, "toggle");
        towerButton2 = new TextButton("Tesla", skin, "toggle");
        towerButton3 = new TextButton("Mago", skin, "toggle");

        // Agrupar botones para selección única
        ButtonGroup<Button> towerGroup = new ButtonGroup<>(towerButton1, towerButton2, towerButton3);
        towerGroup.setMaxCheckCount(1);
        towerGroup.setMinCheckCount(1);
        towerGroup.setUncheckLast(true);
        towerButton1.setChecked(true); // Selección inicial

        // Cambiar tamaño del texto
        towerButton1.getLabel().setFontScale(2.5f);
        towerButton2.getLabel().setFontScale(2.5f);
        towerButton3.getLabel().setFontScale(2.5f);

        // Crear tabla para los botones
        Table table = new Table();
        table.bottom().right().pad(20);
        table.setFillParent(true);
        table.add(towerButton1).width(150).height(50).pad(5).row();
        table.add(towerButton2).width(150).height(50).pad(5).row();
        table.add(towerButton3).width(150).height(50).pad(5).row();

        stage.addActor(table);

        // Listeners para cambiar tipo de torre
        towerButton1.addListener(e -> { selectedTowerType = 1; return false; });
        towerButton2.addListener(e -> { selectedTowerType = 2; return false; });
        towerButton3.addListener(e -> { selectedTowerType = 3; return false; });
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

                if (!isOnPath && LevelManager.money >= 50) {
                    Tower newTower = new Tower(towerPos, selectedTowerType);
                    levelManager.towersThisLevel.add(newTower);
                    LevelManager.money -= 50;
                }
            }
        }

        // Lógica del juego
        for (Tower tower : levelManager.towersThisLevel) {
            tower.update(delta, levelManager.currentEnemies);
        }

        for (Enemy enemy : levelManager.currentEnemies) {
            enemy.update(delta);
        }

        levelManager.removeDeadEnemies();

        if (levelManager.lives <= 0) {
            System.out.println("¡Game Over!");
            return;
        }

        if (levelManager.isWaveFinished()) {
            levelManager.startNextWave();
        }

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
        for (Tower tower : levelManager.towersThisLevel) {
            tower.render(batch);
        }
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

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        background.dispose();
        enemyTexture.dispose();
        font.dispose();
        stage.dispose();
    }
}
