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
    public boolean isActive = true;

    public Bullet(Vector2 startPos, Enemy target, Texture texture, int damage) {
        this.position = new Vector2(startPos);
        this.target = target;
        this.texture = texture;
        this.damage = damage;
    }

    public void update(float delta) {
        // Si el objetivo no existe o ya está muerto o muriendo, desactivar la bala
        if (target == null || target.isDead() || target.isDying()) {
            isActive = false;
            return;
        }

        Vector2 direction = new Vector2(target.position).sub(position).nor();
        position.add(direction.scl(speed * delta));

        if (hasReachedTarget()) {
            System.out.println("Bala impactó al enemigo. Daño: " + damage);
            target.takeDamage(damage);
            isActive = false;
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
