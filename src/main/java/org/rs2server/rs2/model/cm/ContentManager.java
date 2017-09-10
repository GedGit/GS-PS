package org.rs2server.rs2.model.cm;

import org.rs2server.rs2.model.cm.impl.CerberusContent;
import org.rs2server.rs2.model.cm.impl.KrakenContent;
import org.rs2server.rs2.model.cm.impl.ZulrahContent;
import org.rs2server.rs2.model.player.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Nine
 *
 */
public class ContentManager {
	
	private Map<Integer, Class<?>> contentMap = new HashMap<Integer, Class<?>>();
	private List<Content> activeContent = new ArrayList<Content>();
	private Player player;
	
	public ContentManager(Player player) {
		this.player = player;
		
		contentMap.put(Content.ZULRAH, ZulrahContent.class);
		contentMap.put(Content.KRAKEN, KrakenContent.class);
		contentMap.put(Content.CERBERUS, CerberusContent.class);
	}
	
	public void start(int id) {
		Content content = null;
		try {
			content = (Content) contentMap.get(id).getConstructor(Player.class).newInstance(player);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		if (content == null || activeContent.contains(content))
			return;
		if (content.canStart()) {
			content.start();
			activeContent.add(content);
		} else {
			content.onCannotStart();
		}
	}


	public void stopAll() {
		activeContent.forEach(org.rs2server.rs2.model.cm.Content::stop);
	}
	
	public void stop(Content content) {
		if (content.isStopping())
			return;
		content.stop();
		content.setStopping(true);
	}

	public void process() {
		for (Iterator<Content> activeContent = this.activeContent.iterator(); activeContent.hasNext();) {
			Content content = activeContent.next();
			if (content == null || content.isStopping()) {
				activeContent.remove();
				continue;
			}
			content.process();
		}
	}

	public void onDeath() {
		for (int i = 0, n = activeContent.size(); i < n; i++)
			activeContent.get(i).onDeath();
	}

	public Content getActiveContent(int id) {
		Class<?> contentClass = contentMap.get(id);
		for (int i = 0, n = activeContent.size(); i < n; i++) {
			Content content = activeContent.get(i);
			if (content.getClass() == contentClass)
				return content;
		}
		return null;
	}
}
