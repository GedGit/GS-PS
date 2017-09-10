package org.rs2server.rs2.domain.model.player;

import lombok.Getter;
import lombok.Setter;
import org.rs2server.rs2.model.Skill;

import java.util.Set;

/**
 * Player deadman mode state.
 *
 * @author tommo
 */
public final
@Setter @Getter
class PlayerDeadmanStateEntity {

	private Set<Skill> protectedCombatSkills;

	private Set<Skill> protectedOtherSkills;

	public Set<Skill> getProtectedCombatSkills() {
		return protectedCombatSkills;
	}

	public void setProtectedCombatSkills(Set<Skill> protectedCombatSkills) {
		this.protectedCombatSkills = protectedCombatSkills;
	}

	public Set<Skill> getProtectedOtherSkills() {
		return protectedOtherSkills;
	}

	public void setProtectedOtherSkills(Set<Skill> protectedOtherSkills) {
		this.protectedOtherSkills = protectedOtherSkills;
	}

}
