package MusaBDrags;

import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.Bank;
import org.tbot.methods.GameObjects;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Time;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.methods.web.banks.WebBanks;
import org.tbot.wrappers.GameObject;
import org.tbot.wrappers.Item;
import org.tbot.wrappers.Tile;

import MusaOrbs.Locations;

public class Banking {

	static Tile[] TELE_TO_BANK = { new Tile(2966, 3379), new Tile(2959, 3381), new Tile(2950, 3379),
			new Tile(2946, 3369) };
	static Tile BANK_TILE = TELE_TO_BANK[TELE_TO_BANK.length - 1];

	public static class BankItem {

		private static BankItem FALADOR_TELEPORT = new BankItem("Falador teleport", 1);
		private static BankItem DUSTY_KEY = new BankItem("Dusty key", 1);
		private static BankItem MIND_RUNE = new BankItem("Mind rune", 500);
		private static BankItem AIR_RUNE = new BankItem("Air rune", 1000);
		private static BankItem FOOD = new BankItem("Trout", 16);
		private static BankItem ENERGY_POT = new BankItem("Energy potion(4)", 2);
		private static BankItem ANTIPOISON = new BankItem("Antipoison(1)", antipoisonAmount());

		private static int antipoisonAmount(){
			if(Players.getLocal().getCombatLevel() >= 41){
				return 0;
			}
			return 1;
		}
		
		public static BankItem[] LIST = { FALADOR_TELEPORT, DUSTY_KEY, MIND_RUNE, AIR_RUNE, FOOD, ENERGY_POT,
				ANTIPOISON };

		private String name;
		private int amount;

		public BankItem(String n, int a) {
			name = n;
			amount = a;
		}

		protected boolean isReady() {
			return Inventory.getCount(name) == amount;
		}

		protected String getName() {
			return name;
		}

		protected int getAmount() {
			return amount;
		}
	}

	public static void run() {

		LogHandler.log("BNAKING");

		String names = "";
		if (Eat.HP < 100 && Inventory.contains("Trout")
				&& Locations.current() == Locations.BANK){
			if(Bank.isOpen()){
				Bank.close();
			}
			Item i = Inventory.getItemClosestToMouse("Trout");
			i.click();
			Time.sleep(300, 1200);

		} else if (Bank.isOpen()) {
			for (Item i : Inventory.getItems()) {
				boolean shouldKeep = false;
				if (!names.contains("[" + i.getName() + "]")) {
					names = names + "[" + i.getName() + "]";
				} else {
					for (BankItem bi : BankItem.LIST) {
						if (bi.getName().equals(i.getName())) {
							shouldKeep = true;
						}
					}
					if (!shouldKeep) {
						if (Inventory.getCount(i.getName()) > 1) {
							Bank.depositAll();
						} else {
							Bank.depositAll(i.getName());
						}
					}
				}

			}

			for (BankItem bi : BankItem.LIST) {
				if (Inventory.getCount(bi.getName()) != bi.getAmount()) {
					if (Inventory.getCount(bi.getName()) > 0) {
						Bank.depositAll(bi.getName());
					} else {
						Bank.withdraw(bi.getName(), bi.getAmount());
					}
					Time.sleep(Random.nextInt(300, 1500));
				}
			}

		} else {
			if (atBank()) {
				if (BANK_TILE.isOnScreen()) {
					Bank.openBank(WebBanks.FALADOR_WEST_BANK);
				} else if (BANK_TILE.isOnMiniMap()) {
					int X = BANK_TILE.getX() + Random.nextInt(-2, 2);
					int Y = BANK_TILE.getY() + Random.nextInt(-2, 2);
					;
					Walking.walkTileMM(new Tile(X, Y));
				} else {
					Traverse.walkPath(TELE_TO_BANK);
				}
				Time.sleep(Random.nextInt(300, 1200));
			} else {
				Item i = Inventory.getFirst("Falador teleport");
				if (i != null) {
					i.click();
					Time.sleep(Random.nextInt(300, 900));
				}
			}
		}

	}

	public static boolean isReady() {
		if(Traverse.atDragons()){
			if(!Inventory.contains("Trout") && Eat.HP < 50){
				return true;
			} else if (Inventory.getCount("Air rune") < 2 || !Inventory.containsAll("Mind rune")){
				return true;
			}
		}
		return needsBank();
	}

	public static boolean needsBank() {
		if (isUnderground()) {
			if (Players.getLocal().getHealthPercent() < 30 && !Inventory.contains("Trout")) {
				return true;
			}
			String[] ITEMS = { "Trout", "Vial" };
			return Inventory.isFull() && !Inventory.containsOneOf(ITEMS);
		} else {
			if (BANK_TILE.isOnScreen() && Eat.HP < 100) {
				return true;
			} else if(Players.getLocal().getLocation().getX() > 2934){
				for (BankItem i : BankItem.LIST) {
					if (!i.isReady()) {
						return true;
					}
				}
			}
			return false;
		}
	}

	private static boolean isUnderground() {
		return Players.getLocal().getLocation().getY() > 9000;
	}

	private static boolean atBank() {
		return !isUnderground() && Players.getLocal().getLocation().getX() > 2935;
	}

}
