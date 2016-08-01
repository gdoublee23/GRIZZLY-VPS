package MusaBDrags;

import org.tbot.methods.Camera;
import org.tbot.methods.GroundItems;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Time;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.util.Filter;
import org.tbot.wrappers.GroundItem;
import org.tbot.wrappers.Item;
import org.tbot.wrappers.Tile;

public class Loot {

	static String[] LOOT_NAMES = { "Loop half of key", "Tooth half of key", "Clue scroll (hard)", "Rune dagger",
			"Ensouled dragon head", "Nature rune", "Dragon bones", "Blue dragonhide" };

	public static void run() {
		GroundItem loot = getLoot();
		if (loot != null) {
			if (canLoot(loot)) {
				if (Inventory.isFull()) {
					freeUpSpace();
				} else {
					if (loot.isOnScreen()) {
						loot.interact("Take " + loot.getName());
					} else {
						Camera.turnTo(loot);
						if(!loot.isOnScreen()){
							Walking.walkTileMM(loot.getLocation());
						}
					}
				}
			}
		}
		Time.sleep(Random.nextInt(300, 1400));
	}

	public static boolean canLoot(GroundItem loot) {
		if (loot.distance() < 15 && loot.getLocation().getY() >= 9793) {
			if (loot.getName().equals("Blue dragonhide")) {
				return loot.isOnScreen();
			} else {
				return loot.getLocation().isOnMiniMap();
			}
		}
		return false;
	}

	public static boolean isReady() {
		GroundItem i = getLoot();
		return i != null && i.distance() < 12;
	}

	public static void freeUpSpace() {
		if (Inventory.isFull()) {
			String[] DROP = { "Vial", "Energy potion" };

			boolean eating = false;

			for (Item i : Inventory.getItems()) {
				for (String s : DROP) {
					if (i.getName().contains(s)) {
						i.interact("Drop " + i.getName());
					}
				}
			}

			if (Inventory.contains("Trout") && Inventory.isFull()) {
				eating = true;
				while (eating == true) {
					Item i = Inventory.getItemClosestToMouse("Trout");
					if (Eat.HP == 100 && Random.nextInt(0, 4) > 2) {
						i.interact("Drop");
					} else {
						i.interact("Eat");
					}

					if (Inventory.contains("Trout")) {
						if (Inventory.getCount() < Random.nextInt(25, 27)) {
							eating = false;
						}
					} else if (!Inventory.isFull()) {
						eating = false;
					}

				}
			}
		}

		Time.sleep(Random.nextInt(300, 1200));

	}

	private static GroundItem getLoot() {

		GroundItem loot = GroundItems.getNearest(new Filter<GroundItem>() {
			public boolean accept(GroundItem i) {
				for (String s : LOOT_NAMES) {
					if (i.getName().equals(s) && i.getLocation().getY() > 9793 && i.distance() < 12) {
						if(i.getName().equals("Blue dragonhide")){
							return Inventory.getCount("Blue dragonhide") < Inventory.getCount("Dragon bones");
						}
						return true;
					}
				}
				return false;
			}
		});

		return loot;
	}

}
