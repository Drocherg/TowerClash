package com.mygdx.tc;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    public static LevelManager levelManager;
    public int currentLevel = 1;
    public int currentWave = 0;
    public Path currentPath;
    public List<Enemy> currentEnemies;
    public List<Tower> towersThisLevel;
    public int lives = 20; // o el número que prefieras

    public static int money = 100;           // 💰 dinero inicial
    public int towerCost = 30;        // 💸 costo por torre

    public void startLevel() {
        currentWave = 0;
        towersThisLevel = new ArrayList<>();
        currentPath = new Path();
        currentPath.generateRandomPath();
    }

    public void startNextWave() {
        currentWave++;
        currentEnemies = new ArrayList<>();
        for (int i = 0; i < 5 + currentWave * 2; i++) {
            currentEnemies.add(new Enemy(currentPath));
        }
    }

    public void enemyEscaped() {
        lives--;
        System.out.println("¡Un enemigo ha llegado al final! Vidas restantes: " + lives);
    }

    public boolean isWaveFinished() {
        return currentEnemies.stream()
            .noneMatch(e -> !e.isDead() && e.currentWaypoint < currentPath.waypoints.size());
    }


    public static void enemyKilled() {
        money += 10; // Ganas 10 por matar un enemigo
        System.out.println("Enemigo eliminado. Ganas 10 monedas. Dinero total: " + money);
    }

    // También podemos agregar un método para mostrar el dinero en pantalla (para el UI)
    public static int getMoney() {
        return money;
    }

    // Método que elimina a los enemigos muertos
    public void removeDeadEnemies() {
        currentEnemies.removeIf(Enemy::isDead);  // Elimina todos los enemigos que estén muertos
    }
}
