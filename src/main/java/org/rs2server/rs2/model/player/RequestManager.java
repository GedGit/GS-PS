package org.rs2server.rs2.model.player;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.req.DuelRequestListener;
import org.rs2server.rs2.model.req.RequestListener;
import org.rs2server.rs2.model.req.TradeRequestListener;

/**
 * The request manager manages
 * 
 * @author Graham Edgecombe
 *
 */
public class RequestManager {

	/**
	 * Represents the different types of request.
	 * 
	 * @author Graham Edgecombe
	 *
	 */
	public enum RequestType {

		/**
		 * A trade request.
		 */
		TRADE("tradereq", new TradeRequestListener()), DUEL("duelreq",
				new DuelRequestListener())/* , DICE("dicereq", new DiceRequestListener()) */;

		/**
		 * The client-side name of the request.
		 */
		private String clientName;

		/**
		 * The request listener for this request.
		 */
		private RequestListener listener;

		/**
		 * Creates a type of request.
		 * 
		 * @param clientName
		 *            The name of the request client-side.
		 * @param listener
		 *            The request listener.
		 */
		private RequestType(String clientName, RequestListener listener) {
			this.clientName = clientName;
			this.listener = listener;
		}

		/**
		 * Gets the client name.
		 * 
		 * @return The client name.
		 */
		public String getClientName() {
			return clientName;
		}

		/**
		 * Gets the request listener.
		 * 
		 * @return The request listener.
		 */
		public RequestListener getRequestListener() {
			return listener;
		}

	}

	/**
	 * Holds the different states the manager can be in.
	 * 
	 * @author Graham Edgecombe
	 *
	 */
	public enum RequestState {

		/**
		 * Not currently in a request.
		 */
		NORMAL,

		/**
		 * The player is participating in an existing request.
		 */
		PARTICIPATING,

		/**
		 * The player has confirmed on the first screen.
		 */
		CONFIRM_1,

		/**
		 * The player has confirmed on the second screen.
		 */
		CONFIRM_2,

		/**
		 * The the request is in a 'commencing' state - dueling only.
		 */
		COMMENCING,

		/**
		 * The the request is active - dueling only.
		 */
		ACTIVE,

		/**
		 * The request has finished - dueling only.
		 */
		FINISHED;

	}

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * The current state.
	 */
	private RequestState state = RequestState.NORMAL;

	/**
	 * Gets the current state.
	 * 
	 * @return The current state.
	 */
	public RequestState getState() {
		return state;
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            The state to set.
	 */
	public void setState(RequestState state) {
		this.state = state;
	}

	/**
	 * The current request type.
	 */
	private RequestType requestType;

	/**
	 * @param requestType
	 *            the requestType to set
	 */
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	/**
	 * Gets the current request type.
	 * 
	 * @return The current request type.
	 */
	public RequestType getRequestType() {
		return requestType;
	}

	/**
	 * The current 'acquaintance'.
	 */
	private Player acquaintance;

	/**
	 * Sets the acquaintance.
	 * 
	 * @param acquaintance
	 *            The acquaintance to set.
	 */
	public void setAcquaintance(Player acquaintance) {
		this.acquaintance = acquaintance;
	}

	/**
	 * The current 'acquaintance'.
	 * 
	 * @return the acquaintance
	 */
	public Player getAcquaintance() {
		return acquaintance;
	}

	private final PermissionService permissionService;

	/**
	 * Creates the request manager.
	 * 
	 * @param player
	 *            The player whose requests the manager is managing.
	 */
	public RequestManager(Player player) {
		this.player = player;
		this.permissionService = Server.getInjector().getInstance(PermissionService.class);
	}

	/**
	 * Requests a type of request.
	 * 
	 * @param type
	 *            The type of request.
	 * @param acquaintance
	 *            The other player.
	 */
	public void request(RequestType type, Player acquaintance) {
		if (acquaintance == null)
			return;

		if (permissionService.isAny(player, PermissionService.PlayerPermissions.IRON_MAN,
				PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN,
				PermissionService.PlayerPermissions.HARDCORE_IRON_MAN)
				&& !permissionService.is(acquaintance, PermissionService.PlayerPermissions.DEV)) {
			player.getActionSender().sendMessage("You are an iron man and cannot trade.");
			cancelRequest();
			return;
		}

		if (permissionService.isAny(acquaintance, PermissionService.PlayerPermissions.IRON_MAN,
				PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN,
				PermissionService.PlayerPermissions.HARDCORE_IRON_MAN)
				&& !permissionService.is(player, PermissionService.PlayerPermissions.DEV)) {
			player.getActionSender().sendMessage("That player is an iron man and cannot trade.");
			cancelRequest();
			return;
		}
		if (acquaintance.getInterfaceState().getCurrentInterface() > -1
				&& !acquaintance.getInterfaceState().isWalkableInterface()) {
			this.player.getActionSender().sendMessage("Other player is busy at the moment.");
			cancelRequest();
			return;
		}
		this.player.getActionQueue().clearRemovableActions();
		this.player.setInteractingEntity(InteractionMode.REQUEST, acquaintance);
		this.requestType = type;
		this.acquaintance = acquaintance;
		if (acquaintance.getRequestManager().acquaintance == player
				&& acquaintance.getRequestManager().requestType == type) {
			this.state = RequestState.PARTICIPATING;
			this.acquaintance.getRequestManager().state = RequestState.PARTICIPATING;
			this.requestType.getRequestListener().requestAccepted(player, acquaintance);
		} else if (this.state != RequestState.PARTICIPATING) {
			if (type == RequestType.TRADE) {
				this.player.getActionSender()
						.sendMessage("Sending " + type.getClientName().replace("req", "") + " offer...");
				this.acquaintance.getActionSender().sendTradeRequest(player.getName() + " wishes to trade with you.",
						player.getName());
			} else if (type == RequestType.DUEL) {
				this.player.getActionSender()
						.sendMessage("Sending " + type.getClientName().replace("req", "") + " request...");
				this.acquaintance.getActionSender().sendDuelRequest(player.getName() + " wishes to duel with you.",
						player.getName());
			}
		}
	}

	/**
	 * Cancels a request.
	 */
	public void cancelRequest() {
		if (state != RequestState.NORMAL) {
			state = RequestState.NORMAL;
			if (acquaintance.getRequestManager().acquaintance == player
					&& acquaintance.getRequestManager().requestType == requestType) {
				acquaintance.getRequestManager().requestType = null;
				acquaintance.getRequestManager().setAcquaintance(null);
				acquaintance.getRequestManager().setState(RequestState.NORMAL);
			}
			requestType.getRequestListener().requestCancelled(player, acquaintance);
			requestType = null;
			acquaintance = null;
		}
	}

	/**
	 * Finishes a request.
	 */
	public void finishRequest() {
		if (state != RequestState.FINISHED) {
			state = RequestState.FINISHED;

			if (acquaintance != null) {
				if (acquaintance.getRequestManager().acquaintance == player
						&& acquaintance.getRequestManager().requestType == requestType) {
					acquaintance.getRequestManager().setState(RequestState.NORMAL);
					acquaintance.getRequestManager().setAcquaintance(null);
					acquaintance.getRequestManager().setState(RequestState.NORMAL);
				}
				requestType.getRequestListener().requestFinished(player, acquaintance);
			} else {
				if (Constants.DEBUG)
					System.out.println("Request acquaintance is null");
			}
		}
	}
}