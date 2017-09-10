load("nashorn:mozilla_compat.js");//allows importPackage() usage

importPackage(org.rs2server.rs2)
importPackage(org.rs2server.rs2.model)
importPackage(org.rs2server.rs2.model.equipment)
importPackage(org.rs2server.rs2.model.combat.impl)

function windStrike(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(90, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 91, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(92, gfxDelay, 100), PoisonType.NONE, false, 2, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 5.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function waterStrike(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(93, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 94, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(95, gfxDelay, 100), PoisonType.NONE, false, 4, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 7.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function earthStrike(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(96, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 97, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(98, gfxDelay, 100), PoisonType.NONE, false, 6, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 9.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function fireStrike(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(99, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 100, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(101, gfxDelay, 100), PoisonType.NONE, false, 8, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 11.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function windBolt(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	var hasChaosGloves = attacker.getEquipment().contains(777);
	var maxHit = hasChaosGloves ? 12 : 9;
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(117, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 118, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(119, gfxDelay, 100), PoisonType.NONE, false, maxHit, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 13.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function waterBolt(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	var hasChaosGloves = attacker.getEquipment().contains(777);
	var maxHit = hasChaosGloves ? 13 : 10;
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(120, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 121, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(122, gfxDelay, 100), PoisonType.NONE, false, maxHit, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 16.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function earthBolt(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	var hasChaosGloves = attacker.getEquipment().contains(777);
	var maxHit = hasChaosGloves ? 14 : 11;
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(123, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 124, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(125, gfxDelay, 100), PoisonType.NONE, false, maxHit, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 19.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function fireBolt(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	var hasChaosGloves = attacker.getEquipment().contains(777);
	var maxHit = hasChaosGloves ? 15 : 12;
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(126, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 127, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(128, gfxDelay, 100), PoisonType.NONE, false, maxHit, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 21.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function windBlast(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(132, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 133, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(134, gfxDelay, 100), PoisonType.NONE, false, 13, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 25.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function waterBlast(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(135, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 136, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(137, gfxDelay, 100), PoisonType.NONE, false, 14, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 28.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function earthBlast(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(138, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 139, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(140, gfxDelay, 100), PoisonType.NONE, false, 15, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 31.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function fireBlast(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
	attacker.playGraphics(Graphic.create(129, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 130, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(131, gfxDelay, 100), PoisonType.NONE, false, 16, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 34.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function windWave(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1167 : 727));
	attacker.playGraphics(Graphic.create(158, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 159, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(160, gfxDelay, 100), PoisonType.NONE, false, 17, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 36);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function waterWave(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1167 : 727));
	attacker.playGraphics(Graphic.create(161, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 162, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(163, gfxDelay, 100), PoisonType.NONE, false, 18, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 37.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function earthWave(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1167 : 727));
	attacker.playGraphics(Graphic.create(164, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 165, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(166, gfxDelay, 100), PoisonType.NONE, false, 19, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 40);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function fireWave(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1167 : 727));
	attacker.playGraphics(Graphic.create(155, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 156, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(157, gfxDelay, 100), PoisonType.NONE, false, 20, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 42.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function tridentOfTheSeas(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
    attacker.playAnimation(Animation.create(728));
    attacker.playGraphics(Graphic.create(1251, 0, 100));
    attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1252, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
    magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(1253, gfxDelay, 100), PoisonType.NONE, false, 28, delay, 0);
    attacker.getSkills().addExperience(Skills.MAGIC, 1 * 42.5);
    attacker.getCombatState().setSpellDelay(4);
    attacker.getCombatState().setAttackDelay(3);
}

function tridentOfTheSwamp(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
    if (victim.isPlayer()) {
        attacker.getActionSender().sendMessage("You can't use this spell on players.");
        return;
    }
    attacker.playAnimation(Animation.create(728));
    attacker.playGraphics(Graphic.create(665, 0, 100));
    attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1040, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
    magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(1042, gfxDelay, 100), PoisonType.NONE, false, 31, delay, 0);
    attacker.getSkills().addExperience(Skills.MAGIC, 1 * 42.5);
    attacker.getCombatState().setSpellDelay(4);
    attacker.getCombatState().setAttackDelay(3);
}

function saradominStrike(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(811));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(76, 60, 100), PoisonType.NONE, false, 20 + (attacker.getCombatState().isCharged() ? 10 : 0), 3, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 35);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function clawsOfGuthix(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(811));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(77, 60, 100), PoisonType.NONE, false, 20 + (attacker.getCombatState().isCharged() ? 10 : 0), 3, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 35);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function flamesOfZamorak(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(811));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(78, 60, 0), PoisonType.NONE, false, 20 + (attacker.getCombatState().isCharged() ? 10 : 0), 3, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 35);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function ibanBlast(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(708));
	attacker.playGraphics(Graphic.create(87, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 88, 80, 50, clientSpeed, 50, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(89, gfxDelay, 100), PoisonType.NONE, false, 25, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 30);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function magicDart(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1576));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 328, 60, 50, clientSpeed, 48, 32, victim.getProjectileLockonIndex(), 5, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(329, gfxDelay, 100), PoisonType.NONE, false, 10 + (attacker.getSkills().getLevelForExperience(Skills.MAGIC) / 10), delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 30);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function crumbleUndead(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(autocast ? 1166 : 724));
	attacker.playGraphics(Graphic.create(145, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 146, 60, 50, clientSpeed, 40, 32, victim.getProjectileLockonIndex(), 5, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(147, gfxDelay, 100), PoisonType.NONE, false, 15, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 24.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function teleBlock(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1819));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 344, 60, 50, clientSpeed, 40, 32, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(345, gfxDelay, 0), PoisonType.NONE, false, 3, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 80);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function bind(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(710));
	attacker.playGraphics(Graphic.create(177, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 178, 80, 50, clientSpeed + 20, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(181, gfxDelay + 20, 100), PoisonType.NONE, false, 0, delay, 8);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 30);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function snare(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(710));
	attacker.playGraphics(Graphic.create(177, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 178, 80, 50, clientSpeed + 20, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(180, gfxDelay + 20, 100), PoisonType.NONE, false, 3, delay, 17);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 60.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function entangle(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(710));
	attacker.playGraphics(Graphic.create(177, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 178, 80, 50, clientSpeed + 20, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(179, gfxDelay + 20, 100), PoisonType.NONE, false, 5, delay, 25);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 91);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function confuse(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(716));
	attacker.playGraphics(Graphic.create(102, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 103, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(104, gfxDelay, 100), PoisonType.NONE, false, 0, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 13);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function weaken(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(717));
	attacker.playGraphics(Graphic.create(105, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 106, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(107, gfxDelay, 100), PoisonType.NONE, false, 0, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 21);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function curse(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(718));
	attacker.playGraphics(Graphic.create(108, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 109, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(110, gfxDelay, 100), PoisonType.NONE, false, 0, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 29);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function vulnerability(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(718));
	attacker.playGraphics(Graphic.create(167, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 168, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(169, gfxDelay, 100), PoisonType.NONE, false, 0, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 76);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function enfeeble(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(728));
	attacker.playGraphics(Graphic.create(170, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 171, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(172, gfxDelay, 100), PoisonType.NONE, false, 0, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 83);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function teleBlock(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1819));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 344, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(345, gfxDelay, 0), PoisonType.NONE, false, 0, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 90);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function stun(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(729));
	attacker.playGraphics(Graphic.create(173, 0, 100));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 174, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(175, gfxDelay, 100), PoisonType.NONE, false, 0, delay, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 90);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function smokeRush(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1978));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 384, 70, 50, 80, 48, 20, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(385, 80, 100), PoisonType.POISON, false, 13, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 30);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function shadowRush(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1978));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 378, 70, 50, 80, 48, 0, victim.getProjectileLockonIndex(), 0, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(379, 80, 0), PoisonType.NONE, false, 14, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 31);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function bloodRush(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1978));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(373, 80, 0), PoisonType.NONE, false, 15, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 33);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function iceRush(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1978));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 360, 70, 50, 80, 43, 0, victim.getProjectileLockonIndex(), 0, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(361, 80, 0), PoisonType.NONE, false, 17, 4, 8);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 34);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function smokeBurst(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1979));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(389, 80, 0), PoisonType.POISON, true, 17, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 35.5);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function shadowBurst(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1979));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(382, 80, 0), PoisonType.NONE, true, 18, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 37);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function bloodBurst(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1979));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(376, 80, 0), PoisonType.NONE, true, 21, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 39);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function iceBurst(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1979));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(363, 80, 0), PoisonType.NONE, true, 22, 4, 17);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 40);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function smokeBlitz(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1978));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 386, 70, 50, 80, 48, 20, victim.getProjectileLockonIndex(), 15, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(387, 80, 100), PoisonType.SUPER_POISON, false, 23, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 42);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function shadowBlitz(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1978));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 380, 70, 50, 80, 48, 0, victim.getProjectileLockonIndex(), 0, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(381, 80, 0), PoisonType.NONE, false, 24, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 43);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function bloodBlitz(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1978));
	attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 374, 70, 50, 80, 48, 0, victim.getProjectileLockonIndex(), 0, 48));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(375, 80, 0), PoisonType.NONE, false, 25, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 45);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function iceBlitz(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1978));
	attacker.playGraphics(Graphic.create(366));
	if(victim.getSprites().getPrimarySprite() != -1 || victim.getSprites().getSecondarySprite() != -1) {
		attacker.playProjectile(Projectile.create(victim.getCentreLocation(), victim.getCentreLocation(), 366, 70, 50, 90, 0, 0, victim.getProjectileLockonIndex(), 0, 48));
	}
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(367, 80, 0), PoisonType.NONE, false, 26, 3, 25);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 46);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function smokeBarrage(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1979));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(390, 80, 100), PoisonType.SUPER_POISON, true, 27, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 48);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function shadowBarrage(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1979));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(383, 80, 0), PoisonType.NONE, true, 28, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 49);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function bloodBarrage(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1979));
	magicCombatAction.hitEnemy(attacker, victim, spell, Graphic.create(377, 80, 0), PoisonType.NONE, true, 29, 4, 0);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 51);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function iceBarrage(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(1979));
	if(victim.getSprites().getPrimarySprite() != -1 || victim.getSprites().getSecondarySprite() != -1) {
		attacker.playProjectile(Projectile.create(victim.getCentreLocation(), victim.getCentreLocation(), 368, 70, 50, 90, 0, 0, victim.getProjectileLockonIndex(), 0, 48));
	}
	magicCombatAction.hitEnemy(attacker, victim, spell, (victim.getCombatState().canBeFrozen()) ? Graphic.create(369, 80, 0) : Graphic.create(369, 80, 0), PoisonType.NONE, true, 30, 4, 33);
	attacker.getSkills().addExperience(Skills.MAGIC, 1 * 52);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function varrockTeleport(mob) {
    mob.initiateTeleport(Mob.TeleportType.NORMAL_TELEPORT, Location.create(3213 + mob.getRandom().nextInt(2 + 1) - mob.getRandom().nextInt(2 + 1), 3424 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 0, 48));
    mob.getSkills().addExperience(Skills.MAGIC, 35);
}

function lumbridgeTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.NORMAL_TELEPORT, Location.create(3225 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 3218 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 41);
}

function faladorTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.NORMAL_TELEPORT, Location.create(2964 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 3378 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 48);
}

function camelotTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.NORMAL_TELEPORT, Location.create(2757 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 3477 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 55.5);
}

function ardougneTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.NORMAL_TELEPORT, Location.create(2662 + mob.getRandom().nextInt(4 + 1) - mob.getRandom().nextInt(4 + 1), 3305 + mob.getRandom().nextInt(4 + 1) - mob.getRandom().nextInt(4 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 61);
}

function watchtowerTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.NORMAL_TELEPORT, Location.create(2549, 3113, 2));
	mob.getSkills().addExperience(Skills.MAGIC, 68);
}

function trollheimTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.NORMAL_TELEPORT, Location.create(2891 + mob.getRandom().nextInt(2 + 1) - mob.getRandom().nextInt(2 + 1), 3680 + mob.getRandom().nextInt(2 + 1) - mob.getRandom().nextInt(2 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 68);
}

function paddewwaTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.ANCIENT_TELEPORT, Location.create(3097 + mob.getRandom().nextInt(4 + 1) - mob.getRandom().nextInt(4 + 1), 9882 + mob.getRandom().nextInt(4 + 1) - mob.getRandom().nextInt(4 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 64);
}

function senntistenTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.ANCIENT_TELEPORT, Location.create(3322 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 3336 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 70);
}

function kharyrllTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.ANCIENT_TELEPORT, Location.create(3493 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 3477 + mob.getRandom().nextInt(1 + 1) - mob.getRandom().nextInt(1 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 76);
}

function lassarTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.ANCIENT_TELEPORT, Location.create(3006 + mob.getRandom().nextInt(3 + 1) - mob.getRandom().nextInt(3 + 1), 3471 + mob.getRandom().nextInt(2 + 1) - mob.getRandom().nextInt(2 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 82);
}

function dareeyakTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.ANCIENT_TELEPORT, Location.create(2978 + mob.getRandom().nextInt(3 + 1) - mob.getRandom().nextInt(3 + 1), 3698 + mob.getRandom().nextInt(2 + 1) - mob.getRandom().nextInt(2 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 88);
}

function carrallangarTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.ANCIENT_TELEPORT, Location.create(3156 + mob.getRandom().nextInt(3 + 1) - mob.getRandom().nextInt(3 + 1), 3666 + mob.getRandom().nextInt(2 + 1) - mob.getRandom().nextInt(2 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 94);
}

function annakarlTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.ANCIENT_TELEPORT, Location.create(3288, 3886, 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 100);
}

function ghorrockTeleport(mob) {
	mob.initiateTeleport(Mob.TeleportType.ANCIENT_TELEPORT, Location.create(2977 + mob.getRandom().nextInt(3 + 1) - mob.getRandom().nextInt(3 + 1), 3873 + mob.getRandom().nextInt(2 + 1) - mob.getRandom().nextInt(2 + 1), 0, 48));
	mob.getSkills().addExperience(Skills.MAGIC, 106);
}

function vengeance(mob) {
	mob.playAnimation(Animation.create(4410));
	mob.playGraphics(Graphic.create(726, 0, 100));
	mob.getCombatState().setVengeance(true);
	mob.getCombatState().setCanVengeance(false);
	mob.getCombatState().setCanVengeance(50);
}

function vengeanceOther(mob, target) {
	mob.playAnimation(Animation.create(4411));
	target.playGraphics(Graphic.create(725, 0, 100));
	target.getCombatState().setVengeance(true);
	mob.getCombatState().setCanVengeance(false);
	mob.getCombatState().setCanVengeance(50);
}

function spellbookSwap(mob, spellBook) {
	mob.playAnimation(Animation.create(6299));
	mob.playGraphics(Graphic.create(1062));
	mob.getCombatState().setCanSpellbookSwap(false);
	mob.getCombatState().setCanSpellbookSwap(200);
	mob.getActionSender().sendSidebarInterface(105, MagicCombatAction.SpellBook.forId(spellBook).getInterfaceId());
	mob.getCombatState().setSpellBook(spellBook);
}

function charge(mob) {
	if(mob.getActionSender() != null) {
		mob.getActionSender().sendMessage("You feel charged with a magical power.");
	}
	mob.playAnimation(Animation.create(811));
	mob.playGraphics(Graphic.create(301));
	mob.getCombatState().setCharged(100 * 7);
}

function bandosSpiritualMage(attacker, victim, spell, autocast, clientSpeed, magicCombatAction, gfxDelay, delay) {
	attacker.playAnimation(Animation.create(4320));
	magicCombatAction.hitEnemy(attacker, victim, spell, null, PoisonType.NONE, false, 4, 1, 0);
	attacker.getCombatState().setSpellDelay(5);
	attacker.getCombatState().setAttackDelay(4);
}

function barrowsTeleport(mob) {
	mob.getSkills().addExperience(Skills.MAGIC, 90);
}

function barrowsTeleport(mob) {
    mob.initiateTeleport(Mob.TeleportType.NORMAL_TELEPORT, Location.create(3565, 3314, 0));
    mob.getSkills().addExperience(Skills.MAGIC, 90);
}