package org.rs2server.rs2.model.req;

import org.rs2server.rs2.model.player.Player;

/**
 * A request listener is a class which listens to various types of requests in
 * the request system.
 * @author Graham Edgecombe
 *
 */
public interface RequestListener {
	
	/**
	 * Called when a request is accepted.
	 * @param player The player.
	 * @param partner The player's partner.
	 */
	public void requestAccepted(Player player, Player partner);
	
	/**
	 * Called when one of the players cancels the request.
	 * @param player The player.
	 * @param partner The player's partner.
	 */
	public void requestCancelled(Player player, Player partner);
	
	/**
	 * Called when one of the players finishes the request.
	 * @param player The player.
	 * @param partner The player's partner.
	 */
	public void requestFinished(Player player, Player partner);

}
