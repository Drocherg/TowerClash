package com.mygdx.tc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tower {
    Vector2 position;
    float fireCooldown = 1f; // Dispara cada 1 segundo
    float fireTimer = 0;
    int type;
    int damage = 10;
    float range = 100f;

    Texture texture;
    Texture bulletTexture;

    List<Bullet> bullets = new ArrayList<>();

    // Costos específicos para cada tipo de torre
    public static final int COST_CANNON = 60;
    public static final int COST_TESLA = 40;
    public static final int COST_MAGE = 100;

    // Método estático para obtener el costo según el tipo
    public static int getCostForType(int towerType) {
        switch (towerType) {
            case 1: return COST_CANNON;
            case 2: return COST_TESLA;
            case 3: return COST_MAGE;
            default: return 50; // Valor por defecto
        }
    }

    // Método estático para obtener el nombre según el tipo
    public static String getNameForType(int towerType) {
        switch (towerType) {
            case 1: return "Cañón";
            case 2: return "Tesla";
            case 3: return "Mago";
            default: return "Torre";
        }
    }

    public Tower(Vector2 position, int type) {
        this.position = position;
        this.type = type;

        switch (type) {
            case 1:
                texture = new Texture("cannon.png");
                bulletTexture = new Texture("cannonBall.png");
                damage = 25;
                fireCooldown = 1f;
                range = 200f;
                fireTimer = 1;
                break;
            case 2:
                texture = new Texture("tesla.png");
                bulletTexture = new Texture("teslaBullet.png");
                damage = 10;
                fireCooldown = 0.5f;
                range = 100f;
                fireTimer = 1;
                break;
            case 3:
                texture = new Texture("mage.png");
                bulletTexture = new Texture("magicBullet.png");
                damage = 50;
                fireCooldown = 2.5f;
                range = 250f;
                fireTimer = 1;
                break;
        }
    }

    public void update(float delta, List<Enemy> enemies) {
        fireTimer -= delta;

        // Actualizar balas
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update(delta);
            if (!bullet.isActive) {
                bulletIterator.remove();
            }
        }

        // Buscar enemigo en rango
        if (fireTimer <= 0) {
            Enemy closest = null;
            float closestDistance = Float.MAX_VALUE;

            for (Enemy enemy : enemies) {
                if (enemy.isDead()) continue;
                float dist = enemy.position.dst(position);
                if (dist < range && dist < closestDistance) {
                    closest = enemy;
                    closestDistance = dist;
                }
            }

            if (closest != null) {
                shoot(closest);
                fireTimer = fireCooldown;
            }
        }
    }

    private void shoot(Enemy target) {
        Bullet bullet = new Bullet(position, target, bulletTexture, damage);
        bullets.add(bullet);
    }

    public void render(SpriteBatch batch) {
        float drawSize = 128;
        if (type == 3) drawSize = 96;

        batch.draw(texture, position.x - drawSize / 2, position.y - drawSize / 2, drawSize, drawSize);

        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
    }
}
