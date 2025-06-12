package com.mygdx.darkknight;

import com.badlogic.gdx.utils.Array;
import com.mygdx.darkknight.weapons.Weapon;

import java.util.List;

public class Chest {
    public float x, y;
    public List<Weapon> weapons;
    public boolean opened;

    public Chest(float x, float y, List<Weapon> contents) {
        this.x = x;
        this.y = y;
        this.weapons = contents;
    }


}
