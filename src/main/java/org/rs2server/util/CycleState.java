package org.rs2server.util;

/**
 * An enum holding different kinds of cycle states.
 *
 * @author Emperor
 */
public enum CycleState {

    /**
     * The commencing of the cycle.
     */
    COMMENCE,

    /**
     * The executing of the cycle.
     */
    EXECUTE,

    /**
     * The finalizing of the cycle.
     */
    FINALIZE,
    
    /**
     * The cycle has finished.
     */
    FINISHED;

}
