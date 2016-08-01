package MusaBDrags;

import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Skills;
import org.tbot.methods.Time;
import org.tbot.methods.Skills.Skill;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.util.Filter;
import org.tbot.wrappers.Item;

public class Eat {

	static double HP = 100 * Skills.getCurrentLevel(Skill.HITPOINTS) / Skills.getRealLevel(Skill.HITPOINTS);

	public static void run() {

		if (Walking.getRunEnergy() < Random.nextInt(30, 65)) {
			Item i = Inventory.getFirst(new Filter<Item>() {
				public boolean accept(Item i) {
					return i.getName().contains("Energy potion");
				}
			});

			if (i != null) {
				i.click();
			}

		}

		while (HP < Random.nextInt(60, 85)) {
			Item i = Inventory.getFirst("Trout");
			if (i != null) {
				i.click();
				Time.sleep(300, 1200);
				HP = 100 * Skills.getCurrentLevel(Skill.HITPOINTS) / Skills.getRealLevel(Skill.HITPOINTS);
			}
		}

	}

	public static boolean isReady() {

		HP = 100 * Skills.getCurrentLevel(Skill.HITPOINTS) / Skills.getRealLevel(Skill.HITPOINTS);

		Item pot = Inventory.getFirst(new Filter<Item>() {
			public boolean accept(Item i) {
				return i.getName().contains("Energy potion");
			}
		});

		Item food = Inventory.getFirst("Trout");

		if (Walking.getRunEnergy() < Random.nextInt(30, 65) && pot != null) {
			return true;
		}

		if (Players.getLocal().getInteractingCharacterIndex() > 0) {
			return HP < Random.nextInt(25, 50);
		}
		
		return HP < Random.nextInt(50, 75) && food != null;

	}

}
