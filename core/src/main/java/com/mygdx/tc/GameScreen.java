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

import java.util.List;

public class GameScreen implements Screen {

    SpriteBatch batch;
    BitmapFont font;  // Para mostrar el dinero en pantalla
    Texture background;
    Texture enemyTexture;
    Texture towerTexture;
    Texture bulletTexture;

    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

    static LevelManager levelManager;

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();  // Inicializa el BitmapFont para el texto

        background = new Texture("background.png");
        enemyTexture = new Texture("enemy.png");
        towerTexture = new Texture("cannon.png");
        bulletTexture = new Texture("cannonBall.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 360); // menor resolución lógica = zoom

        levelManager = new LevelManager();
        levelManager.startLevel();
        levelManager.startNextWave();


    }

    @Override
    public void render(float delta) {
        // LIMPIAR PANTALLA
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // INPUT: colocar torre al tocar (fuera del camino)
        List<Vector2> points = levelManager.currentPath.waypoints;

        if (Gdx.input.justTouched()) {
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

            if (!isOnPath) {
                if (LevelManager.money >= 50) {
                    levelManager.towersThisLevel.add(new Tower(towerPos, towerTexture, bulletTexture));
                    LevelManager.money -= 50;
                } else {
                    System.out.println("No tienes suficiente dinero para colocar la torre.");
                }
            } else {
                System.out.println("No se puede colocar torre sobre el camino");
            }
        }

        // ACTUALIZAR TORRES
        for (Tower tower : levelManager.towersThisLevel) {
            tower.update(delta, levelManager.currentEnemies);
        }

        // ACTUALIZAR ENEMIGOS
        for (Enemy enemy : levelManager.currentEnemies) {
            enemy.update(delta);
        }

        // Eliminar enemigos muertos
        levelManager.currentEnemies.removeIf(Enemy::isDead);
        levelManager.removeDeadEnemies();

        // OLEADA TERMINADA → siguiente
        if (levelManager.isWaveFinished()) {
            levelManager.startNextWave();
        }

        // --- DIBUJAR BACKGROUND ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, 640, 360);
        batch.end();

        // --- DIBUJAR CAMINO ---
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f); // gris
        for (int i = 0; i < points.size() - 1; i++) {
            Vector2 a = points.get(i);
            Vector2 b = points.get(i + 1);
            shapeRenderer.rectLine(a, b, 40); // 40 px ancho de camino
        }
        shapeRenderer.end();

        // --- DIBUJAR BARRAS DE VIDA ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : levelManager.currentEnemies) {
            enemy.renderHealthBar(shapeRenderer);
        }
        shapeRenderer.end();

        // --- DIBUJAR TORRES Y ENEMIGOS ---
        batch.begin();
        for (Tower tower : levelManager.towersThisLevel) {
            tower.render(batch);
        }

        for (Enemy enemy : levelManager.currentEnemies) {
            enemy.render(batch, enemyTexture);
        }

        // --- DIBUJAR DINERO ---
        font.draw(batch, "Dinero: " + LevelManager.money, 10, 350);
        batch.end();
    }

    // CALCULAR DISTANCIA DE PUNTO A UN SEGMENTO DE LÍNEA
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

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        background.dispose();
        enemyTexture.dispose();
        towerTexture.dispose();
        bulletTexture.dispose();  // Asegúrate de liberar la textura de la bala
        font.dispose();  // Asegúrate de liberar el BitmapFont
    }
}
