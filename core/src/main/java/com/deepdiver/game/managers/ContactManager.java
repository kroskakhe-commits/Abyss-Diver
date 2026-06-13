package com.deepdiver.game.managers;

import com.badlogic.gdx.utils.Array;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.objects.BubbleObject;
import com.deepdiver.game.objects.CrystalObject;
import com.deepdiver.game.objects.DiverObject;
import com.deepdiver.game.objects.JellyfishObject;
import com.deepdiver.game.objects.PowerUpObject;
import com.deepdiver.game.objects.SharkObject;
import com.deepdiver.game.objects.OctopusObject;
import com.deepdiver.game.objects.RayObject;
import com.deepdiver.game.screens.GameScreen;

/**
 * Обработчик столкновений между дайвером и игровыми объектами.
 * Содержит статические методы для проверки пересечений и применения эффектов.
 */
public class ContactManager {

    /**
     * Проверяет все столкновения дайвера с объектами на экране.
     * @param diver объект дайвера
     * @param screen ссылка на игровой экран для вызова методов изменения состояния
     */
    public static void checkCollisions(DiverObject diver, GameScreen screen) {
        // Проверка столкновений с кристаллами
        Array<CrystalObject> crystals = screen.getCrystals();
        for (int i = crystals.size - 1; i >= 0; i--) {
            CrystalObject c = crystals.get(i);
            if (diver.bounds.overlaps(c.bounds)) {
                if (c.isGold) {
                    screen.addScore(GameSettings.SCORE_GOLD_CRYSTAL);
                    screen.addCrystalGold();
                } else {
                    screen.addScore(GameSettings.SCORE_CRYSTAL);
                    screen.addCrystalNormal();
                }
                screen.getGame().audioManager.playCollect();
                crystals.removeIndex(i);
            }
        }

        // Проверка столкновений с пузырьками кислорода
        Array<BubbleObject> bubbles = screen.getBubbles();
        for (int i = bubbles.size - 1; i >= 0; i--) {
            BubbleObject b = bubbles.get(i);
            if (diver.bounds.overlaps(b.bounds)) {
                screen.replenishOxygen(GameSettings.BUBBLE_REPLENISH);
                screen.addBubble();
                screen.getGame().audioManager.playCollect();
                bubbles.removeIndex(i);
            }
        }

        // Проверка столкновений с бонусами
        Array<PowerUpObject> powerUps = screen.getPowerUps();
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUpObject p = powerUps.get(i);
            if (diver.bounds.overlaps(p.bounds)) {
                screen.activatePowerUp(p.type);
                screen.getGame().audioManager.playCollect();
                powerUps.removeIndex(i);
            }
        }

        // Проверка столкновений с медузами
        Array<JellyfishObject> jellies = screen.getJellyfish();
        for (int i = jellies.size - 1; i >= 0; i--) {
            JellyfishObject j = jellies.get(i);
            if (diver.bounds.overlaps(j.bounds)) {
                if (!screen.hasShield()) {
                    screen.damageOxygen(GameSettings.JELLY_DAMAGE);
                    screen.getGame().audioManager.playHit();
                    screen.getDiver().hit();
                }
                jellies.removeIndex(i);
            }
        }

        // Проверка столкновений с акулами
        Array<SharkObject> sharks = screen.getSharks();
        for (int i = sharks.size - 1; i >= 0; i--) {
            SharkObject s = sharks.get(i);
            if (diver.bounds.overlaps(s.bounds)) {
                if (!screen.hasShield()) {
                    screen.damageOxygen(GameSettings.SHARK_DAMAGE);
                    screen.getGame().audioManager.playHit();
                    screen.getDiver().hit();
                }
                sharks.removeIndex(i);
            }
        }

        // Проверка столкновений с осьминогами
        Array<OctopusObject> octopuses = screen.getOctopuses();
        for (int i = octopuses.size - 1; i >= 0; i--) {
            OctopusObject o = octopuses.get(i);
            if (diver.bounds.overlaps(o.bounds)) {
                if (!screen.hasShield()) {
                    screen.damageOxygen(GameSettings.OCTOPUS_DAMAGE);
                    screen.getGame().audioManager.playHit();
                    screen.getDiver().hit();
                }
                octopuses.removeIndex(i);
            }
        }

        // Проверка столкновений со скатами
        Array<RayObject> rays = screen.getRays();
        for (int i = rays.size - 1; i >= 0; i--) {
            RayObject r = rays.get(i);
            if (diver.bounds.overlaps(r.bounds)) {
                if (!screen.hasShield()) {
                    screen.damageOxygen(GameSettings.RAY_DAMAGE);
                    screen.getGame().audioManager.playHit();
                    screen.getDiver().hit();
                }
                rays.removeIndex(i);
            }
        }
    }
}
