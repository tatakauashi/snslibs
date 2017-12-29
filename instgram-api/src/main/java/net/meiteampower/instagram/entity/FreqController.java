package net.meiteampower.instagram.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.util.InstagramUtils;

/**
 * @author kie
 */
public class FreqController {

	private static final Logger logger = LoggerFactory.getLogger(FreqController.class);

	private static final int MAX_FIRST_SIZE = 5000;
	private static final int NORMAL_FIRST_SIZE = 2000;

	private static final long MIN_SLEEP_TIME_MILLIS = 2000;

	/** graphql/query の取得サイズ */
	private int firstSize = 2000;

	private long sleepTime = MIN_SLEEP_TIME_MILLIS;

	private List<FreqData> freqDataList;

	public FreqController() {
		freqDataList = new ArrayList<FreqData>();
	}

	public FreqController(int firstSize) {
		this();

		this.firstSize = firstSize;
	}

//	public void add(FreqData freqData) {
//		freqDataList.add(freqData);
//	}

	public final int getFirstSize() {
		return firstSize;
	}
	public final void setFirstSize(int firstSize) {
		if (firstSize > NORMAL_FIRST_SIZE) {
			this.firstSize = NORMAL_FIRST_SIZE;
		} else {
			this.firstSize = firstSize;
		}
	}

	public final long getSleepTimeMillis() {
		int dataListSize = freqDataList.size();
		if (dataListSize > 0) {
			FreqData freqData = freqDataList.get(dataListSize - 1);

			int statusCode = freqData.getStatusCode();
			if (statusCode == 502) {
				// Gateway error
				sleepTime = InstagramUtils.getError502SleepTimeMillis();
			}
			else if (statusCode == 429) {
				// Rate limiting error
				sleepTime = InstagramUtils.getError429SleepTimeMillis();
			}
			else if (statusCode != 200) {
				// other unknown error
				sleepTime = InstagramUtils.getErrorOthersSleepTimeMillis();
			} else {
				// 429 Rate limiting対策
				if (dataListSize >= 85) {
					Instant point = freqDataList.get(dataListSize - 85).getAccessTime();
					long diffSec = (Instant.now().toEpochMilli() - point.toEpochMilli()) / 1000;

					// 90アクセスを10分以内に行うと429 Rate limitingにはまる？
					if (600 - diffSec > 2) {
						sleepTime = (600 - diffSec + 1) * 1000;
					} else {
						sleepTime = MIN_SLEEP_TIME_MILLIS;
					}
				} else {
					sleepTime = MIN_SLEEP_TIME_MILLIS;
				}
			}
		} else {
			sleepTime = MIN_SLEEP_TIME_MILLIS;
		}
		return sleepTime;
	}

	public final void set(QueryResponse response, EdgeLikedBy edgeLikedBy) {
		FreqData data = new FreqData(firstSize, Instant.now(),
				response.getStatusCode(), response.getReasonPhrase(), response.getContentLength(), edgeLikedBy);
		freqDataList.add(data);
		data.dump();
	}

	public void dump() {
		dump(freqDataList.size());
	}

	public void dump(int size) {

		logger.debug("-------------------------------------------------------------------- DUMP Start");
		logger.debug(String.format("FreqCon::DUMP - firstSize=[%d] sleepTime=[%d]", firstSize, sleepTime));
		int startIndex = freqDataList.size() - size;
		startIndex = startIndex < 0 ? 0 : startIndex;
//		for (FreqData freqData : freqDataList) {
		for (int i = startIndex; i < freqDataList.size(); i++) {
			freqDataList.get(i).dump();
		}
		logger.debug("-------------------------------------------------------------------- DUMP End --");
	}
}
