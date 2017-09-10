package org.rs2server.rs2;
 
public class WorldDef {
 
    public int id;
    public int mask;
    public String ip;
   
    public WorldDef(int id, int mask, String ip) {
        this.id = id;
        this.mask = mask;
        this.ip = ip;
        //how does the world description work?
        // sec
    }
}