package org.rs2server.rs2.domain.service.impl.content;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.rs2server.rs2.domain.service.api.content.RegionMusicService;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Tim on 12/1/2015.
 */
public class RegionMusicServiceImpl implements RegionMusicService {

	private static Music[] music;

	private static final Logger logger = Logger.getLogger(RegionMusicServiceImpl.class.getName());

	/**
	 * The auto-play flag.
	 */
	private boolean auto = true;

	/**
	 * The manual-play flag.
	 */
	@SuppressWarnings("unused")
	private boolean man;

	/**
	 * The looping flag.
	 */
	@SuppressWarnings("unused")
	private boolean loop;

	private List<Integer> unlockedMusics = new LinkedList<Integer>();

	public static void load() throws Exception {
		logger.info("Loading music...");

		JsonParser parser = new JsonParser();
		JsonArray array = (JsonArray) parser.parse(new FileReader(new File("./data/music.json")));
		music = new Music[array.size()];
		int count = 0;

		for (int i = 0; i < array.size(); i++) {
			JsonObject reader = (JsonObject) array.get(i);

			int region = -1;
			if (reader.has("region"))
				region = reader.get("region").getAsInt();
			String name = reader.get("name").getAsString();
			int song = reader.get("song").getAsInt();
			int frame = reader.get("frame").getAsInt();
			int button = reader.get("button").getAsInt();

			music[i] = new Music(region, name, song, frame, button);
			count++;
		}

		logger.info("Loaded " + count + " music!");
	}

	@Override
	public void updateRegionMusic(@Nonnull Player player, int regionId) {
		Music music = forRegion(regionId);
		if (!auto)
			return;
		if (music != null) {
			player.getActionSender().sendSong(music.getSong());
			if (!unlockedMusics.contains(music.getSong())) {
				unlockedMusics.add(music.getSong());
				// TODO config for green text
				player.sendMessage("You've unlocked the music track: " + music.getName() + ".");
			}
		}
	}

	@Override
	public void updateTrackList(@Nonnull Player player) {

	}

	public static Music[] getMusic() {
		return music;
	}

	public static Music forRegion(int region) {
		for (Music m : music) {
			if (m == null)
				continue;
			if (m.region == region)
				return m;
		}
		return null;
	}

	public static Music forFrame(int button) {
		for (Music m : music) {
			if (m == null)
				continue;
			if (m.button == button)
				return m;
		}
		return null;
	}

	public static Music forSong(int song) {
		for (Music m : music) {
			if (m == null)
				continue;
			if (m.song == song)
				return m;
		}
		return null;
	}

	public static class Music {

		public Music(int region, String name, int song, int frame, int button) {
			this.region = region;
			this.name = name;
			this.song = song;
			this.frame = frame;
			this.button = button;
		}

		private final int region;

		private final String name;

		private final int song;

		private final int frame;

		private final int button;

		public String getName() {
			return name;
		}

		public int getSong() {
			return song;
		}

		public int getFrame() {
			return frame;
		}

		public int getButton() {
			return button;
		}

	}
}
