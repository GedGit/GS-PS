package org.rs2server.rs2.domain.model.player;

import lombok.Getter;
import lombok.Setter;
import org.rs2server.rs2.domain.dao.MongoEntity;
import org.rs2server.rs2.model.Skill;

import java.util.HashMap;
import java.util.Map;

/**
 * Player skills entity.
 *
 * @author tommo
 */
public final @Setter @Getter class PlayerSkillsEntity extends MongoEntity {

	/**
	 * The players total level, e.g. the sum of all skill levels.
	 */
	@SuppressWarnings("unused")
	private int totalLevel;

	/**
	 * The players total experience, e.g. the sum of experience for all skills.
	 */
	@SuppressWarnings("unused")
	private long totalExperience;

	/**
	 * A map of {@link org.rs2server.rs2.model.Skill}s to their level.n
	 */
	private Map<Skill, Integer> levels = new HashMap<>();

	/**
	 * A map of {@link org.rs2server.rs2.model.Skill}s to their experience.
	 */
	private Map<Skill, Integer> experiences = new HashMap<>();

	public Object getLevels() {
		// TODO Auto-generated method stub
		return levels;
	}

	@SuppressWarnings("rawtypes")
	public void setLevels(HashMap hashMap) {
		// TODO Auto-generated method stub
		return;
	}

	public Object getExperiences() {
		// TODO Auto-generated method stub
		return experiences;
	}

	@SuppressWarnings("rawtypes")
	public void setExperiences(HashMap hashMap) {
		// TODO Auto-generated method stub
		return;

	}

	public void setTotalLevel(int totalLevel2) {
		// TODO Auto-generated method stub
		this.totalLevel = totalLevel2;
		return;

	}

	public void setTotalExperience(long totalExperience2) {
		// TODO Auto-generated method stub
		this.totalExperience = totalExperience2;
		return;

	}
}
