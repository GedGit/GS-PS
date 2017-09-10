package org.rs2server.rs2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single animation request.
 * 
 * @author Graham Edgecombe
 *
 */
public class Animation {

	/**
	 * Different animation constants.
	 */
	public final static Animation YES_EMOTE = create(855);
	public final static Animation NO_EMOTE = create(856);
	public final static Animation THINKING = create(857);
	public final static Animation BOW = create(858);
	public final static Animation ANGRY = create(859);
	public final static Animation CRY = create(860);
	public final static Animation LAUGH = create(861);
	public final static Animation CHEER = create(862);
	public final static Animation WAVE = create(863);
	public final static Animation BECKON = create(864);
	public final static Animation CLAP = create(865);
	public final static Animation DANCE = create(866);
	public final static Animation PANIC = create(2105);
	public final static Animation JIG = create(2106);
	public final static Animation SPIN = create(2107);
	public final static Animation HEADBANG = create(2108);
	public final static Animation JOYJUMP = create(2109);
	public final static Animation RASPBERRY = create(2110);
	public final static Animation YAWN = create(2111);
	public final static Animation SALUTE = create(2112);
	public final static Animation SHRUG = create(2113);
	public final static Animation BLOW_KISS = create(1368);
	public final static Animation GLASS_WALL = create(1128);
	public final static Animation LEAN = create(1129);
	public final static Animation CLIMB_ROPE = create(1130);
	public final static Animation GLASS_BOX = create(1131);
	public final static Animation GOBLIN_BOW = create(2127);
	public final static Animation GOBLIN_DANCE = create(2128);

	/**
	 * Creates an animation with no delay.
	 * 
	 * @param id
	 *            The id.
	 * @return The new animation object.
	 */
	public static Animation create(int id) {
		return create(id, 0);
	}

	/**
	 * Creates an animation.
	 * 
	 * @param id
	 *            The id.
	 * @param delay
	 *            The delay.
	 * @return The new animation object.
	 */
	public static Animation create(int id, int delay) {
		return new Animation(id, delay);
	}

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The delay.
	 */
	private int delay;

	/**
	 * Creates an animation.
	 * 
	 * @param id
	 *            The id.
	 * @param delay
	 *            The delay.
	 */
	private Animation(int id, int delay) {
		this.id = id;
		this.delay = delay;
	}

	/**
	 * Gets the id.
	 * 
	 * @return The id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the delay.
	 * 
	 * @return The delay.
	 */
	public int getDelay() {
		return delay;
	}

	public enum Emote {

		YES(0, Animation.create(855), null),

		NO(1, Animation.create(856), null),

		BOW(2, Animation.create(858), null),

		ANGRY(3, Animation.create(859), null),

		THINK(4, Animation.create(857), null),

		WAVE(5, Animation.create(863), null),

		SHRUG(6, Animation.create(2113), null),

		CHEER(7, Animation.create(862), null),

		BECKON(8, Animation.create(864), null),

		LAUGH(9, Animation.create(861), null),

		JUMP_FOR_JOY(10, Animation.create(2109), null),

		YAWN(11, Animation.create(2111), null),

		DANCE(12, Animation.create(866), null),

		JIG(13, Animation.create(2106), null),

		SPIN(14, Animation.create(2107), null),

		HEADBANG(15, Animation.create(2108), null),

		CRY(16, Animation.create(860), null),

		BLOW_KISS(17, Animation.create(1368), Graphic.create(574)),

		PANIC(18, Animation.create(2105), null),

		RASPBERRY(19, Animation.create(2110), null),

		CLAP(20, Animation.create(865), null),

		SALUTE(21, Animation.create(2112), null),

		GOBLIN_BOW(22, Animation.create(2127), null),

		GOBLIN_SALUTE(23, Animation.create(2128), null),

		GLASS_BOX(24, Animation.create(1131), null),

		CLIMB_ROPE(25, Animation.create(1130), null),

		LEAN(26, Animation.create(1129), null),

		GLASS_WALL(27, Animation.create(1128), null),

		SLAP_HEAD(28, Animation.create(4276), null),

		STOMP(29, Animation.create(4278), null),

		FLAP(30, Animation.create(4280), null),

		IDEA(31, Animation.create(4275), Graphic.create(712)),

		ZOMBIE_WALK(32, Animation.create(3544), null),

		ZOMBIE_DANCE(33, Animation.create(3543), null),

		ZOMBIE_HAND(34, Animation.create(7272), Graphic.create(1244)),

		SCARED(35, Animation.create(2836), null),

		BUNNY_HOP(36, Animation.create(6111), null);

		private int button;

		private Animation animation;

		private Graphic graphic;

		public static Emote forId(int button) {
			for (Emote emote : Emote.values()) {
				if (emote.getButton() == button)
					return emote;
			}
			return null;
		}

		private Emote(int button, Animation animation, Graphic graphic) {
			this.button = button;
			this.animation = animation;
			this.graphic = graphic;
		}

		/**
		 * @return the button
		 */
		public int getButton() {
			return button;
		}

		/**
		 * @return the animation
		 */
		public Animation getAnimation() {
			return animation;
		}

		/**
		 * @return the graphic
		 */
		public Graphic getGraphic() {
			return graphic;
		}
	}

	public enum FacialAnimation {
		/**
		 * Dialogue animations.
		 */
		HAPPY(Animation.create(588)), // - Joyful/happy
		CALM_1(Animation.create(589)), // - Speakingly calmly
		CALM_2(Animation.create(590)), // - Calm talk
		DEFAULT(Animation.create(591)), // - Default speech
		EVIL(Animation.create(592)), // - Evil
		EVIL_CONTINUED(Animation.create(593)), // - Evil continued
		DELIGHTED_EVIL(Animation.create(594)), // - Delighted evil
		ANNOYED(Animation.create(595)), // - Annoyed
		DISTRESSED(Animation.create(596)), // - Distressed
		DISTRESSED_CONTINUED(Animation.create(597)), // - Distressed continued
		ALMOST_CRYING(Animation.create(598)), // - Almost crying
		BOWS_HEAD_WHILE_SAD(Animation.create(599)), // - Bows head while sad
		DRUNK_TO_LEFT(Animation.create(600)), // - Talks and looks sleepy/drunk
		// to left
		DRUNK_TO_RIGHT(Animation.create(601)), // - Talks and looks sleepy/drunk
		// to right
		DISINTERESTED(Animation.create(602)), // - Sleepy or disinterested
		SLEEPY(Animation.create(603)), // - Tipping head as if sleepy.
		PLAIN_EVIL(Animation.create(604)), // - Plain evil
		// (Animation.create(Grits teeth and
		// moves eyebrows)
		LAUGH_1(Animation.create(605)), // - Laughing or yawning
		LAUGH_2(Animation.create(606)), // - Laughing or yawning for longer
		LAUGH_3(Animation.create(607)), // - Laughing or yawning for longer
		LAUGH_4(Animation.create(608)), // - Laughing or yawning
		EVIL_LAUGH(Animation.create(609)), // - Evil laugh then plain evil
		SAD(Animation.create(610)), // - Slightly sad
		MORE_SAD(Animation.create(611)), // - Quite sad
		ON_ONE_HAND(Animation.create(612)), // - On one hand...
		NEARLYC_RYING(Animation.create(613)), // - Close to crying
		ANGER_1(Animation.create(614)), // - Angry
		ANGER_2(Animation.create(615)), // - Angry
		ANGER_3(Animation.create(616)), // - Angry
		ANGER_4(Animation.create(617)) // - Angry
		;

		/**
		 * A map of facial animations.
		 */
		private static Map<Animation, FacialAnimation> facialAnimations = new HashMap<Animation, FacialAnimation>();

		/**
		 * Gets a facial animation by its ID.
		 * 
		 * @param facialAnimation
		 *            The facial animation item id.
		 * @return The facial animation, or <code>null</code> if the id is not a facial
		 *         animation.
		 */
		public static FacialAnimation forId(Animation facialAnimation) {
			return facialAnimations.get(facialAnimation);
		}

		/**
		 * Populates the facial animation map.
		 */
		static {
			for (FacialAnimation facialAnimation : FacialAnimation.values()) {
				facialAnimations.put(facialAnimation.animation, facialAnimation);
			}
		}

		private Animation animation;

		private FacialAnimation(Animation animation) {
			this.animation = animation;
		}

		/**
		 * @return the animation
		 */
		public Animation getAnimation() {
			return animation;
		}
	}
}