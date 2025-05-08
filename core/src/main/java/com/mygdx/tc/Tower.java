package com.mygdx.tc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class Tower {
    Vector2 position;
    Texture texture;
    Texture bulletTexture; // ✅ Solo una instancia, no muchas
    List<Bullet> bullets;

    float shootInterval = 1f;
    float timeSinceLastShot = 0f;
    int damage = 20;
    float range = 150f; // ✅ rango de ataque

    public Tower(Vector2 position, Texture texture) {
        this.position = position;
        this.texture = texture;
        this.bulletTexture = new Texture("cannonBall.png"); // una vez
        this.bullets = new ArrayList<>();
    }

    public void update(float delta, List<Enemy> enemies) {
        timeSinceLastShot += delta;

        if (timeSinceLastShot >= shootInterval) {
            Enemy target = getClosestEnemyInRange(enemies);
            if (target != null) {
                bullets.add(new Bullet(position, target, bulletTexture, damage));
                timeSinceLastShot = 0f;
            }
        }

        bullets.forEach(b -> b.update(delta));
        bullets.removeIf(b -> !b.isActive); // ✅ eliminar balas inactivas
    }

    private Enemy getClosestEnemyInRange(List<Enemy> enemies) {
        Enemy closest = null;
        float minDist = Float.MAX_VALUE;

        for (Enemy enemy : enemies) {
            float dist = position.dst(enemy.position);
            if (dist < range && !enemy.isDead()) {
                if (dist < minDist) {
                    minDist = dist;
                    closest = enemy;
                }
            }
        }

        return closest;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 64, position.y - 64, 128, 128);
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
    }

    public void dispose() {
        texture.dispose();
        bulletTexture.dispose();
    }
}
