package com.mygdx.tc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class LevelManager {
    public static LevelManager levelManager;

    public int currentLevel = 1;
    public int currentWave = 0;

    public Path currentPath;
    public List<Enemy> currentEnemies;
    public List<Tower> towersThisLevel;

    public static int lives = 20;
    public static int money = 100;

    // Para gestionar la aparición gradual de enemigos
    private float enemySpawnTimer = 0;
    private float enemySpawnInterval = 1.0f; // 1 segundo entre cada enemigo
    public int enemiesToSpawn = 0; // Cambiado a público para acceder desde GameScreen

    public void startLevel() {
        currentWave = 0;
        towersThisLevel = new ArrayList<>();
        currentPath = new Path();
        currentPath.generateRandomPath();
        currentEnemies = new CopyOnWriteArrayList<>(); // Thread-safe list
        Enemy.resetIDCounter(); // Reiniciar contador de IDs
    }

    public void startNextWave() {
        currentWave++;

        // Calcular enemigos para esta oleada
        // Primera oleada: 3 enemigos
        // Oleadas pares: base + 3 enemigos adicionales
        // Oleadas impares: base + 2 por oleada
        if (currentWave == 1) {
            enemiesToSpawn = 3; // Primera oleada: solo 3 enemigos
        } else if (currentWave % 2 == 0) {
            // Oleada par: base + 3 adicionales
            enemiesToSpawn = 3 + (currentWave * 2) + 3;
        } else {
            // Oleada impar: base + 2 por oleada
            enemiesToSpawn = 3 + (currentWave * 2);
        }

        enemySpawnTimer = 0; // Comenzar a generar enemigos inmediatamente
        System.out.println("Iniciando oleada " + currentWave + " con " + enemiesToSpawn + " enemigos");
    }

    // Método para actualizar la generación de enemigos
    public void update(float delta) {
        // Si hay enemigos por generar, actualizar el temporizador
        if (enemiesToSpawn > 0) {
            enemySpawnTimer += delta;

            // Si es tiempo de generar un nuevo enemigo
            if (enemySpawnTimer >= enemySpawnInterval) {
                enemySpawnTimer = 0;
                spawnEnemy();
                enemiesToSpawn--;
            }
        }
    }

    // Método para generar un solo enemigo
    private void spawnEnemy() {
        Enemy enemy = new Enemy(currentPath);
        currentEnemies.add(enemy);
        System.out.println("Generado nuevo enemigo ID " + enemy.getID() + ". Quedan " + enemiesToSpawn + " por generar.");
    }

    public boolean isWaveFinished() {
        // La oleada termina cuando no hay más enemigos por generar y todos los enemigos actuales
        // están muertos o han llegado al final
        return enemiesToSpawn <= 0 && currentEnemies.stream()
            .noneMatch(e -> !e.isDead() && !e.hasReachedEnd());
    }

    public static void enemyKilled() {
        Random rand = new Random();
        int recompensa = rand.nextInt(16); // genera un número entre 0 y 10 (inclusive)
        money += recompensa;
        System.out.println("Enemigo eliminado. Ganas " + recompensa + " monedas. Dinero total: " + money);
    }

    public static int getMoney() {
        return money;
    }

    // Método para eliminar enemigos muertos de la lista
    public void removeDeadEnemies() {
        // Crear una lista temporal para almacenar los enemigos que deben permanecer
        List<Enemy> enemiesToKeep = new ArrayList<>();

        // Recorrer la lista actual y guardar solo los enemigos vivos
        for (Enemy enemy : currentEnemies) {
            if (!enemy.isDead() && !enemy.hasReachedEnd()) {
                enemiesToKeep.add(enemy);
            } else {
                System.out.println("Removiendo enemigo ID " + enemy.getID() +
                    " de la lista (" + (enemy.isDead() ? "muerto" : "llegó al final") + ")");
            }
        }

        // Limpiar la lista actual y añadir solo los enemigos que deben permanecer
        currentEnemies.clear();
        currentEnemies.addAll(enemiesToKeep);
    }
}
