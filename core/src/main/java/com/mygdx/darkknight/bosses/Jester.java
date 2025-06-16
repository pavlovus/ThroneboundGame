package com.mygdx.darkknight.bosses;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.List;

public class Jester extends Enemy {
    private static final int MAX_HEALTH = 40;
    private static final float PHASE_2_THRESHOLD = 0.66f; // 66% HP
    private static final float PHASE_3_THRESHOLD = 0.33f; // 33% HP

    private JesterTurret turret;
    private boolean isTurretActive;

    public Jester(float x, float y, GameMap gameMap, Rectangle roomBounds, List<Bullet> bullets, List<Enemy> currentWaveEnemies, List<Enemy> enemiesToAdd, List<Enemy> globalEnemies) {
        super(Assets.jesterTexture, x, y, 50, 60, 0f, MAX_HEALTH, 0, bullets, new JesterAI(roomBounds), gameMap, false);
        this.isTurretActive = true;
        // Спавнимо турель у тій же позиції (центрована на босі)
        this.turret = new JesterTurret(x - 67, y - 72, gameMap, roomBounds, bullets, this); // Центруємо турель відносно боса
        enemiesToAdd.add(turret);
        currentWaveEnemies.add(turret);
        globalEnemies.add(turret);
    }

    @Override
    public void attack(Hero hero) {
        // Бос не атакує напряму, уся атака через турель
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);

        if (isDead()) {
            // Коли бос вмирає, турель автоматично теж вмре через свій update()
            isTurretActive = false;
            return;
        }

        // Оновлюємо фазу турелі залежно від здоров'я
        float healthRatio = (float) getHealth() / MAX_HEALTH;
        if (healthRatio <= PHASE_3_THRESHOLD) {
            turret.setMode(JesterTurret.TurretMode.PHASE_3);
        } else if (healthRatio <= PHASE_2_THRESHOLD) {
            turret.setMode(JesterTurret.TurretMode.PHASE_2);
        } else {
            turret.setMode(JesterTurret.TurretMode.PHASE_1);
        }
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // Коли бос отримує урон, перевіряємо чи він помер
        if (isDead()) {
            isTurretActive = false;
            // Турель сама помре в своєму update()
        }
    }

    public boolean isTurretActive() {
        return isTurretActive;
    }

    public JesterTurret getTurret() {
        return turret;
    }
}
