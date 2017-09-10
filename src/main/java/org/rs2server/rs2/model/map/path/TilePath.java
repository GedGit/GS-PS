package org.rs2server.rs2.model.map.path;

import org.rs2server.rs2.model.map.BasicPoint;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author 'Mystic Flow <Steven@rune-server.org>
 */
public class TilePath {

    private Deque<BasicPoint> points = new ArrayDeque<BasicPoint>();
    private int state = 0;

    public Deque<BasicPoint> getPoints() {
        return points;
    }

    public void routeFailed() {
        this.state = 1;
    }
    
    public void routeIncomplete() {
    	this.state = 2;
    }

    public boolean isRouteFound() {
        return state != 1;
    }
    
    public boolean isRouteIncomplete() {
    	return state == 2;
    }

}
