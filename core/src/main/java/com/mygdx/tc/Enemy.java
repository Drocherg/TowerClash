package com.mygdx.tc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    Vector2 position;
    int currentWaypoint;
    float speed = 60f;
    Path path;
    public float health = 100;

    public Enemy(Path path) {
        this.path = path;
        this.position = new Vector2(path.waypoints.get(0));
        this.currentWaypoint = 1;
    }

    public void update(float delta) {
        if (currentWaypoint >= path.waypoints.size()) return;

        Vector2 target = path.waypoints.get(currentWaypoint);
        Vector2 direction = new Vector2(target).sub(position).nor();
        position.add(direction.scl(speed * delta));

        if (position.dst(target) < 5f) currentWaypoint++;
    }

    public void render(SpriteBatch batch, Texture texture) {
        batch.draw(texture, position.x - 16, position.y - 16, 32, 32); // enemigo más pequeño
    }
    public void renderHealthBar(ShapeRenderer shapeRenderer) {
        float barWidth = 32;
        float barHeight = 4;
        float x = position.x - barWidth / 2;
        float y = position.y + 20;

        float healthRatio = Math.max(health / 100f, 0);

        shapeRenderer.setColor(1, 0, 0, 1); // rojo (fondo)
        shapeRenderer.rect(x, y, barWidth, barHeight);

        shapeRenderer.setColor(0, 1, 0, 1); // verde (salud restante)
        shapeRenderer.rect(x, y, barWidth * healthRatio, barHeight);
    }


    // Método para recibir daño de las balas
    public void takeDamage(int amount) {
        health -= amount;

    }

    public boolean isDead() {
        if (health <= 0) {
            GameScreen.levelManager.enemyKilled(); // o LevelManager.levelManager...
            return true;
        }
        return false;
    }
}
