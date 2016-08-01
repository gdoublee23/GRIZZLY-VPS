package MusaBDrags;

import org.tbot.wrappers.Character;
import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.Camera;
import org.tbot.methods.GameObjects;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Time;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.util.Filter;
import org.tbot.wrappers.GameObject;
import org.tbot.wrappers.Item;
import org.tbot.wrappers.Tile;

public class Traverse {

	static TileObject CRUMBLING_WALL = new TileObject("Crumbling wall", "Climb-over", 2936, 3354);
	static TileObject LADDER = new TileObject("Ladder", "Climb-down", 2884, 3398);
	static TileObject DOOR_WEST = new TileObject("Prison door", "Open", 2888, 9831);
	static TileObject DOOR_SOUTH = new TileObject("Door", "Open", 2892, 9827);
	static TileObject DOOR_SOUTH_OPEN = new TileObject("Door", "Close", 2892, 9826);
	static TileObject GATE = new TileObject("Gate", 2924, 9803);

	static Tile BLACK_KNIGHT_TILE = new Tile(2937, 9812);
	static Tile BRIDGE_TILE = new Tile(2930, 9756);
	
	static Tile[] EAST_PATH = { new Tile(2939, 9820), new Tile(2940, 9812), new Tile(2943, 9801), new Tile(2950, 9795),
			new Tile(2952, 9783), new Tile(2949, 9773) };
	static Tile[] BANK_TO_WALL = { new Tile(2938, 3367), new Tile(2938, 3357) };
	static Tile[] WALL_TO_LADDER = { new Tile(2926, 3358), new Tile(2920, 3365), new Tile(2913, 3372),
			new Tile(2904, 3378), new Tile(2898, 3388), new Tile(2892, 3395), new Tile(2884, 3396) };
	static Tile[] LADDER_TO_GATE = { new Tile(2885, 9809), new Tile(2884, 9821), new Tile(2887, 9830) };
	static Tile[] GATE_TO_DRAGS = { new Tile(2893, 9826), new Tile(2898, 9818), new Tile(2908, 9819),
			new Tile(2920, 9818), new Tile(2931, 9823), new Tile(2938, 9820), new Tile(2938, 9810),
			new Tile(2934, 9801), new Tile(2950, 9794), new Tile(2956, 9784), new Tile(2953, 9775),
			new Tile(2944, 9775), new Tile(2939, 9777), new Tile(2935, 9767), new Tile(2932, 9757),
			new Tile(2924, 9762), new Tile(2925, 9768), new Tile(2929, 9771), new Tile(2931, 9777), new Tile(2932, 9783),
			new Tile(2932, 9795), new Tile(2924, 9802) };

	public static void run() {

		LogHandler.log("L1AD");

		Tile t = Players.getLocal().getLocation();

		if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 5) {
			Walking.setRun(true);
		}

		if (Players.getLocal().isMoving() && Walking.getDestination().distance() > Random.nextInt(2, 7)) {
			if (Random.nextInt(0, 10) > 8) {
				Antiban.run();
			} else if (Random.nextInt(0, 10) > 4) {
				Time.sleep(Random.nextInt(300, 1200));
			}
		}

		if (Players.getLocal().getLocation().getY() > 9000 && !atDragons()) {

			int PLAYER_X = Players.getLocal().getLocation().getX();
			int PLAYER_Y = Players.getLocal().getLocation().getY();

			GameObject DGATE = GATE.getObject();
			GameObject WDOOR = DOOR_WEST.getObject();

			Tile DEMONS_DOOR = new Tile(2923, 9803);

			if (PLAYER_X > 2934 && PLAYER_Y > 9780) {
				walkPath(EAST_PATH);
			} else if ((DGATE != null && DGATE.getLocation().isOnMiniMap() && PLAYER_X >= DGATE.getLocation().getX())
					&& PLAYER_X < 2941 && PLAYER_Y < 9812 && PLAYER_Y > 9782) {
				LogHandler.log("DOING");
				if (DGATE.isOnScreen()) {
					GATE.interactWith("Dusty key");
				} else {
					Walking.walkTileMM(GATE.getLocation());
				}
			} else if (PLAYER_Y >= 9794 && (PLAYER_X <= 2889 || PLAYER_X < WDOOR.getLocation().getX())) {
				if (WDOOR != null && WDOOR.isOnScreen() && PLAYER_X < WDOOR.getLocation().getX()) {
					WDOOR.interact("Open");
					Time.sleep(400, 1400);
				} else {
					if (PLAYER_X >= 2889 && !atDragons()) {
						walkPath(GATE_TO_DRAGS);
					} else {
						walkPath(LADDER_TO_GATE);
					}
				}
			} else {
				GameObject SDOOR = DOOR_SOUTH.getObject();
				if (SDOOR != null && SDOOR.getActions()[0].equals("Open") && SDOOR.isOnScreen()) {
					SDOOR.interact("Open");
				} else {
					walkPath(GATE_TO_DRAGS);
				}
			}
			/**
			 * if (t.getX() < 2889 && t.getY() > 9795 && t.getX() < 2897) {
			 * DOOR_WEST.interact(); } else if (t.getX() >= 2889 && t.getY() >=
			 * 9825) { GameObject o = DOOR_SOUTH.getObject(); if
			 * (o.hasAction("Close")) { GATE.interactWith("Dusty key"); } else {
			 * DOOR_SOUTH.interact(); } } else { LogHandler.log("YAS");
			 * GATE.interactWith("Dusty key"); }
			 **/

		} else if (t.getX() > 2935) {
			if (CRUMBLING_WALL.getObject() != null && CRUMBLING_WALL.getObject().isOnScreen()) {
				CRUMBLING_WALL.interact();
			} else {
				walkPath(BANK_TO_WALL);
			}
		} else {
			if (LADDER.getObject() != null && LADDER.getObject().isOnScreen()) {
				LADDER.interact();
			} else {
				LogHandler.log("WALL");
				walkPath(WALL_TO_LADDER);
			}
		}

	}

	static boolean isUnderground() {
		return Players.getLocal().getLocation().getY() > 9000;
	}

	public static boolean atDragons() {
		Tile t = Players.getLocal().getLocation();
		int minX = 2891;
		int maxX = 2923;
		int minY = 9785;
		int maxY = 9813;
		return t.getX() >= minX && t.getX() <= maxX && t.getY() <= maxY && t.getY() >= minY;
	}

	public static boolean isReady() {

		int PLAYER_X = Players.getLocal().getLocation().getX();
		int PLAYER_Y = Players.getLocal().getLocation().getY();

		Character CHAR = Players.getLocal().getInteractingEntity();
		
		if(CHAR != null){
			if(CHAR.getName().equals("Blue dragon") || CHAR.getName().equals("Baby dragon")){
				return false;
			}
		}
		
		int minX = 2888;
		int maxX = 2923;
		int minY = 9785;
		int maxY = 9813;
		
		if(PLAYER_Y <= 9813 && PLAYER_X >= 2887 && PLAYER_Y >= 9784 && PLAYER_X <= 2923){
			return false;
		}
		
		if (atDragons()) {
			return false;
		} else if (isUnderground()) {
			if (PLAYER_X > 2937 || PLAYER_Y > 9790) {
				return true;
			} else if(PLAYER_Y < 9776){
				return true;
			}
			Tile t = Players.getLocal().getLocation();
		
			return (t.getX() < minX || t.getX() > maxX) && (t.getY() > maxY || t.getY() < minY);
		}
		return !Banking.isReady();
	}

	public static void walkTo(Tile t) {
		if (t.isOnScreen()) {
			Walking.walkTileOnScreen(t);
		} else {
			Walking.walkTileMM(t);
		}
	}

	private static GameObject getObject(Tile TILE) {

		GameObject OUT = null;

		OUT = GameObjects.getNearest(new Filter<GameObject>() {
			public boolean accept(GameObject o) {
				if (o.getName().equals("Gate") && o.distance(TILE) < 5) {
					LogHandler.log(o.getActions()[0]);
					if (o.getActions()[0].equals("Open")) {
						Tile DEMONS_DOOR = new Tile(2923, 9803);
						return o.distance(DEMONS_DOOR) > 4;
					}
				}
				return false;
			}
		});

		if (OUT != null) {
			return OUT;
		}

		return null;
	}

	public static void walkPath(Tile[] PATH) {

		int INDEX = getTileIndex(PATH);

		Tile DESTINATION = PATH[INDEX];

		GameObject OBJECT = getObject(DESTINATION);

		LogHandler.log(DESTINATION);

		if (!DESTINATION.isOnMiniMap()) {
			DESTINATION = PATH[INDEX - 1];
		}

		LogHandler.log(DESTINATION);

		if (OBJECT == null) {
			OBJECT = getObject(DESTINATION);
		}

		int X = DESTINATION.getX() + Random.nextInt(-1, 1);
		int Y = DESTINATION.getY() + Random.nextInt(-1, 1);

		DESTINATION = new Tile(X, Y);

		LogHandler.log(DESTINATION + "::");

		if (OBJECT != null && OBJECT.getLocation().isOnMiniMap()) {
			LogHandler.log("ASD");
			if (OBJECT.isOnScreen()) {
				OBJECT.interact(OBJECT.getActions()[0]);
			} else {
				Walking.walkTileMM(OBJECT.getLocation());
			}
		} else if (DESTINATION.isOnScreen()) {
			if (DESTINATION.distance() > Random.nextInt(2, 3)) {
				Walking.walkTileOnScreen(DESTINATION);
			}
		} else {
			if(Random.nextInt(0, 10) > 8){
				Camera.turnTo(DESTINATION);
			}
			Walking.walkTileMM(DESTINATION);
		}

		Time.sleep(300, 700);

	}

	public static int getTileIndex(Tile[] PATH) {

		int OUT = PATH.length - 1;

		for (int i = OUT - 1; i >= 0; i--) {
			if ((getObject(PATH[i]) != null && PATH[i].isOnScreen())) {
				return OUT;
			}
			if ((PATH[i].distance() < PATH[OUT].distance())) {
				if (PATH[i].distance() < 5 || PATH[i + 1].isOnMiniMap()) {
					if (PATH[i].getX() < 2937) {
						OUT = i + 1;
					}
				}
				OUT = i;
			}
		}
		return OUT + 1;
	}

}
