package com.mygdx.tc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private static int nextId = 0;  // Generador de IDs únicos
    private final int id;  // ID único para cada enemigo
    Vector2 position;
    int currentWaypoint;
    float speed = 60f;
    Path path;
    public float health = 100;
    private boolean isDead = false; // 👈 nuevo campo para evitar duplicar efectos

    public Enemy(Path path) {
        this.id = nextId++;  // Asigna un ID único e incrementa el contador
        this.path = path;
        this.position = new Vector2(path.waypoints.get(0));
        this.currentWaypoint = 1;
    }

    public int getId() {
        return id;
    }

    public void update(float delta) {
        if (isDead) return; // 🚫 no seguir moviendo si está muerto
        if (currentWaypoint >= path.waypoints.size()) return;

        Vector2 target = path.waypoints.get(currentWaypoint);
        Vector2 direction = new Vector2(target).sub(position).nor();
        position.add(direction.scl(speed * delta));

        if (position.dst(target) < 5f) currentWaypoint++;
    }


    public void render(SpriteBatch batch, Texture texture) {
        if (!isDead) {  // Solo renderiza si el enemigo no está muerto
            batch.draw(texture, position.x - 16, position.y - 16, 32, 32);
        }
    }

    public void renderHealthBar(ShapeRenderer shapeRenderer) {
        if (!isDead) {  // Solo renderiza la barra de salud si el enemigo no está muerto
            float barWidth = 32;
            float barHeight = 4;
            float x = position.x - barWidth / 2;
            float y = position.y + 20;

            float healthRatio = Math.max(health / 100f, 0);

            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.rect(x, y, barWidth, barHeight);

            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(x, y, barWidth * healthRatio, barHeight);
        }
    }

    public void takeDamage(int amount) {
        if (isDead) return;  // Si ya está muerto, no hace nada.
        health -= amount;
        if (health <= 0) {
            isDead = true;  // Marca como muerto
            GameScreen.levelManager.enemyKilled();  // Llamada para sumar dinero
            System.out.println("Enemigo " + id + " ha muerto.");
        }
    }

    public boolean isDead() {
        return isDead;
    }
}
