package com.mygdx.tc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class Tower {
    Vector2 position;
    Texture texture;
    List<Bullet> bullets;
    float shootInterval = 1f; // Tiempo entre disparos
    float timeSinceLastShot = 0f;
    int damage = 20; // Daño de la torre (esto lo puedes modificar según la torre)

    public Tower(Vector2 position, Texture texture) {
        this.position = position;
        this.texture = texture;
        this.bullets = new ArrayList<>();
    }

    public void update(float delta, List<Enemy> enemies) {
        // Actualizar el tiempo para controlar el intervalo de disparo
        timeSinceLastShot += delta;

        // Si ha pasado suficiente tiempo, dispara
        if (timeSinceLastShot >= shootInterval) {
            shootAt(enemies);
            timeSinceLastShot = 0f;
        }

        // Actualizar las balas
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (bullet.hasReachedTarget()) {
                bullet.target.takeDamage(bullet.damage); // Aplica el daño al enemigo
                bullets.remove(i); // Eliminar la bala cuando llegue a su objetivo
                break;
            }
        }
    }

    public void shootAt(List<Enemy> enemies) {
        // Buscar el primer enemigo en el rango
        if (!enemies.isEmpty()) {
            Enemy target = enemies.get(0);  // Por ahora, disparamos al primer enemigo
            bullets.add(new Bullet(position, target, new Texture("cannonBall.png"), damage));
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 64, position.y - 64, 128, 128); // Torre

        // Renderizar las balas
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
    }
}
