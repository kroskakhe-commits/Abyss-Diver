package com.deepdiver.game.managers;

import com.badlogic.gdx.utils.Array;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.objects.BubbleObject;
import com.deepdiver.game.objects.CrystalObject;
import com.deepdiver.game.objects.DiverObject;
import com.deepdiver.game.objects.JellyfishObject;
import com.deepdiver.game.screens.GameScreen;

/**
 * Проверяет столкновения дайвера со всем, что летает по экрану.
 *
 * ВАЖНО: идём по массивам С КОНЦА, потому что внутри цикла удаляем элементы.
 * Если идти с начала — пропустим следующий объект после удаления.
 */
public class ContactManager {

    public static void checkCollisions(DiverObject diver, GameScreen screen) {
        // ========== КРИСТАЛЛЫ ==========
        Array<CrystalObject> crystals = screen.getCrystals();
        for (int i = crystals.size - 1; i >= 0; i--) {
            CrystalObject c = crystals.get(i);

            if (diver.bounds.overlaps(c.bounds)) {
                // Даём очки в зависимости от типа кристалла
                if (c.isGold) {
                    screen.addScore(GameSettings.SCORE_GOLD_CRYSTAL);
                    screen.addCrystalGold();      // Для квеста
                } else {
                    screen.addScore(GameSettings.SCORE_CRYSTAL);
                    screen.addCrystalNormal();    // Для квеста
                }
                screen.getGame().audioManager.playCollect();
                crystals.removeIndex(i);          // Кристалл исчезает
            }
        }

        // ========== ПУЗЫРЬКИ КИСЛОРОДА ==========
        Array<BubbleObject> bubbles = screen.getBubbles();
        for (int i = bubbles.size - 1; i >= 0; i--) {
            BubbleObject b = bubbles.get(i);
            if (diver.bounds.overlaps(b.bounds)) {
                screen.replenishOxygen(GameSettings.BUBBLE_REPLENISH);
                screen.addBubble();               // Для квеста
                screen.getGame().audioManager.playCollect();
                bubbles.removeIndex(i);
            }
        }

        // ========== МЕДУЗЫ (ОПАСНОСТЬ) ==========
        Array<JellyfishObject> jellies = screen.getJellyfish();
        for (int i = jellies.size - 1; i >= 0; i--) {
            JellyfishObject j = jellies.get(i);
            if (diver.bounds.overlaps(j.bounds)) {
                screen.damageOxygen(GameSettings.JELLY_DAMAGE);
                screen.getGame().audioManager.playHit();
                screen.getDiver().hit();          // Визуальный эффект покраснения
                jellies.removeIndex(i);           // Медуза исчезает после укуса
                System.out.println("🔥 Медуза ужалила! Кислород: " + screen.getOxygen());
            }
        }
    }
}
