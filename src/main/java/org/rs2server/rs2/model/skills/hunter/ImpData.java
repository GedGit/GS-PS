package org.rs2server.rs2.model.skills.hunter;

public enum ImpData {
	  /**
	   * Baby Impling.
	   */
	  BABY("Baby Impling", 11238, 20, 1, 1635),
	  /**
	   * Young Impling.
	   */
	  YOUNG("Young Impling", 11240, 48, 17, 1636),
	  /**
	   * Gourmet Impling.
	   */
	  GOURMET("Gourmet Impling", 11244, 82, 34, 1637),
	  /**
	   * Earth Impling.
	   */
	  EARTH("Earth Impling", 11244, 126, 34, 1638),
	  /**
	   * Essence Impling.
	   */
	  ESSENCE("Essence Impling", 11246, 160, 40, 1639),
	  /**
	   * Electic Impling.
	   */
	  ELECTIC("Electic Impling", 11248, 205, 50, 1640),
	  /**
	   * Nature Impling.
	   */
	  NATURE("Nature Impling", 11250, 250, 58, 1641),
	  /**
	   * Magpie Impling.
	   */
	  MAGPIE("Magpie Impling", 11252, 289, 65, 1642),
	  /**
	   * Ninja Impling.
	   */
	  NINJA("Ninja Impling", 11254, 339, 74, 1643),
	  /**
	   * Dragon Impling.
	   */
	  DRAGON("Dragon Impling", 11256, 390, 83, 1644);

	  /**
	   * Variables.
	   */
	  public String name;

	  public int impJar, XPReward, levelReq, npcId;

	  /**
	   * Creating the Impling.
	   *
	   * @param name
	   * @param JarAdded
	   * @param XPAdded
	   * @param LevelNeed
	   * @param Npc
	   */
	  ImpData(String name, int JarAdded, int XPAdded, int LevelNeed, int Npc) {
	    this.name = name;
	    this.impJar = JarAdded;
	    this.XPReward = XPAdded;
	    this.levelReq = LevelNeed;
	    this.npcId = Npc;
	  }

	  public static ImpData forId(int npcId) {
	    for (ImpData imps : ImpData.values()) {
	      if (imps.npcId == npcId) {
	        return imps;
	      }
	    }
	    return null;
	  }
	}
