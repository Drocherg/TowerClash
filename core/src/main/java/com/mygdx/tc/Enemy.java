package com.mygdx.tc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    // ID único para cada enemigo
    private static int nextID = 1;
    public final int id;

    public Vector2 position;
    private Path path;
    private int currentWaypoint = 0;
    private float speed = 50f;

    private int maxHealth = 100;
    private int health = 100;
    private boolean dead = false;
    private boolean reachedEnd = false;

    // Para animación de muerte
    private boolean dying = false;
    private float deathTimer = 0.5f;
    private float scale = 1.0f;

    public Enemy(Path path) {
        this.id = nextID++;
        this.path = path;
        if (path.waypoints.size() > 0) {
            position = new Vector2(path.waypoints.get(0));
        } else {
            position = new Vector2(0, 0);
        }
        System.out.println("Creado enemigo con ID: " + id);
    }

    public void update(float delta) {
        // Si ya está completamente muerto o llegó al final, no hacer nada
        if (dead || reachedEnd) {
            return;
        }

        // Si está en proceso de morir, solo actualizar la animación de muerte
        if (dying) {
            // Animación de muerte: reducir escala y transparencia
            deathTimer -= delta;
            scale = Math.max(0, deathTimer / 0.5f);

            System.out.println("Enemigo ID " + id + " muriendo: " + deathTimer);

            if (deathTimer <= 0) {
                // Ahora sí está completamente muerto
                dead = true;
                System.out.println("Enemigo ID " + id + " muerto, otorgando monedas");
                LevelManager.enemyKilled(); // Dar 10 monedas al jugador
            }
            return; // No seguir con el movimiento
        }

        // Movimiento normal si no está muriendo ni muerto
        if (currentWaypoint < path.waypoints.size() - 1) {
            Vector2 target = path.waypoints.get(currentWaypoint + 1);
            Vector2 direction = new Vector2(target).sub(position).nor();
            position.add(direction.scl(speed * delta));

            if (position.dst(target) < 5f) {
                currentWaypoint++;
            }
        } else {
            reachedEnd = true;
            LevelManager.lives--;
            System.out.println("Enemigo ID " + id + " llegó al final del camino");
        }
    }

    public void render(SpriteBatch batch, Texture texture) {
        // No renderizar si está completamente muerto
        if (dead) {
            return;
        }

        float size = 32 * scale;

        // Aplicar transparencia si está muriendo
        if (dying) {
            batch.setColor(1, 1, 1, scale);
        }

        batch.draw(texture,
            position.x - size/2, position.y - size/2,
            size, size);

        // Restaurar color normal
        if (dying) {
            batch.setColor(1, 1, 1, 1);
        }
    }

    public void renderHealthBar(ShapeRenderer shapeRenderer) {
        // No mostrar barra de vida si está muriendo o muerto
        if (dead || dying) {
            return;
        }

        float healthPercentage = (float) health / maxHealth;
        float barWidth = 30;

        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(position.x - barWidth/2, position.y + 20, barWidth, 5);

        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(position.x - barWidth/2, position.y + 20, barWidth * healthPercentage, 5);
    }

    public void takeDamage(int damage) {
        // No aplicar daño si ya está muriendo o muerto
        if (dead || dying) {
            return;
        }

        health -= damage;
        System.out.println("Enemigo ID " + id + " recibió daño. Salud: " + health);

        if (health <= 0) {
            // Iniciar animación de muerte
            System.out.println("Iniciando animación de muerte para enemigo ID " + id);
            dying = true;
            health = 0;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public boolean hasReachedEnd() {
        return reachedEnd;
    }

    public boolean isDying() {
        return dying;
    }

    public int getID() {
        return id;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    // Metodo para reiniciar el contador de IDs
    public static void resetIDCounter() {
        nextID = 1;
    }
}
