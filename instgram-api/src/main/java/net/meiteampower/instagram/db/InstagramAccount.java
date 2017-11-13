package net.meiteampower.instagram.db;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kie
 */
public class InstagramAccount implements Serializable {

	private String accountId;
	private String username;
	private boolean exclutionFlag;
	private Date insertTime;

	public final String getAccountId() {
		return accountId;
	}
	public final void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public final String getUsername() {
		return username;
	}
	public final void setUsername(String username) {
		this.username = username;
	}
	public final boolean isExclutionFlag() {
		return exclutionFlag;
	}
	public final void setExclutionFlag(boolean exclutionFlag) {
		this.exclutionFlag = exclutionFlag;
	}
	public final Date getInsertTime() {
		return insertTime;
	}
	public final void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
}
