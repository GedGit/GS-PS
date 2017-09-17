package org.rs2server.rs2.model;

import org.rs2server.rs2.model.player.PrivateChat.ClanRank;

public class Alias {
    private final int uid;
    private final String name;
    private ClanRank rank;
    private int worldId;

    public Alias(int uid, String name, ClanRank rank) {
        this.uid = uid;
        this.name = name;
        this.rank = rank;
    }

    public Alias(int uid, String name, ClanRank rank, int worldId) {
        this(uid, name, rank);
        this.worldId = worldId;
    }

    public final int getUid() {
        return uid;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int id) {
        this.worldId = id;
    }

    public final ClanRank getRank() {
        return rank;
    }

    public final void setRank(ClanRank rank) {
        this.rank = rank;
    }

    public final String toString() {
        return name;
    }

}