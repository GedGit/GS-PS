package org.rs2server.rs2.content;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.content.StaffService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.smithing.SmithingUtils;
import org.rs2server.rs2.model.skills.smithing.SuperheatItemAction;
import org.rs2server.rs2.tickable.impl.StoppingTick;

import java.util.Objects;
import java.util.Optional;

public class Magic {

	private final StaffService staffService;
	private final GroundItemService groundItemService;
	private final Player player;
	private static final int TELEGRAB_PROJECTILE = 143;

	public Magic(Player player) {
		this.player = player;
		this.staffService = Server.getInjector().getInstance(StaffService.class);
		this.groundItemService = Server.getInjector().getInstance(GroundItemService.class);
	}

	public void handleTeleGrab(GroundItemService.GroundItem item) {
		if (player.getSkills().getLevelForExperience(Skills.MAGIC) < 33) {
			player.sendMessage("You need a Magic level of 33 to use this spell.");
			return;
		}
		if (!staffService.containsRune(player, new Item(556)) || !staffService.containsRune(player, new Item(563))) {
			player.sendMessage("You do not have the required runes to cast this spell.");
			return;
		}
		if (player.hasAttribute("lastTeleGrab")
				&& System.currentTimeMillis() - (long) player.getAttribute("lastTeleGrab") < 4000) {
			return;
		}
		int clientSpeed;
		int gfxDelay;
		if (player.getLocation().distance(item.getLocation()) <= 1) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if (player.getLocation().distance(item.getLocation()) >= 1
				&& player.getLocation().distance(item.getLocation()) <= 5) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if (player.getLocation().distance(item.getLocation()) >= 5
				&& player.getLocation().distance(item.getLocation()) <= 8) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		int delay = (gfxDelay / 20) - 1;
		player.face(item.getLocation());
		player.playAnimation(Animation.create(728));
		player.playGraphics(Graphic.create(142, 0, 100));
		Projectile projectile = Projectile.create(player.getLocation(), item.getLocation(), TELEGRAB_PROJECTILE, 45, 50,
				clientSpeed, 43, 0, 10, 48);
		player.playProjectile(projectile);
		Inventory.removeRune(player, new Item(556));
		Inventory.removeRune(player, new Item(563));
		player.setAttribute("lastTeleGrab", System.currentTimeMillis());
		World.getWorld().submit(new StoppingTick(delay) {
			@Override
			public void executeAndStop() {
				Optional<GroundItemService.GroundItem> groundItemOptional = groundItemService
						.getGroundItem(item.getItem().getId(), item.getLocation());
				if (groundItemOptional.isPresent()) {
					World.getWorld().getRegionManager().getRegionByLocation(groundItemOptional.get().getLocation())
							.getPlayers().stream().filter(Objects::nonNull).forEach(i -> i.getActionSender()
									.sendStillGFX(144, 0, groundItemOptional.get().getLocation()));
					groundItemService.teleGrabItem(player, groundItemOptional.get());
				}
			}
		});
	}

	public void handleMagicOnItem(Item item, int spellId, int slot) {
		if (System.currentTimeMillis() - player.lastMagicOnItem > 2000) {
			player.lastMagicOnItem = System.currentTimeMillis();
			switch (player.getCombatState().getSpellBook()) {// Could use that enum, but w/e.
			case 0:// Modern magic
				switch (spellId) {
				/*
				 * Superheat item
				 */
				case 23:
					if (player.getSkills().getLevel(Skills.MAGIC) < 41) {
						player.sendMessage("You need a Magic level of 41 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(561))
							|| !staffService.containsRune(player, new Item(554, 4))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}
					boolean success = false;
					for (final SmithingUtils.SmeltingBar bar : SmithingUtils.SmeltingBar.values()) {
						if (bar.getItemsRequired()[0].getId() == item.getId()) {
							SmithingUtils.SmeltingBar smeltingBar = bar;
							if (smeltingBar.getItemsRequired()[0].getId() == 440
									&& player.getInventory().getCount(453) >= 2) {
								smeltingBar = SmithingUtils.SmeltingBar.STEEL;
							}
							player.getActionQueue().addAction(new SuperheatItemAction(player, smeltingBar, 1));
							success = true;
							break;
						}
					}

					if (!success)
						player.sendMessage("You need to cast superheat item on ore.");

					break;
				/*
				 * Low level alching..
				 */
				case 11:
					if (player.getSkills().getLevel(Skills.MAGIC) < 21) {
						player.sendMessage("You need a Magic level of 21 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(561))
							|| !staffService.containsRune(player, new Item(554, 3))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}
					if (item.getId() == 995) {
						player.sendMessage("You cannot convert gold into gold.");
						return;
					}
					if (item.getDefinition() == null)
						return;
					for (int destroyableId : Constants.DESTROYABLE_ITEMS) {
						if (destroyableId == item.getId()) {
							player.sendMessage("You cannot alch this item.");
							return;
						}
					}
					int price = item.getDefinition2().getLowAlch();
					if (item.getDefinition().isNoted()) {
						Item unnoted = new Item(item.getId() - 1);
						price = unnoted.getDefinition2().getLowAlch();
						if (price <= 0) {
							player.sendMessage(
									"Error occured price was less than or equal to 0, Please report to a staff member.");
							return;
						}
					}
					if (price <= 0) {
						player.sendMessage("Item has no price; report to an admin!");
						return;
					}
					player.playAnimation(Animation.create(-1));
					player.playAnimation(Animation.create(712));
					player.playGraphics(Graphic.create(112, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 15);
					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(995, price));
					player.getActionSender().switchTab(4);
					staffService.removeRune(player, new Item(561));
					staffService.removeRune(player, new Item(554, 3));
					break;
				/*
				 * High level alching..
				 */
				case 32:
					if (player.getSkills().getLevel(Skills.MAGIC) < 55) {
						player.sendMessage("You need a Magic level of 55 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(561))
							|| !staffService.containsRune(player, new Item(554, 5))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}
					if (item.getId() == 995) {
						player.sendMessage("You cannot convert gold into gold.");
						return;
					}
					if (item.getDefinition() == null) {
						player.sendMessage("You cannot alch this item.");
						return;
					}
					for (int destroyableId : Constants.DESTROYABLE_ITEMS) {
						if (destroyableId == item.getId()) {
							player.sendMessage("You cannot alch this item.");
							return;
						}
					}
					price = item.getDefinition2().getHighAlch();
					if (item.getDefinition().isNoted()) {
						Item unnoted = new Item(item.getId() - 1);
						price = unnoted.getDefinition2().getHighAlch();
						if (price <= 0) {
							player.sendMessage(
									"Error occured price was less than or equal to 0, Please report to a staff member.");
							return;
						}
					}
					if (price <= 0) {
						player.sendMessage(
								"Error occured price was less than or equal to 0, Please report to a staff member.");
						return;
					}
					player.playAnimation(Animation.create(713));
					player.playGraphics(Graphic.create(113, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 65);
					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(995, price));
					player.getActionSender().switchTab(4);
					staffService.removeRune(player, new Item(561));
					staffService.removeRune(player, new Item(554, 5));
					break;
				/*
				 * Level 1 Enchanting.
				 */
				case 3:
					if (player.getSkills().getLevel(Skills.MAGIC) < 7) {
						player.sendMessage("You need a Magic level of 7 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(555))
							|| !staffService.containsRune(player, new Item(564))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}
					if (item.getId() == 1637) { // Sapphire ring
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(238, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 44);
						staffService.removeRune(player, new Item(555));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(2550));
						player.getActionSender().switchTab(4);
					}
					player.getInventory().remove(slot, new Item(item.getId()));

					if (item.getId() == 11071) { // Sapphire braclet
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 44);
						staffService.removeRune(player, new Item(555, 1));
						staffService.removeRune(player, new Item(564, 1));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11074));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1656) { // Sapphire necklace
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 44);
						staffService.removeRune(player, new Item(555));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(3853));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1694) { // Sapphire ammy
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 44);
						staffService.removeRune(player, new Item(555));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(1727));
						player.getActionSender().switchTab(4);
					}
					break;
				/*
				 * Level 2 Enchanting.
				 */
				case 14:
					if (player.getSkills().getLevel(Skills.MAGIC) < 27) {
						player.sendMessage("You need a Magic level of 27 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(556, 3))
							|| !staffService.containsRune(player, new Item(564))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}
					if (item.getId() == 1639) { // Emerald ring
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(238, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 55);
						staffService.removeRune(player, new Item(556, 3));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(2552));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1656) { // Emerald necklace
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 55);
						staffService.removeRune(player, new Item(556, 3));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(5521));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 11078) { // emerald bracelet
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 55);
						staffService.removeRune(player, new Item(556, 3));
						staffService.removeRune(player, new Item(564, 1));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11079));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1696) { // Emerald ammy
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 55);
						staffService.removeRune(player, new Item(556, 3));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(1729));
						player.getActionSender().switchTab(4);
					}
					break;
				/*
				 * Level 3 Enchanting.
				 */
				case 26:
					if (player.getSkills().getLevel(Skills.MAGIC) < 49) {
						player.sendMessage("You need a Magic level of 49 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(554, 5))
							|| !staffService.containsRune(player, new Item(564))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}
					if (item.getId() == 1641) { // Ruby ring
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(238, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 66);
						staffService.removeRune(player, new Item(554, 5));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(2568));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 11087) { // Ruby bracelet
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(238, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 66);
						staffService.removeRune(player, new Item(554, 5));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11088));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1660) { // Ruby necklace
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(115, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 66);
						staffService.removeRune(player, new Item(554, 5));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11195));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1698) { // Ruby ammy
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 66);
						staffService.removeRune(player, new Item(554, 5));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(1725));
						player.getActionSender().switchTab(4);
					}
					break;
				/*
				 * Level 4 Enchanting.
				 */
				case 34:
					if (player.getSkills().getLevel(Skills.MAGIC) < 57) {
						player.sendMessage("You need a Magic level of 57 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(557, 10))
							|| !staffService.containsRune(player, new Item(564))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}
					if (item.getId() == 1643) { // Diamond ring
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(238, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 77);
						staffService.removeRune(player, new Item(557, 10));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(2570));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 11094) { // diamond bracelet
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 77);
						staffService.removeRune(player, new Item(557, 10));
						staffService.removeRune(player, new Item(564, 1));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11095));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1662) { // Diamond necklace
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(115, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 77);
						staffService.removeRune(player, new Item(557, 10));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11090));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1700) { // Diamond ammy
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 77);
						staffService.removeRune(player, new Item(557, 10));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(1731));
						player.getActionSender().switchTab(4);
					}
					break;
				/*
				 * Level 5 Enchanting.
				 */
				case 49:
					if (player.getSkills().getLevel(Skills.MAGIC) < 57) {
						player.sendMessage("You need a Magic level of 57 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(555, 15))
							|| !staffService.containsRune(player, new Item(564))
							|| !staffService.containsRune(player, new Item(557, 15))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}
					if (item.getId() == 1645) { // Dragonstone ring
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(238, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 87);
						staffService.removeRune(player, new Item(555, 15));
						staffService.removeRune(player, new Item(557, 15));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(2572));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 11115) { // dragonstone braclet
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 87);
						staffService.removeRune(player, new Item(555, 15));
						staffService.removeRune(player, new Item(557, 15));
						staffService.removeRune(player, new Item(564, 1));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11972));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1664) { // Dragonstone necklace
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(115, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 87);
						staffService.removeRune(player, new Item(555, 15));
						staffService.removeRune(player, new Item(557, 15));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11113));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 1702) { // Dragonstone ammy
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, (100 << 16)));
						player.getSkills().addExperience(Skills.MAGIC, 87);
						staffService.removeRune(player, new Item(555, 15));
						staffService.removeRune(player, new Item(557, 15));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(1712));
						player.getActionSender().switchTab(4);
					}
					break;
				/*
				 * Level 6 Enchanting.
				 */
				case 61:
					if (player.getSkills().getLevel(Skills.MAGIC) < 87) {
						player.sendMessage("You need a Magic level of 87 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(554, 20))
							|| !staffService.containsRune(player, new Item(564))
							|| !staffService.containsRune(player, new Item(557, 20))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}
					if (item.getId() == 6581) { // Fury Ammy
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, 0, 100));
						player.getSkills().addExperience(Skills.MAGIC, 97);
						staffService.removeRune(player, new Item(554, 20));
						staffService.removeRune(player, new Item(557, 20));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(6585));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 6577) { // Onyx necklace
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, 0, 100));
						player.getSkills().addExperience(Skills.MAGIC, 97);
						staffService.removeRune(player, new Item(554, 20));
						staffService.removeRune(player, new Item(557, 20));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11128));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 11132) { // onyx braclet
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, 0, 100));
						player.getSkills().addExperience(Skills.MAGIC, 97);
						staffService.removeRune(player, new Item(554, 20));
						staffService.removeRune(player, new Item(557, 20));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(11133));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 6575) { // Onyx ring
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, 0, 100));
						player.getSkills().addExperience(Skills.MAGIC, 97);
						staffService.removeRune(player, new Item(554, 20));
						staffService.removeRune(player, new Item(557, 20));
						staffService.removeRune(player, new Item(564));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(6583));
						player.getActionSender().switchTab(4);
					}
					break;
				// Level 7 enchanting
				case 63:
					if (player.getSkills().getLevel(Skills.MAGIC) < 90) {
						player.sendMessage("You need a Magic level of 90 to cast this spell.");
						return;
					}
					if (!staffService.containsRune(player, new Item(565, 20))
							|| !staffService.containsRune(player, new Item(566, 20))
							|| !staffService.containsRune(player, new Item(564, 1))) {
						player.sendMessage("You do not have the required runes to cast this spell.");
						return;
					}

					if (item.getId() == 19492) { // zenyte bracelet
						player.playAnimation(Animation.create(712)); // SET CORRECT ONE
						player.playGraphics(Graphic.create(114, 0, 100));
						player.getSkills().addExperience(Skills.MAGIC, 110);
						staffService.removeRune(player, new Item(565, 20));
						staffService.removeRune(player, new Item(566, 20));
						staffService.removeRune(player, new Item(564, 1));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(19544));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 19535) { // zenyte necklace
						player.playAnimation(Animation.create(712));
						player.playGraphics(Graphic.create(114, 0, 100));
						player.getSkills().addExperience(Skills.MAGIC, 110);
						staffService.removeRune(player, new Item(565, 20));
						staffService.removeRune(player, new Item(566, 20));
						staffService.removeRune(player, new Item(564, 1));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(19547));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 19538) { // zenyte ring
						player.playAnimation(Animation.create(712));
						player.playGraphics(Graphic.create(114, 0, 100));
						player.getSkills().addExperience(Skills.MAGIC, 110);
						staffService.removeRune(player, new Item(565, 20));
						staffService.removeRune(player, new Item(566, 20));
						staffService.removeRune(player, new Item(564, 1));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(19550));
						player.getActionSender().switchTab(4);
					}
					if (item.getId() == 19541) { // zenyte amulet
						player.playAnimation(Animation.create(712));
						player.playGraphics(Graphic.create(114, 0, 100));
						player.getSkills().addExperience(Skills.MAGIC, 110);
						staffService.removeRune(player, new Item(565, 20));
						staffService.removeRune(player, new Item(566, 20));
						staffService.removeRune(player, new Item(564, 1));
						player.getInventory().remove(slot, new Item(item.getId()));
						player.getInventory().add(new Item(19553));
						player.getActionSender().switchTab(4);
					}

					break;
				default:
					System.out.println("Unhandled magic on item spell: " + spellId + ".");
				}
				break;
			}
		}
	}
}