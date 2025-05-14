package com.mygdx.tc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Enemy {
    public Vector2 position;
    public int currentWaypoint = 0;
    public float speed = 60f;
    public int maxHealth = 100;
    public int currentHealth = maxHealth;
    private boolean isDead = false;
    private boolean reachedEnd = false;

    private List<Vector2> path;

    public Enemy(Path path) {
        this.path = path.waypoints;
        this.position = new Vector2(this.path.get(0));
    }

    public void update(float delta) {
        if (isDead || reachedEnd) return;

        if (currentWaypoint < path.size()) {
            Vector2 target = path.get(currentWaypoint);
            Vector2 direction = new Vector2(target).sub(position).nor();
            position.add(direction.scl(speed * delta));

            if (position.dst(target) < 3f) {
                currentWaypoint++;
                if (currentWaypoint >= path.size() && !reachedEnd && !isDead) {
                    reachedEnd = true;
                    LevelManager.lives--;
                    System.out.println("Un enemigo llegó al final. Vidas: " + LevelManager.lives);
                }
            }
        }
    }

    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0 && !isDead) {
            isDead = true;
            LevelManager.enemyKilled();
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean hasReachedEnd() {
        return reachedEnd;
    }

    public void render(SpriteBatch batch, Texture texture) {
        if (!isDead) {
            batch.draw(texture, position.x - 16, position.y - 16, 64, 64);
        }
    }

    public void renderHealthBar(ShapeRenderer shapeRenderer) {
        if (!isDead) {
            float barWidth = 20;
            float barHeight = 3;
            float healthPercent = (float) currentHealth / maxHealth;
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.rect(position.x - barWidth / 2, position.y + 10, barWidth, barHeight);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(position.x - barWidth / 2, position.y + 10, barWidth * healthPercent, barHeight);
        }
    }
}
