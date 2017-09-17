package org.rs2server.rs2.model;

import java.util.HashSet;
import java.util.Set;

public class ReferencedWorld {
    /**
     * used to tell how many people are in that world.
     */
    private final Set<Integer> players = new HashSet<Integer>();

    private final int id;
    private String address, activity;

    private int flag, syncCount;
    private boolean released = false;

    public ReferencedWorld(int id, int flag, String address, String activity, int online) {
        this.id = id;
        this.flag = flag;
        this.address = address;
        this.activity = activity;
        this.syncCount = online;
        this.released = syncCount == -1;
    }

    public final int getId() {
        return id;
    }

    public final int getFlag() {
        return flag;
    }

    public final String getAddress() { return released ? "" : address; }

    public final String getActivity() { return released ? "Offline" : activity; }

    public int getPlayers() {
        return released ? -1 : players.size();
    }

    public void insertNumber(ReferencedPerson person) {
        players.add(person.getStaticIndex());
    }

    /**
     * used only for quick world list
     * @param globalId
     */
    public void removeNumber(int globalId) {
        players.remove(globalId);
    }

    public final void update(int flag, String worldAddress, String worldActivity, int playerAmt) {
        this.flag = flag;
        this.address = worldAddress;
        this.activity = worldActivity;
        this.syncCount = playerAmt;
        this.released = syncCount == -1;
    }

    public final String toString() {
        return "[id="+id+(released ? "" :  ", online="+ players.size()+ (syncCount > 0 ? ":"+syncCount+", act="+getActivity() : ""))+"]";
    }
}
