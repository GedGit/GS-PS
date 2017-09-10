package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.NPCDefinition;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;


/**
 * Solus Dellagar "A Mage's Revenge" boss
 * @author Canownueasy
 *
 */
public class SolusDellagar extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final SolusDellagar INSTANCE = new SolusDellagar();
	
	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}
	
	private enum CombatStyle {
		MAGIC,
		SUMMON,
		FREEZE,
		HEAL,
		RAPID
	}
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		final NPC npc = (NPC) attacker;
		final Player player = (Player) victim;
		
		CombatStyle style = CombatStyle.MAGIC;
		
		switch(Misc.random(10)) {
			default:
				style = CombatStyle.MAGIC;
				break;
			case 4:
			case 5:
			case 9:
				style = CombatStyle.FREEZE;
				break;
			case 6:
				style = CombatStyle.SUMMON;
				break;
			case 10:
				style = CombatStyle.HEAL;
				break;
			case 8:
				style = CombatStyle.RAPID;
				break;
		}
		
		if(style == CombatStyle.RAPID && Misc.getDistance(attacker.getLocation(), victim.getLocation()) > 1) {
			style = CombatStyle.MAGIC;
		}
		
		if(style == CombatStyle.HEAL) {
			if(attacker.getSkills().getLevel(Skills.HITPOINTS) >= 400) {
				style = CombatStyle.MAGIC;
			}
		}
		
		int maxHit;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;
		int clientSpeed;
		int gfxDelay;
		switch(style) {
		case RAPID:
			maxHit = 14;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
				maxHit = (int) (maxHit * 0.4);
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			blockAnimation = true;
			hitDelay = 0;
			hit = randomHit;
			break;
		case HEAL:
			hit = 0;
			blockAnimation = false;
			hitDelay = 2;
			attacker.playGraphics(Graphic.create(84));
			attacker.playAnimation(Animation.create(7001));
			attacker.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, Misc.random(40, attacker.getSkills().getLevel(Skills.HITPOINTS) < 100 ? 90 : 50));
			player.getActionSender().sendMessage("The powerful mage heals himself!");
			break;
		case FREEZE:
			maxHit = 29;
			blockAnimation = false;
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			
			attacker.playAnimation(Animation.create(1979));
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = (int) (maxHit * 0.4);
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		case SUMMON:
			blockAnimation = false;
			hitDelay = 3;
			attacker.playAnimation(Animation.create(7000));
			attacker.playGraphics(Graphic.create(1207));
			hit = 0;
			break;
		default:
		case MAGIC:
			maxHit = 41;
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			
			attacker.playAnimation(Animation.create(2890));
			attacker.playGraphics(Graphic.create(155));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 156, 45, 50, clientSpeed, 70, 35, victim.getProjectileLockonIndex(), 10, 48));

			blockAnimation = false;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = (int) (maxHit * 0.4);
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}//6994
		
		attacker.getCombatState().setAttackDelay(4);
		attacker.getCombatState().setSpellDelay(4);
		if(style == CombatStyle.RAPID) {
			attacker.getCombatState().setAttackDelay(1);
			attacker.getCombatState().setSpellDelay(1);
		}
		final CombatStyle preStyle = style;
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				switch(preStyle) {
				case RAPID:
					World.getWorld().submit(new Tickable(1) {
						public void execute() {
							if(npc.completedRapidAttacks >= 4) {
								this.stop();
								npc.completedRapidAttacks = 0;
								return;
							}
							npc.completedRapidAttacks++;
							npc.playAnimation(Animation.create(1667));
							attacker.getCombatState().setAttackDelay(1);
							attacker.getCombatState().setSpellDelay(1);
						}
					});
					break;
				case SUMMON:
					if(npc.spawnedAssistants >= 2) {
						this.stop();
						return;
					}
					Location loc1 = Location.create(2381, 4720);
					Location loc2 = Location.create(2387, 4720);
					final NPC mage1 = new NPC(NPCDefinition.quickDef(5250), loc1, loc1, loc1, 0);
					final NPC mage2 = new NPC(NPCDefinition.quickDef(5250), loc2, loc2, loc2, 0);
					World.getWorld().register(mage1);
					World.getWorld().register(mage2);
					npc.spawnedAssistants += 2;
					World.getWorld().submit(new Tickable(2) {
						public void execute() {
							mage1.setAggressiveDistance(10);
							mage2.setAggressiveDistance(10);
							attacker.playGraphics(Graphic.create(1246));
							mage1.playGraphics(Graphic.create(1246, 0, 100));
							mage2.playGraphics(Graphic.create(1246, 0, 100));
							//DialogueManager.openDialogue((Player) victim, 746);	
							this.stop();
							World.getWorld().submit(new Tickable(50) {
								public void execute() {
									if(mage1 == null || mage2 == null) {
										this.stop();
										return;
									}
									mage1.canAttack = true;
									player.getActionSender().sendMessage("A holy force grabs the minions away.");
									mage1.playAnimation(Animation.create(6994));
									mage2.playAnimation(Animation.create(6994));
									World.getWorld().submit(new Tickable(4) {
										public void execute() {
											if(mage1 == null || mage2 == null) {
												this.stop();
												return;
											}
											World.getWorld().unregister(mage1);
											World.getWorld().unregister(mage2);
											npc.spawnedAssistants = 0;
											this.stop();
										}
									});
									this.stop();
								}
							});
						}
					});
					break;
				case MAGIC:
					victim.playGraphics(Graphic.create(hit > 0 ? 157 : 85, 0, 100));
					break;
				case FREEZE:
					victim.playGraphics(Graphic.create(hit > 0 ? 369 : 85));
					victim.getCombatState().setCanMove(false);
					World.getWorld().submit(new Tickable(20) {
						public void execute() {
							victim.getCombatState().setCanMove(true);
							this.stop();
						}
					});
					World.getWorld().submit(new Tickable(1) {
						public void execute() {
							if(Misc.getDistance(attacker.getLocation(), victim.getLocation()) > 1) {
								this.stop();
								return;
							}
							if(attacker.getLocation().getY() == 4724) {
								this.stop();
								return;
							}
							int newY = (attacker.getLocation().getY() + 1);
							if(victim.getLocation().getY() > attacker.getLocation().getY()) {
								newY = (attacker.getLocation().getY() - 1);
							}
							attacker.getWalkingQueue().addStep(attacker.getLocation().getX(), newY);
							attacker.getWalkingQueue().finish();
							this.stop();
						}
					});
					break;
				default:
					break;
				}
				if(hit > 0) {
					victim.inflictDamage(new Hit(hit), attacker);
					smite(attacker, victim, hit);
					recoil(attacker, victim, hit);
					vengeance(attacker, victim, hit, 1);
				}
				this.stop();
			}
		});
		
		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}
	
	@Override
	public int distance(Mob attacker) {
		return 5;
	}
}