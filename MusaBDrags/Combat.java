package MusaBDrags;

import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.Camera;
import org.tbot.methods.GameObjects;
import org.tbot.methods.Mouse;
import org.tbot.methods.Npcs;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Time;
import org.tbot.methods.Widgets;
import org.tbot.methods.combat.magic.Magic;
import org.tbot.methods.combat.magic.Spell;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.util.Filter;
import org.tbot.wrappers.NPC;
import org.tbot.wrappers.Player;
import org.tbot.wrappers.Tile;
import org.tbot.wrappers.WidgetChild;

import MusaOrbs.Locations;

import org.tbot.wrappers.Character;
import org.tbot.wrappers.GameObject;
import org.tbot.wrappers.Item;

public class Combat {

	static Tile WEST_TILE = new Tile(2897, 9800);
	static int LAST_DRAGON_ID = -1;
	static Tile LAST_DRAGON_TILE = null;
	static Tile EAST_TILE = new Tile(2915, 9803);
	static Tile EAST_TILE2 = new Tile(2908, 9803);

	static boolean TAKING_DRAGONS = false;

	static Tile[] RETREAT_PATH = { EAST_TILE, EAST_TILE2, SafeArea.EAST_AREA.getTile(), SafeArea.CENTRE_AREA.getTile(),
			WEST_TILE };
	static boolean found = false;

	public static void run() {

		if (!WEST_TILE.isOnMiniMap() && !SafeArea.CENTRE_AREA.getTile().isOnMiniMap()) {
			Traverse.walkPath(RETREAT_PATH);
		} else {

			try {

				if (shouldHop()) {
					LAST_DRAGON_ID = -1;
					MusaBDrags.IS_HOPPING = true;
				}

				Item i = Inventory.getItemClosestToMouse("Antipoison(1)");
				if (i != null) {
					i.click();
					Time.sleep(400, 1200);
				}

				if (shouldFlee()) {
					flee();
				} else {

					WidgetChild c = Widgets.getWidget(233, 2);
					WidgetChild c1 = Widgets.getWidget(193, 2);
					WidgetChild[] continueWidgets = { c, c1 };

					for (WidgetChild w : continueWidgets) {
						if (w != null && w.isValid() && w.isOnScreen()
								&& w.getText().equals("Click here to continue")) {
							int x = w.getX();
							w.click();
						}
					}

					if (!Inventory.isOpen() && Random.nextInt(0, 10) > 7) {
						Inventory.openTab();
					}

					if (Walking.getRunEnergy() > Random.nextInt(20, 60) && !Walking.isRunEnabled()) {
						Walking.setRun(true);
					}

					NPC dragon = getDragon();

					if (Players.getLocal().getInteractingCharacterIndex() > 0
							|| Players.getLocal().getInteractingEntity() != null) {
						LogHandler.log("3");
						Character interacting = Players.getLocal().getInteractingEntity();
						if (interacting.getName().equals("Baby dragon")) {
							LogHandler.log("4");
							flee();
						} else if (interacting.getName().equals("Blue dragon")) {
							if (isRespawned()) {
								LAST_DRAGON_ID = interacting.getID();
							}
							SafeArea area = SafeArea.getArea(LAST_DRAGON_ID);
							if (!area.containsPlayer()) {
								if (Random.nextInt(0, 10) > 3) {
									Walking.walkTileOnScreen(area.getTile());
								} else {
									Tile t = area.getTile();
									if (t.isOnMiniMap()) {
										Traverse.walkTo(area.getTile());
									} else {
										Tile TILE = randomizeTile(WEST_TILE, 2);
										Traverse.walkTo(TILE);
									}

								}
							} else if (Random.nextInt(0, 100) > 30) {
								if (Camera.getPitch() < Random.nextInt(20, 60)) {
									Camera.setPitch(Random.nextInt(50, 180));
								}
								if (Players.getLocal().getInteractingEntity() != null && Random.nextInt(0, 10) > 6) {
									Camera.turnTo(Players.getLocal().getInteractingEntity());
								}
								if (Random.nextInt(0, 10) > 8 && Inventory.isFull()) {
									Loot.freeUpSpace();
								}
								LogHandler.log("AB");
								Antiban.run();
							}
						}
					} else {

						while (!isRespawned() && Locations.current() == Locations.UNDERGROUND) {
							waitForRespawn(LAST_DRAGON_ID);
						}

						if (isRespawned()) {
							dragon = getDragon();
							LogHandler.log("2");
							attack(dragon);
						} else if (getDragon() == null) {
							Time.sleep(300, 1800);
							SafeArea area = null;
							if (getDragon() == null) {
								Tile T = SafeArea.CENTRE_AREA.getTile();
								while (!Players.getLocal().getLocation().equals(T)
										&& Locations.current() != Locations.BANK) {
									if (T.isOnMiniMap()) {
										Walking.walkTileMM(T);
									} else if (WEST_TILE.isOnMiniMap()) {
										Walking.walkTileMM(WEST_TILE);
									} else {
										Walking.walkTileMM(EAST_TILE);
									}
									Time.sleep(600, 1200);
								}
								if (getDragon() == null && !Loot.isReady()) {
									LAST_DRAGON_ID = -1;
									MusaBDrags.IS_HOPPING = true;
								}
							}
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static boolean shouldHop() {

		GameObject CANNON = GameObjects.getNearest("Dwarf multicannon");
		if (CANNON != null && CANNON.getLocation().getY() > 9793 && CANNON.getLocation().getX() < 2909) {
			return true;
		}

		if (!isRespawned()) {
			return false;
		} else if (getFreeDragon(0) == null) {
			return true;
		}

		Player[] VALID = Players.getLoaded(new Filter<Player>() {
			public boolean accept(Player p) {
				return p.getInteractingCharacterIndex() > 0 && p.getInteractingEntity().getName().equals("Blue dragon");
			}
		});

		LogHandler.log("VALID: " + VALID.length);

		Character INTERACTING = Players.getLocal().getInteractingEntity();
		if (INTERACTING != null) {
			return !INTERACTING.getName().equals("Blue dragon") && VALID.length > 2;
		}

		return VALID.length > 2 || getDragon() == null;
	}

	private static boolean shouldFlee() {
		return getBabyDrag() != null && LAST_DRAGON_ID != 265;
	}

	private static NPC getBabyDrag() {
		for (NPC n : Npcs.getLoaded("Baby dragon")) {
			if (n.isInteractingWithLocalPlayer() && n.distance() <= 5 && n.isOnScreen()) {
				return n;
			}
		}
		return null;
	}

	private static void flee() {
		Tile safeTile = new Tile(2901, 9810);
		if (Players.getLocal().getLocation().getX() < 2901) {
			safeTile = SafeArea.WEST_AREA.getTile();
		}
		boolean done = false;
		while (done == false && Locations.current() != Locations.BANK) {
			if (!Players.getLocal().getLocation().equals(safeTile)) {
				Traverse.walkTo(safeTile);
			} else {
				NPC n = Npcs.getNearest(new Filter<NPC>() {
					public boolean accept(NPC npc) {
						return npc.getName().equals("Baby dragon");
					}
				});
				if (n != null && n.isOnScreen() && n.distance() < 5) {
					if (n.interact("Attack")) {
						if (Players.getLocal().getAnimation() > -1) {
							if (Walking.walkTileOnScreen(Players.getLocal().getLocation())) {
								done = true;
							}
						}
					}
				} else {
					done = true;
				}
			}
			Time.sleep(Random.nextInt(300, 1200));
		}
	}

	private static void waitForRespawn(int id) {
		SafeArea area = SafeArea.getArea(LAST_DRAGON_ID);
		if (area != null) {
			if (!area.containsPlayer()) {
				if (Random.nextInt(0, 10) > 7) {
					Traverse.walkTo(area.getTile());
				} else {
					if (area.getTile().isOnScreen()) {
						Walking.walkTileMM(area.getTile());
					} else if (WEST_TILE.isOnMiniMap()) {
						Walking.walkTileMM(WEST_TILE);
					} else {
						Walking.walkTileMM(EAST_TILE);
					}
				}
			} else {
				while (!isRespawned() && Locations.current() != Locations.BANK) {
					Time.sleep(50);
				}
			}
			Time.sleep(Random.nextInt(300, 1200));
		}
	}

	public static boolean isRespawned() {

		if (Loot.isReady()) {
			return true;
		} else if (LAST_DRAGON_ID < 0) {
			return true;
		} else {
			int[] IDS = { 265, 267, 268 };

			for (int i : IDS) {
				if (i < LAST_DRAGON_ID) {
					NPC DRAGON = getFreeDragon(i);
					if (DRAGON != null && DRAGON.getAnimation() == -1) {
						return DRAGON.getID() > 0 && DRAGON.getID() < LAST_DRAGON_ID;
					}
				}
			}

			NPC dragon = Npcs.getNearest(new Filter<NPC>() {
				public boolean accept(NPC n) {
					if (n.getID() == LAST_DRAGON_ID && n.getLocation().getY() > 9793) {
						return n.getAnimation() == -1;
					}
					return false;
				}
			});

			return dragon != null && dragon.getAnimation2() > 89;
		}
	}

	private static void attack(NPC n) {
		Tile TILE = SafeArea.getArea(n).getTile();
		if (TILE != null && TILE.distance() > 1 && Random.nextInt(0, 10) > 3) {
			if (TILE.isOnScreen()) {
				Walking.walkTileOnScreen(TILE);
			} else if (TILE.isOnMiniMap()) {
				Walking.walkTileMM(TILE);
			} else if (WEST_TILE.isOnMiniMap()) {
				Walking.walkTileMM(WEST_TILE);
			} else {
				Walking.walkTileMM(EAST_TILE);
			}
		} else if (n.isOnScreen()) {
			if (n.getAnimation() == 92 && n.getAnimation2() == 90) {
				if (Random.nextInt(0, 10) > 4) {
					Walking.walkTileOnScreen(n.getLocation());
				}
			} else {
				n.interact("Attack");
				LAST_DRAGON_ID = n.getID();
			}
		} else {
			Camera.turnTo(n);
			if (!n.isOnScreen()) {
				Camera.setPitch(Random.nextInt(20, 60));
			}

			/**
			 * if (n.getID() == 267) { Camera.turnTo(n);
			 * Camera.setPitch(Random.nextInt(0, 60)); } else if
			 * (Random.nextInt(0, 100) > 80) {
			 * Camera.setPitch(Random.nextInt(40, 200));
			 * 
			 * }
			 * 
			 * 
			 * if (!n.isOnScreen()) { if (Random.nextInt(0, 10) >= 3) {
			 * Walking.walkTileOnScreen(n.getLocation()); } else { if
			 * (n.getLocation().isOnMiniMap()) { Tile T =
			 * randomizeTile(n.getLocation(), 2); Walking.walkTileMM(T); } else
			 * { Tile T = randomizeTile(WEST_TILE, 4); Walking.walkTileMM(T); }
			 * } }
			 **/
		}
	}

	private static Tile randomizeTile(Tile TILE, int INDEX) {
		int x = TILE.getX() + Random.nextInt(-INDEX, INDEX);
		int y = TILE.getY() + Random.nextInt(-INDEX, INDEX);
		return new Tile(x, y);
	}

	public static boolean isReady() {
		return !Loot.isReady() && Traverse.atDragons();
	}

	public static NPC getDragon() {

		int[] IDS = { 265, 267, 268 };

		NPC out = null;

		if (!TAKING_DRAGONS) {
			out = Npcs.getNearest(new Filter<NPC>() {
				public boolean accept(NPC n) {
					if (n.getID() == LAST_DRAGON_ID && LAST_DRAGON_ID > 0) {
						if (n.getName().equals("Blue dragon") && n.getLocation().getY() > 9793) {
							if (n.isInteractingWithLocalPlayer() || n.getInteractingCharacterIndex() < 0) {
								LAST_DRAGON_ID = n.getID();
								LogHandler.log("LAST");
								return true;
							}
						}
					}
					return false;
				}
			});

			if (out != null) {
				return out;
			}
		}

		NPC FIRST = getFreeDragon(265);
		NPC SECOND = getFreeDragon(267);
		NPC THIRD = getFreeDragon(268);

		NPC[] LIST = { FIRST, SECOND, THIRD };

		for (NPC n : LIST) {
			if (n != null) {
				if (n.getInteractingCharacterIndex() < 0 || n.isInteractingWithLocalPlayer()) {
					return n;
				}
			}
		}

		return out;
	}

	static NPC getFreeDragon(int id) {

		NPC out = null;
		int[] IDS = { 265, 267, 268 };

		if (id > 0) {
			int[] ID = { id };
			IDS = ID;
		}

		for (int i : IDS) {
			out = Npcs.getNearest(new Filter<NPC>() {
				public boolean accept(NPC n) {
					if (n.getID() == i && n.getName().equals("Blue dragon") && n.getLocation().getY() > 9793) {
						if (n.getInteractingCharacterIndex() < 0 || n.isInteractingWithLocalPlayer()) {
							return true;
						}
					}
					return false;
				}
			});

			if (out != null) {
				return out;
			}

		}
		return out;
	}
}
