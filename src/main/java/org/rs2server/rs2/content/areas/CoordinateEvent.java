package org.rs2server.rs2.content.areas;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;

public abstract class CoordinateEvent {

    private Mob mob;
    private int x, y, sizeX, sizeY;

    public CoordinateEvent(Mob mob, int x, int y, int lengthX, int lengthY) {
       this(mob, x, y, lengthX, lengthY, -1);
    }
    
    public CoordinateEvent(Mob mob, int x, int y, int size) {
      this(mob, x, y, size, size);
    }
    
    
    public CoordinateEvent(Mob mob, int x, int y, int lengthX, int lengthY, int rotation) {
        this.mob = mob;
        this.x = x;
        this.y = y;
        if (rotation != 1 && rotation != 3) {
        	this.sizeX = lengthX;
        	this.sizeY = lengthY;
        } else {
        	this.sizeX = lengthY;
        	this.sizeY = lengthX;
        }
    }

    public boolean inArea() {
		return mob.getWalkingQueue().isEmpty() && mob.getLocation().isWithinDistance(Location.create(x, y), getLarger());
    }
    
    public int getLarger() {
    	if (sizeY > sizeX) {
    		return sizeY;
    	}
    	return sizeX;
    }
    
    public abstract void execute();

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

