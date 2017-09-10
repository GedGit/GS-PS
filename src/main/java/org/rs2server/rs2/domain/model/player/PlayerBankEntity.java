package org.rs2server.rs2.domain.model.player;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * @author Twelve
 */
public final @Setter @Getter class PlayerBankEntity {

	private int recoveryDelay = 7;
	private DateTime pinRequestTime;
	private boolean deleted;
	private int[] requestedPin;

	public int getRecoveryDelay() {
		return recoveryDelay;
	}

	public void setRecoveryDelay(int recoveryDelay) {
		this.recoveryDelay = recoveryDelay;
	}

	public DateTime getPinRequestTime() {
		return pinRequestTime;
	}

	public void setPinRequestTime(DateTime pinRequestTime) {
		this.pinRequestTime = pinRequestTime;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int[] getRequestedPin() {
		return requestedPin;
	}

	public void setRequestedPin(int[] requestedPin) {
		this.requestedPin = requestedPin;
	}

}
