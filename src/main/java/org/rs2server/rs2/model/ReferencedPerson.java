package org.rs2server.rs2.model;


import java.util.HashSet;
import java.util.Set;

import org.rs2server.rs2.model.player.PrivateChat.ClanRank;
import org.rs2server.rs2.model.region.Region;

public class ReferencedPerson extends Entity implements Communicable {

    private int privacy;
    private String username;
    private Set<Integer> friendList = new HashSet<Integer>();
    private Set<Integer> ignoreList = new HashSet<Integer>();

    public ReferencedPerson(int world, int staticIndex, int privacy) {
        setIndex(world);
        setStaticIndex(staticIndex);
        this.privacy = privacy;
    }

    @Override
    public int getStatus() {
        return privacy;
    }

    @Override
    public void setStatus(int status) {
        this.privacy = status;
    }

    public final void setUsername(String username) {
        this.username = username;
    }

    public final String getUsername() {
        return username;
    }

    @Override
    public int getWorldId() {
        return getIndex();
    }

    public void addFriend(int friend) {
        friendList.add(friend);
    }

    public void removeFriend(int friend) {
        friendList.remove(friend);
    }

    @Override
    public boolean hasFriend(int staticIndex) {
        return friendList.contains(staticIndex);
    }

    @Override
    public boolean hasIgnore(int staticIndex) {
        return ignoreList.contains(staticIndex);
    }

    @Override
    public Alias toAlias(ClanRank one) {
        return new Alias(getStaticIndex(), username, one, getWorldId());
    }

    public void addIgnore(int friend) {
        friendList.add(friend);
    }

    public void removeIgnore(int friend) {
        friendList.remove(friend);
    }


	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Location getCentreLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPlayer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNPC() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isObject() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getClientIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeFromRegion(Region region) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addToRegion(Region region) {
		// TODO Auto-generated method stub
		
	}
}
