package MusaBDrags;

import java.awt.Point;

import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.Camera;
import org.tbot.methods.Mouse;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Time;
import org.tbot.methods.Widgets;
import org.tbot.methods.tabs.Inventory;
import org.tbot.wrappers.Character;
import org.tbot.wrappers.Item;
import org.tbot.wrappers.Player;
import org.tbot.wrappers.WidgetChild;

public class Antiban {

	public static void run() {

		if (randomize(10, 6)) {
			switch (Random.nextInt(0, 9)) {
			case 1:
				camera();
			case 2:
				mouse();
			case 3:
				if (!Players.getLocal().isMoving() && Random.nextInt(0, 1000) > 800) {
					tab();
				}
			case 4:
				player();
			case 5:
				xp();
			case 6:
				npc();
			}
		}
	}

	private static void npc() {
		if (Random.nextInt(0, 1000) > 800) {
			if (Players.getLocal().getInteractingCharacterIndex() > 0) {
				Character CHAR = Players.getLocal().getInteractingEntity();
				if (CHAR != null) {
					try {
						if (CHAR.interact("Attack"))
							;
					} catch (Exception e) {
					}
				}
			}
		}
	}

	private static void inv() {
		for (Item i : Inventory.getItems()) {
			if (i != null && Random.nextInt(0, 100) > 77) {

			}
		}
	}

	private static void xp() {
		if (Random.nextInt(0, 1000) > 800) {
			boolean checked = false;
			while (checked == false) {
				int W = getWidgetInt();
				WidgetChild w = Widgets.getWidget(320, W);
				WidgetChild w1 = w.getChild(Random.nextInt(0, 4));
				if (w1 != null && w1.isVisible()) {
					Mouse.move(new Point(w1.getX() + Random.nextInt(0, 30), w1.getY() + Random.nextInt(0, 30)));
					Time.sleep(Random.nextInt(500, 3000));
					Mouse.moveRandomly();
					checked = true;
				} else {
					WidgetChild tab = Widgets.getWidget(548, 46);
					tab.click();
					Time.sleep(Random.nextInt(30, 700));
				}
			}
		}
	}

	private static int getWidgetInt() {
		if (Random.nextInt(0, 10) > 8) {
			return 9;
		}
		return 6;
	}

	private static void glory() {

	}

	private static void player() {
		for (Player p : Players.getLoaded()) {
			if (p.isOnScreen()) {
			}
		}
	}

	private static void tab() {
		if (randomize(1000, 900)) {
			int[] a = { Random.nextInt(26, 58), Random.nextInt(35, 36) };
			WidgetChild w = Widgets.getWidget(548, a[Random.nextInt(0, 1)]);
			w.click();
			Time.sleep(Random.nextInt(1000, 5000));
		}
	}

	private static void camera() {
		if (randomize(1000, 300)) {
			if (Random.nextInt(0, 100) > 50 && Players.getLocal().getInteractingEntity() != null) {
				Camera.turnTo(Players.getLocal().getInteractingEntity());
			}
			if (Random.nextInt(0, 1000) > 750) {
				Camera.setPitch(Random.nextInt(32, 150));
			}
			if (Random.nextInt(0, 1000) > 500) {
				Camera.setAngle(Random.nextInt(0, 360));
			}
		}
	}

	private static void mouse() {
		if (randomize(1000, 230)) {
			Mouse.move(Random.nextInt(-500, 1500), Random.nextInt(-500, 1500));
			Time.sleep(1000, 1500);
		}
	}

	private static boolean randomize(int b, int c) {
		return Random.nextInt(0, b) > c;
	}
}