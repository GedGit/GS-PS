package org.rs2server.rs2.model;

import org.rs2server.rs2.model.player.PrivateChat.ClanRank;

public interface Communicable {

    /**
     * getIndex() refers to worldId, playerinworldId
     *
     * @return
     */
    int getIndex();

    /**
     * static index refers to players globalId given on account creation
     *
     * @return
     */
    int getStaticIndex();

    /**
     * @return private messaging id, or world server status
     */
    int getStatus();

    /**
     * set privacy status, or world server status
     * @param status
     */
    void setStatus(int status);


    String getUsername();

    int getWorldId();

    boolean hasFriend(int staticIndex);

    boolean hasIgnore(int staticIndex);

	Alias toAlias(ClanRank clanRank);
}