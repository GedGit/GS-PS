package org.rs2server.rs2.domain.model.claim;

import org.rs2server.rs2.domain.dao.MongoEntity;

/**
 * @author tommo
 */
public class ClaimEntity extends MongoEntity {

	/**
	 * The claim type.
	 */
	private ClaimType claimType;

	/**
	 * The IP address from which this was claimed.
	 */
	private String ipAddress;

	/**
	 * The name of the player who claimed this.
	 */
	private String claimedBy;

	public ClaimType getClaimType() {
		return claimType;
	}

	public void setClaimType(ClaimType claimType) {
		this.claimType = claimType;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getClaimedBy() {
		return claimedBy;
	}

	public void setClaimedBy(String claimedBy) {
		this.claimedBy = claimedBy;
	}

}
