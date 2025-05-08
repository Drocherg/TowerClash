package com.mygdx.tc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.ArrayList;

public class Path {
    public List<Vector2> waypoints;

    public Path() {
        waypoints = new ArrayList<>();
    }

    public void generateRandomPath() {
        waypoints.clear();

        // Empieza a la izquierda y termina a la derecha, centrado verticalmente
        waypoints.add(new Vector2(0, 180));

        waypoints.add(new Vector2(120, 220));
        waypoints.add(new Vector2(240, 140));
        waypoints.add(new Vector2(360, 220));
        waypoints.add(new Vector2(480, 180));
        waypoints.add(new Vector2(640, 180)); // Final en borde derecho
    }

}
