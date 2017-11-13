package net.meiteampower.instagram.entity;

import java.io.Serializable;
import java.time.Instant;

import org.apache.log4j.Logger;

import net.meiteampower.util.InstagramUtils;

/**
 * GraphQLでアクセスしたデータ量等を保持する。
 * @author kie
 */
public class FreqData implements Serializable {

	private static final Logger logger = Logger.getLogger(FreqData.class);

	/** アクセス時のfirstサイズ */
	private int firstSize;
	/** 受信したデータの数 */
	private int receivedDataCount;
	/** 受信したデータ量 */
	private long receivedDataSize;

	/** レスポンスコード */
	private int statusCode;
	/** レスポンスメッセージ */
	private String reasonPhrase;
	/** アクセスした時間 */
	private Instant accessTime;

	/**
	 * コンストラクタ。
	 * @param firstSize
	 * @param accessTime
	 * @param response
	 */
	public FreqData(int firstSize, Instant accessTime,
			int statusCode, String reasonPhrase, long contentLength, EdgeLikedBy edgeLikedBy) {

		this.firstSize = firstSize;
		this.accessTime = accessTime;

		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
		this.receivedDataSize = contentLength;

		if (edgeLikedBy != null) {
			receivedDataCount = edgeLikedBy.getEdges().size();
		}
	}

	public final int getFirstSize() {
		return firstSize;
	}
	public final void setFirstSize(int firstSize) {
		this.firstSize = firstSize;
	}
	public final int getReceivedDataCount() {
		return receivedDataCount;
	}
	public final void setReceivedDataCount(int receivedDataCount) {
		this.receivedDataCount = receivedDataCount;
	}
	public final long getReceivedDataSize() {
		return receivedDataSize;
	}
	public final void setReceivedDataSize(long receivedDataSize) {
		this.receivedDataSize = receivedDataSize;
	}
	public final int getStatusCode() {
		return statusCode;
	}
	public final void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public final String getReasonPhrase() {
		return reasonPhrase;
	}
	public final void setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
	}
	public final Instant getAccessTime() {
		return accessTime;
	}
	public final void setAccessTime(Instant accessTime) {
		this.accessTime = accessTime;
	}
	public void dump() {
		logger.debug(String.format("FreqCon::DUMP::FreqData[%s] - StatusCode=[%d] Message=[%s]"
				+ " Count=[%d] Size=[%d] ", InstagramUtils.getDateTimeString(this.getAccessTime()),
				this.getStatusCode(), this.getReasonPhrase(),
				this.getReceivedDataCount(), this.getReceivedDataSize()));

	}
}
