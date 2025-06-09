package com.mygdx.darkknight.lwjgl3;

import com.badlogic.gdx.Game;
import com.mygdx.darkknight.menus.StartMenu;

public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new StartMenu());
    }
}
