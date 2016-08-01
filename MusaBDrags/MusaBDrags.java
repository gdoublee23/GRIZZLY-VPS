package MusaBDrags;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.tbot.bot.TBot;
import org.tbot.internal.AbstractScript;
import org.tbot.internal.Manifest;
import org.tbot.internal.event.events.MessageEvent;
import org.tbot.internal.event.listeners.MessageListener;
import org.tbot.internal.event.listeners.PaintListener;
import org.tbot.internal.handlers.LogHandler;
import org.tbot.internal.handlers.RandomHandler;
import org.tbot.methods.Bank;
import org.tbot.methods.Game;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Skills;
import org.tbot.methods.Time;
import org.tbot.methods.Widgets;
import org.tbot.methods.Skills.Skill;
import org.tbot.methods.combat.magic.Magic;
import org.tbot.methods.combat.magic.Spell;
import org.tbot.methods.tabs.Equipment;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.wrappers.Character;
import org.tbot.wrappers.Item;
import org.tbot.wrappers.Tile;
import org.tbot.wrappers.WidgetChild;

import MusaOrbs.Glory;
import MusaOrbs.Locations;

@Manifest(name = "MusaBDrags", authors = "MansaMusa")
public class MusaBDrags extends AbstractScript implements PaintListener, MessageListener {

	static boolean IS_HOPPING = false;

	Image IMG;

	long START_TIME = System.currentTimeMillis();

	int START_XP = Skills.getExperience(Skill.MAGIC);
	int CURRENT_WORLD = Game.getCurrentWorld();
	int DEATHS = 0;

	int[] BAD_WORLDS = { 301, 316, 317, 318, 321, 325, 326, 335, 337, 345, 349, 353, 361, 365, 366, 373, 374, 378, 381,
			382, 383, 384, 385, Game.getCurrentWorld() };

	public boolean isSwitchedWorld() {
		if (Game.getCurrentWorld() != CURRENT_WORLD && Game.isMembersWorld()) {
			for (int i : BAD_WORLDS) {
				if (Game.getCurrentWorld() == i) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean onStart() {

		try {
			IMG = ImageIO.read(new URL("http://i.imgur.com/oljjpE3.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	public int loop() {

		try {

			if (!Bank.isOpen() && Magic.getAutoCastSpell() == null && Inventory.contains("Mind rune")
					&& Inventory.getCount("Air rune") > 1 && Equipment.contains("Staff of fire")) {
				LogHandler.log("YEAAA");
				WidgetChild SPELL = Widgets.getWidget(201, 0).getChild(4);
				WidgetChild SELECT = Widgets.getWidget(593, 24);
				WidgetChild TAB = Widgets.getWidget(548, 52);
				if (SPELL != null && SPELL.isVisible()) {
					SPELL.click();
				} else if (SELECT != null && SELECT.isVisible()) {
					SELECT.click();
				} else {
					TAB.click();
				}
				Time.sleep(300, 1200);
			}

			if (IS_HOPPING) {

				if (Game.isLoggedIn() && Locations.current() != Locations.UNDERGROUND) {
					IS_HOPPING = false;
				}

				TBot.getBot().getScriptHandler().getRandomHandler().disableAll();
				if (!SafeArea.CENTRE_AREA.containsPlayer()) {
					LogHandler.log("HOPPING: GETTING SAFE");
					Tile TILE = SafeArea.CENTRE_AREA.getTile();
					if (!TILE.isOnMiniMap()) {
						if (SafeArea.EAST_AREA.getTile().isOnMiniMap()) {
							Walking.walkTileMM(SafeArea.EAST_AREA.getTile());
						}
						if (Combat.EAST_TILE.isOnMiniMap()) {
							TILE = Combat.EAST_TILE;
						} else if (Combat.WEST_TILE.isOnMiniMap()) {
							TILE = Combat.WEST_TILE;
						} else {
							LogHandler.log("WE ARE FUCKED!");
						}
					}
					Walking.walkTileMM(TILE);
					Time.sleep(100, 500);
				} else {
					if (Game.isLoggedIn()) {
						LogHandler.log("HOPPING: LOGGING");
						CURRENT_WORLD = Game.getCurrentWorld();
						Game.logout();
					} else {
						LogHandler.log("HOPPING: SWITCHING");
						while (!isSwitchedWorld()) {
							for (int i = 302; i < 386; i++) {
								for (int WORLD : BAD_WORLDS) {
									if (i != WORLD && Random.nextInt(0, 1000) > 992) {
										Game.hop(i);
										Time.sleep(500);
										break;
									}
									break;
								}
							}
							Time.sleep(400, 1200);
						}
						TBot.getBot().getScriptHandler().getRandomHandler().enableAll();
						IS_HOPPING = false;
					}
				}
			} else {

				TBot.getBot().getScriptHandler().getRandomHandler().get(RandomHandler.TOGGLE_ROOF).disable();

				if (!Game.isLoggedIn()) {

				} else {

					if (!Players.getLocal().isMoving() || Walking.getDestination().distance() < Random.nextInt(0, 3)) {

						if (Eat.isReady()) {
							Eat.run();
						} else if (Traverse.isReady()) {
							Traverse.run();
						} else if (Traverse.atDragons()) {
							if (Inventory.isFull() && !Inventory.contains("Trout") && !Inventory.contains("Vial")) {
								Combat.LAST_DRAGON_ID = -1;
								tele();
							} else if (Loot.isReady()) {
								LogHandler.log("LOOT");
								Loot.run();
							} else if (Combat.isReady()) {
								LogHandler.log("COMBAT");
								Combat.run();
							}

						}

						if (Locations.current() != Locations.UNDERGROUND) {
							if (Banking.isReady()) {
								Banking.run();
							} else {
								Traverse.run();
							}
						}
					}
				}

			}

		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return Random.nextInt(10, 300);

	}

	public void tele() {
		if (!Inventory.isOpen()) {
			Inventory.openTab();
		} else {
			Item i = Inventory.getItemClosestToMouse("Falador teleport");
			if (i != null) {
				i.interact("Break");
				Time.sleep(400, 1600);
			}
		}
	}

	public void messageReceived(MessageEvent e) {
		LogHandler.log(e.getMessage().toString());
		if (Traverse.atDragons()) {
			if (!e.getSource().equals(Players.getLocal())) {
				if (e.getMessage().toString().contains("(2)")) {
					if (Random.nextInt(0, 10) > 3) {
						LogHandler.log(e.getMessage().toString());
						IS_HOPPING = true;
					}
				}
			}
		}
	}

	public String getTime() {
		DecimalFormat nf = new DecimalFormat("00");
		long millis = System.currentTimeMillis() - START_TIME;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
	}

	public String perHour(int gained) {
		return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - START_TIME)));
	}

	public String formatNumber(int start) {
		DecimalFormat nf = new DecimalFormat("0.0");
		double i = start;
		if (i >= 1000000) {
			return nf.format((i / 1000000)) + "m";
		}
		if (i >= 1000) {
			return nf.format((i / 1000)) + "k";
		}
		return "" + start;
	}

	public void onRepaint(Graphics g) {
		g.drawImage(IMG, 10, 5, null);

		int XP = Skills.getExperience(Skill.MAGIC) - START_XP;
		int AMOUNT = XP / 76;
		int PROFIT = 1500 * AMOUNT;

		g.setFont(new Font("Tahoma", Font.PLAIN, 12));
		g.drawString("Elapsed Time ", 120, 125);
		g.setFont(new Font("Tahoma", Font.BOLD, 18));
		g.drawString(getTime(), 140, 145);

		g.setFont(new Font("Tahoma", Font.PLAIN, 12));
		g.drawString("XP Gained", 70, 175);
		g.setFont(new Font("Tahoma", Font.BOLD, 18));
		g.drawString(formatNumber(XP), 90, 195);

		g.setFont(new Font("Tahoma", Font.PLAIN, 12));
		g.drawString("XP/Hour", 70, 225);
		g.setFont(new Font("Tahoma", Font.BOLD, 18));
		g.drawString(perHour(XP), 90, 245);

		g.setFont(new Font("Tahoma", Font.PLAIN, 12));
		g.drawString("Gross Revenue", 190, 175);
		g.setFont(new Font("Tahoma", Font.BOLD, 18));
		g.drawString("0", 210, 195);

		g.setFont(new Font("Tahoma", Font.PLAIN, 12));
		g.drawString("Gross/Hour", 190, 225);
		g.setFont(new Font("Tahoma", Font.BOLD, 18));
		g.drawString("0", 210, 245);

	}
}
