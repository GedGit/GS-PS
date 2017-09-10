package org.rs2server.rs2.domain.model.player.treasuretrail.step;

import org.rs2server.rs2.content.dialogue.Dialogue;
import org.rs2server.rs2.content.dialogue.TalkingDialogue;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import javax.annotation.Nonnull;

/**
 * A clue step which requires the player to perform an action to spawn Uri.
 *
 * @author tommo
 */
public class TreasureTrailUriStep extends TreasureTrailClueStep {

	public static final int URI_NPC_ID = 1774;
	public static final Graphic URI_NPC_SMOKE_GRAPHIC = Graphic.create(86);

	/**
	 * The dialogue which will occur if a player who talks to a Uri which wasn't spawned for them.
	 */
	public static final Dialogue URI_NO_BUSINESS_DIALOGUE = TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANNOYED,
			"I do not believe we have any business, Comrade.");

	public static final Dialogue[] URI_DIALOGUES = {
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Once, I was a poor man, but then I found a party hat."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_2, "There were three goblins in a bar, which one left first?"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_3, "Would you like to buy a pewter spoon?"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_4, "In the end, only the three-legged survive."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I heard that the tall man fears only strong winds."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_2, "In Canifis the men are known for eating much spam."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I am the egg man, are you one of the egg men?"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "The sudden appearance of a deaf squirrel is most puzzling, Comrade."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I believe that it is very rainy in Varrock."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "The slowest of fisherman catch the swiftiest of fish"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "It is quite easy being green."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Don't forget to find the jade monkey."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Brother, do you even lift?"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Do you want ants? Because that's how you get ants."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I once named a duck after a girl. Big mistake, they all hated it."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Loser says what."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I'm looking for a girl named Molly. I can't seem to find her. Any assistance?"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Fancy a holiday? I heard there was a whole other world to the west."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Guys, let's lake dive!"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I gave you what you needed; not what you think you needed."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Want to see me bend a spoon?"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Is that Deziree?"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "This is the last night you'll spend alone."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "(Breathing intensifies)"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Init doe. Lyk, I hope yer reward iz goodd aye?"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I'm going to get married, to the night."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I took a college course in bowling; still not any good."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Tonight we dine............... quite nicely actually."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I don't like pineapple, it has that bone in it."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Some say..."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "I told him not to go near that fence, and what did he do? Sheesh..."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Connection lost. Please wait - attempting to reestablish."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "There's this guy I sit next to. Makes weird faces and sounds. Kind of an odd fellow."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Mate, mate... I'm the best."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "Hurry, there's a bee sticking out of my arm!"),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "The Ankou's are a lie."),
			TalkingDialogue.npcSaying(URI_NPC_ID, "Uri", Animation.FacialAnimation.ANGER_1, "9 years my princess, forever my light.")
	};

	public void spawnUri(@Nonnull Player player) {
		final Location spawnLocation = player.getLocation().closestFreeTileOrSelf(player.getLocation(), 1, 1);
		final NPC uri = new NPC(URI_NPC_ID, spawnLocation, spawnLocation, spawnLocation, 0);
		uri.setLocation(spawnLocation);
		uri.setInstancedPlayer(player);

		World.getWorld().register(uri);
		uri.playGraphics(URI_NPC_SMOKE_GRAPHIC);

		uri.setInteractingEntity(Mob.InteractionMode.TALK, player);
		player.setInteractingEntity(Mob.InteractionMode.TALK, uri);

		// Make sure to remove Uri if he wasn't interacted with.
		World.getWorld().submit(new Tickable(16) {
			@Override
			public void execute() {
				if (!uri.isDestroyed()) {
					despawnUri(uri);
					stop();
				}
			}
		});
	}

	public void despawnUri(@Nonnull NPC uri) {
		uri.playGraphics(URI_NPC_SMOKE_GRAPHIC);
		World.getWorld().unregister(uri);
	}

}
