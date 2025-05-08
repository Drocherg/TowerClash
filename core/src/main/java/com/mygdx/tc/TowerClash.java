package com.mygdx.tc;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
// Main class
public class TowerClash extends Game {
    @Override
    public void create() {
        this.setScreen(new GameScreen());
    }

}

