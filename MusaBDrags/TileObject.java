package MusaBDrags;

import org.tbot.internal.handlers.LogHandler;
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

public class TileObject {

	String name;
	String action;

	Tile location;

	public TileObject(String n, String a, int x, int y) {
		name = n;
		action = a;
		location = new Tile(x, y);
	}

	public TileObject(String n, int x, int y) {
		name = n;
		location = new Tile(x, y);
	}

	protected void interactWith(String s) {

		GameObject object = GameObjects.getNearest(new Filter<GameObject>() {
			public boolean accept(GameObject o) {
				return o.getName().equals(name) && o.distance(location) < 5;
			}
		});

		if (object.isOnScreen()) {
			Item i = Inventory.getFirst(s);
			if (i != null) {
				if (i.click()) {
					object.interact("Use");
					Time.sleep(Random.nextInt(700, 1500));
				}
			}
		} else {
			Walking.walkTileMM(location);
		}

	}

	protected Tile getLocation() {
		return location;
	}
	
	protected boolean hasAction(String a){
		
		GameObject object = GameObjects.getNearest(new Filter<GameObject>() {
			public boolean accept(GameObject o) {
				return o.getName().equals(name) && o.distance(location) < 5;
			}
		});
		
		for(String s : object.getActions()){
			if(s.equals(a)){
				return true;
			}
		}
		
		return false;
	}
	
	protected GameObject getObject(){
		
		GameObject object = GameObjects.getNearest(new Filter<GameObject>() {
			public boolean accept(GameObject o) {
				return o.getName().equals(name) && o.distance(location) < 5;
			}
		});
		
		return object;
	}

	protected void interact() {

		GameObject o = GameObjects.getNearest(name);
		if (o != null && o.isOnScreen() && o.distance(location) < 5) {
			o.interact(action);
		} else {
			Walking.findPath(location).traverse();
		}
		Time.sleep(Random.nextInt(300, 1500));

	}

	protected boolean exists() {

		GameObject object = GameObjects.getNearest(new Filter<GameObject>() {
			public boolean accept(GameObject o) {
				if (o.getName().equals(name)) {
					for (String s : o.getActions()) {
						if (s.equals(action)) {
							LogHandler.log(name + ":" + action);
							if (o.distance(location) < 5) {
								LogHandler.log(name + ":" + action + ":" + "asdasd");
								return true;
							}
						}
					}
				}
				return false;
			}
		});

		if (object != null) {
			for (String s : object.getActions()) {
				if (s.equals(action)) {
					LogHandler.log(action);
					return true;
				}
			}
		}

		return false;

	}
}
