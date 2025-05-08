package com.mygdx.tc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    Vector2 position;
    Enemy target;
    float speed = 200f;
    Texture texture;
    int damage;
    public boolean isActive = true; // ✅ para saber si debe eliminarse

    public Bullet(Vector2 startPos, Enemy target, Texture texture, int damage) {
        this.position = new Vector2(startPos);
        this.target = target;
        this.texture = texture;
        this.damage = damage;
    }

    public void update(float delta) {
        if (target == null || target.isDead()) {
            isActive = false; // 🔴 sin objetivo válido
            return;
        }

        Vector2 direction = new Vector2(target.position).sub(position).nor();
        position.add(direction.scl(speed * delta));

        if (hasReachedTarget()) {
            target.takeDamage(damage); // 🩸 aplicar daño
            isActive = false; // ✅ marcar como usada
        }
    }

    public boolean hasReachedTarget() {
        return position.dst(target.position) < 5f;
    }

    public void render(SpriteBatch batch) {
        if (isActive) {
            batch.draw(texture, position.x - 5, position.y - 5, 10, 10);
        }
    }
}
