package net.meiteampower.instagram.service.post;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.instagram.entity.ProfilePage;
import net.meiteampower.instagram.entity.Update;

/**
 * 投稿取得サービス。
 * @author kie
 */
public class PostService {

	/** ロガー */
	private static final Logger logger = LoggerFactory.getLogger(PostService.class);

	private final InstagramApi api;

	public PostService(InstagramApi api) {
		this.api = api;
	}

	public List<PostPage> get(String username, int minutes) throws Exception {
		return get(username, minutes, Instant.now());
	}

	/**
	 * 指定した分間隔の直近の投稿の一覧を取得する。
	 * 例えば、分間隔が10分で、現在が8時12分の場合、8時0分から8時10分まで
	 * （8時10分ちょうどは含まない）の投稿を取得する。
	 * @param username スクリーン名
	 * @param minutes 分間隔
	 * @param execTime 実行した日時
	 * @return
	 * @throws Exception
	 */
	public List<PostPage> get(String username, int minutes, Instant execTime) throws Exception {

		ProfilePage profilePage = api.getProfilePage(username, execTime.minusSeconds(minutes * 2));

		// 取得範囲を決める
//		Instant now = Instant.now();
		LocalDateTime t = LocalDateTime.ofInstant(execTime, ZoneId.of("Asia/Tokyo"));
		Instant from = execTime.minusSeconds(t.getHour() * 60 * 60 + t.getMinute() * 60 + t.getSecond());
		from = from.minusNanos(from.getNano());
		Instant to = from;
		for (to = from.plusSeconds(minutes * 60 * 2); to.isBefore(execTime);
				from = from.plusSeconds(minutes * 60), to = to.plusSeconds(minutes * 60));
		to = from.plusSeconds(minutes * 60);
		if (to.compareTo(execTime) >= 0) {
			from = from.minusSeconds(minutes * 60);
			to = to.minusSeconds(minutes * 60);
		}
		logger.debug("PostService.get() from=" + from.toString());
		logger.debug("PostService.get() to  =" + to.toString());

		List<PostPage> list = new ArrayList<PostPage>();
		for (Update update : profilePage.getUpdateList()) {
			Instant takenAtTimestamp = update.getTakenAtTimestamp();
			if (takenAtTimestamp.compareTo(from) >= 0 && takenAtTimestamp.compareTo(to) < 0) {
				PostPage postPage = api.getPostPage(update.getShortcode());
				list.add(postPage);
			}
		}

		// takenAtTimeの昇順に並び替える。
		list.sort(new Comparator<PostPage>() {

			@Override
			public int compare(PostPage o1, PostPage o2) {
				if (o1.getTakenAtTimestamp().isBefore(o2.getTakenAtTimestamp())) {
					return 1;
				} else if (o1.getTakenAtTimestamp().isAfter(o2.getTakenAtTimestamp())) {
					return -1;
				}
				return 0;
			}

		});

		return list;
	}
}
