package MusaBDrags;

import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.util.Filter;
import org.tbot.wrappers.NPC;
import org.tbot.wrappers.Player;
import org.tbot.wrappers.Tile;

public class SafeArea {

	int minX;
	int maxX;
	int minY;
	int maxY;

	static SafeArea CENTRE_AREA = new SafeArea(2900, 2902, 9809, 9810);
	static SafeArea WEST_AREA = new SafeArea(2894, 2894, 9791, 9792);
	static SafeArea EAST_AREA = new SafeArea(2903, 2903, 9808, 9809);

	public SafeArea(int mnx, int mxx, int mny, int mxy) {
		minX = mnx;
		maxX = mxx;
		minY = mny;
		maxY = mxy;
	}

	boolean containsPlayer() {
		Tile t = Players.getLocal().getLocation();
		return t.getX() >= minX && t.getX() <= maxX && t.getY() >= minY && t.getY() <= minY;
	}

	Tile getTile() {
		return new Tile(Random.nextInt(minX, maxX), Random.nextInt(minY, maxY));
	}

	static SafeArea getArea(int i) {
		if (i == 265) {
			return CENTRE_AREA;
		} else if (i == 268) {
			return EAST_AREA;
		} else if (i == 267) {
			return WEST_AREA;
		}
		return null;
	}

	static SafeArea getArea(NPC n) {
		if (n != null) {
			if (n.getID() == 265) {
				return CENTRE_AREA;
			} else if (n.getID() == 268) {
				return EAST_AREA;
			} else if (n.getID() == 267) {
				return WEST_AREA;
			}
		}
		return null;
	}

}
