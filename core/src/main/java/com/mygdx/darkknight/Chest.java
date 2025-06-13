package com.mygdx.darkknight;

import com.badlogic.gdx.utils.Array;
import com.mygdx.darkknight.weapons.Weapon;

import java.util.List;

public class Chest {
    public float x, y;
    public Weapon weapon;
    public boolean opened;

    public Chest(float x, float y, Weapon contents) {
        this.x = x;
        this.y = 600 - y;
        this.weapon = contents;
        this.opened = false;
    }


}
