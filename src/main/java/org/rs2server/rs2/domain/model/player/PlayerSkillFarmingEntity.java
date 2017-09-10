package org.rs2server.rs2.domain.model.player;

import lombok.Getter;
import lombok.Setter;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatch;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchState;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingTool;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains farming state.
 *
 * @author tommo
 */
public final
@Setter @Getter
class PlayerSkillFarmingEntity {

	private Map<FarmingPatch, FarmingPatchState> patches = new HashMap<>();

	private Map<FarmingTool, Integer> toolStore = new HashMap<>();

	public Map<FarmingTool, Integer> getToolStore() {
		return toolStore;
	}

	public void setToolStore(Map<FarmingTool, Integer> toolStore) {
		this.toolStore = toolStore;
	}

	public Map<FarmingPatch, FarmingPatchState> getPatches() {
		return patches;
	}

	public void setPatches(Map<FarmingPatch, FarmingPatchState> patches) {
		this.patches = patches;
	}
}
