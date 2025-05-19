package com.mygdx.tc;

import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.ArrayList;

public class Path {
    public List<Vector2> waypoints;

    public Path() {
        waypoints = new ArrayList<>();
        generateRandomPath(); // Genera un camino cuando se crea el objeto Path
    }

    public void generateRandomPath() {
        waypoints.clear();

        // Empieza a la izquierda y termina a la derecha, centrado verticalmente
        waypoints.add(new Vector2(0, 180)); // Punto inicial

        // Se hace un zigzag pronunciado
        waypoints.add(new Vector2(100, 250)); // Primer giro
        waypoints.add(new Vector2(150, 150)); // Segundo giro
        waypoints.add(new Vector2(200, 240)); // Tercer giro
        waypoints.add(new Vector2(250, 130)); // Cuarto giro más fuerte
        waypoints.add(new Vector2(300, 220)); // Continuamos el zigzag
        waypoints.add(new Vector2(350, 140)); // Otro giro
        waypoints.add(new Vector2(400, 230)); // Otro giro más
        waypoints.add(new Vector2(450, 170)); // Más zigzagueante
        waypoints.add(new Vector2(500, 120)); // Curva más baja
        waypoints.add(new Vector2(550, 200)); // Curvatura que va hacia arriba
        waypoints.add(new Vector2(600, 180)); // Movimiento hacia abajo

        // Terminamos en el borde derecho
        waypoints.add(new Vector2(640, 180)); // Final en el borde derecho

        // Log para verificar los puntos generados
        for (Vector2 waypoint : waypoints) {
            System.out.println("Waypoint: " + waypoint);
        }
    }


}
