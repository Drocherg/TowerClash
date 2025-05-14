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

    public static int lives = 20;
    public static int money = 100;

    public int towerCost = 30;

    public void startLevel() {
        currentWave = 0;
        towersThisLevel = new ArrayList<>();
        currentPath = new Path();
        currentPath.generateRandomPath();
    }

    public void startNextWave() {
        currentWave++;
        currentEnemies = new ArrayList<>();
        int numEnemies = 5 + currentWave * 2;
        for (int i = 0; i < numEnemies; i++) {
            currentEnemies.add(new Enemy(currentPath));
        }
    }

    public boolean isWaveFinished() {
        return currentEnemies.stream()
            .noneMatch(e -> !e.isDead() && !e.hasReachedEnd());
    }

    public static void enemyKilled() {
        money += 10;
        System.out.println("Enemigo eliminado. Ganas 10 monedas. Dinero total: " + money);
    }

    public static int getMoney() {
        return money;
    }

    public void removeDeadEnemies() {
        currentEnemies.removeIf(e -> e.isDead() || e.hasReachedEnd());
    }
}
