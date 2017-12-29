package net.meiteampower.instagram.main;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.meiteampower.instagram.ExecType;
import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.db.InstagramAccount;
import net.meiteampower.instagram.db.InstagramDao;
import net.meiteampower.instagram.db.InstagramLastLiked;
import net.meiteampower.instagram.entity.EdgeLikedBy;
import net.meiteampower.instagram.entity.Follows;
import net.meiteampower.instagram.entity.FreqController;
import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.instagram.entity.ProfilePage;
import net.meiteampower.instagram.entity.QueryResponse;
import net.meiteampower.instagram.entity.Update;
import net.meiteampower.util.InstagramUtils;
import net.meiteampower.util.NetUtils;
import net.meiteampower.util.Thumbnail;

/**
 * @author kie
 *
 */
public class LikeDigger {

	private static final Logger logger = LoggerFactory.getLogger(LikeDigger.class);

	private InstagramApi api = null;
	private FreqController freqCon = new FreqController();

	/**
	 * 特定のアカウントに絞って処理を行う場合のアカウント名
	 */
	@Option(name = "-u", metaVar = "username", required = false, usage = "特定のアカウントの処理を行いたい場合に指定")
	private String specUsername;

	/**
	 * 特定のshortcodeに絞って処理を行う場合のshortcode
	 */
	@Option(name = "-s", metaVar = "shortcode", required = false, usage = "特定のshortcodeの処理を行いたい場合に指定")
	private String specShortcode;

	/**
	 * 読み込み開始日。「yyyyMMdd」形式
	 */
	@Option(name = "-L", metaVar = "startDate", required = false, usage = "読み込み開始日を指定する場合のみ指定")
	private String specLowerDateTimeString;

	/**
	 * 確認終了日。「yyyyMMdd」形式
	 */
	@Option(name = "-U", metaVar = "endDate", required = false, usage = "確認終了日を指定する場合のみ指定")
	private String specUpperDateTimeString;

	public LikeDigger() {
		api = new InstagramApi();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		logger.info("[START] LIKE探索処理を開始します。");

		long startTimeMillis = System.currentTimeMillis();
		try {
			LikeDigger digger = new LikeDigger();
			CmdLineParser parser = new CmdLineParser(digger);

			parser.parseArgument(args);
			digger.execute();

		} catch (Exception e) {
			logger.warn("例外が発生しました。", e);
		} finally {
//			digger.dump();
		}

		long endTimeMillis = System.currentTimeMillis();
		long spentTimeMillis = endTimeMillis - startTimeMillis;
		long spentSec = spentTimeMillis / 1000;
		long sec = spentSec % 60;
		long spentMinutes = spentSec / 60;
		long minutes = spentMinutes % 60;
		long spentHours = spentMinutes / 60;

		logger.info("[END] LIKE探索処理を終了します。"
				+ String.format("%d 時間 %d 分 %d 秒掛かりました。",
						spentHours, minutes, sec));
	}

	public void dump() {
		freqCon.dump();
	}

	public void dumpShort() {
		freqCon.dump(300);
	}

	public void init() throws Exception {
		api = new InstagramApi();
		api.getFrontPage();
		api.login();
	}

	public void close() throws Exception {
		if (api != null) {
			api.logout();
		}
	}

	public void execute() {

		try {
			init();

			if (specShortcode != null) {
				PostPage postPage = api.getPostPage(specShortcode);
				Map<String, InstagramLastLiked> lastLikedMap = InstagramDao.getLastLiked(postPage.getId());
				InstagramLastLiked instagramLastLiked = lastLikedMap.get(specShortcode);
				// 対象アカウントの各shortcodeのチェック状況を取得する
				checkLikedUpdate(postPage.getId(), postPage.getUsername(), specShortcode,
						postPage.getTakenAtTimestamp(), instagramLastLiked);
				return;
			}

			String accountId = InstagramUtils.getSakaiMeiAccountId();

			// 登録済みのアカウントを取得する
			List<InstagramAccount> accounts = InstagramDao.getAccounts();

			if (specUsername != null) {
				ProfilePage profile = api.getProfilePage(specUsername, Instant.now());

				try {
					// アカウントごとの処理を行う
					executePerAccount(accounts, profile);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(String.format(
							"アカウント=[%s] の処理に失敗しました。", profile.getUsername()), e);
				}
			} else {

				// めいめいのフォローを確認する
				QueryResponse response = new QueryResponse();
				api.getFollowers(accountId, null, freqCon, response);
				Follows follows = Follows.build(new Gson().fromJson(response.getJson(), JsonObject.class));

				for (ProfilePage pp : follows.getProfileList()) {
					logger.debug("FOLLOW:" + pp.getUsername());
				}
	//			if (follows.getProfileList().size() > 0) return;

				// 各アカウントの処理を行う
				for (ProfilePage profile : follows.getProfileList()) {

					if (specUsername != null && !specUsername.equals(profile.getUsername())) {
						continue;
					}

					try {
						// アカウントごとの処理を行う
						executePerAccount(accounts, profile);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(String.format(
								"アカウント=[%s] の処理に失敗しました。", profile.getUsername()), e);
					}
				}
			}

		} catch (Exception e) {
			logger.error("例外が発生して終了しました。", e);
		} finally {
			try {
				if (api != null) {
					api.logout();
				}
			} catch (Exception e) {
				logger.error("ログアウトに失敗しました。", e);
			}
		}
	}

	/**
	 * アカウントごとの処理を行う。
	 * @param accounts 対象となるアカウント
	 * @param profile 処理を行うアカウントのプロフィールページ
	 * @throws Exception エラーが発生した場合
	 */
	private void executePerAccount(List<InstagramAccount> accounts, ProfilePage profile)
			throws Exception {

		// アカウントごとの対応を取得する
		ExecType type = getExecuteType(profile, accounts);
		switch (type) {
			case New:
				logger.info("ユーザー[" + profile.getUsername() + "] は新規処理を行います。");
				execNewProcedure(profile.getId(), profile.getUsername());
				logger.info("ユーザー[" + profile.getUsername() + "] の新規処理が終了しました。");
				break;
			case Continuous:
				logger.debug("ユーザー[" + profile.getUsername() + "] は継続処理を行います。");
				execContinuousProcedure(profile.getUsername());
				logger.debug("ユーザー[" + profile.getUsername() + "] の継続処理が終了しました。");
				break;
			case Skip:
				logger.debug("ユーザー[" + profile.getUsername() + "] の処理をスキップしました。");
				break;
		}
	}

	void execContinuousProcedure(String username) throws Exception {

		// 最低読み込み期間
		int lowerSpanDays = InstagramUtils.getContinuousCheckBackDays();
		// 最低読み込み日時
		Instant lowerDateTime = Instant.now().minus((long)lowerSpanDays, ChronoUnit.DAYS);
		if (specLowerDateTimeString != null) {
			lowerDateTime = InstagramUtils.yyyyMMddToInstant(specLowerDateTimeString);
		}

		// 確認終了日
		Instant upperDateTime = Instant.now();
		if (specUpperDateTimeString != null) {
			upperDateTime = InstagramUtils.yyyyMMddToInstant(specUpperDateTimeString);
		}
		upperDateTime = upperDateTime.plusSeconds(24 * 60 * 60);

		ProfilePage profilePage = api.getProfilePage(username, lowerDateTime);

		String accountId = profilePage.getId();
		// すでにlikeを確認しているshortcodeを取得する
		Set<String> likedShortcodeSet = InstagramDao.getLikedShortcodes(accountId);
		// 対象アカウントの各shortcodeのチェック状況を取得する
		Map<String, InstagramLastLiked> lastLikedMap = InstagramDao.getLastLiked(accountId);

		for (Update update : profilePage.getUpdateList()) {

			if (update.getTakenAtTimestamp().isAfter(upperDateTime)) {
				continue;
			}

			if (update.getTakenAtTimestamp().isBefore(lowerDateTime)) {
				break;
			}

			String shortcode = update.getShortcode();
			InstagramLastLiked instagramLastLiked = lastLikedMap.get(shortcode);
			if (!likedShortcodeSet.contains(shortcode)) {
				try {
					checkLikedUpdate(accountId, username, shortcode, update.getTakenAtTimestamp(), instagramLastLiked);
				} catch (Exception e) {
					logger.error("ユーザー[" + username + "] shortcode[" + shortcode + "] の確認に失敗しました。", e);
				}
			} else {
				logger.debug(String.format("ユーザー[%s] shortcode[%s] はすでに「いいね！」されています。",
						username, shortcode));
			}
		}

		// アカウント情報を更新する
		InstagramDao.updateAccount(accountId, username, profilePage.getProfilePicUrl(), profilePage.getProfilePicUrlHd());

	}

	void execNewProcedure(String accountId, String username) throws Exception {

		// 最低読み込み数
		int lowerCount = InstagramUtils.getNewInitialCheckCount();
		// 最低読み込み期間
		int lowerSpanDays = InstagramUtils.getNewInitialCheckBackDays();
		// 最低読み込み日
		Instant lowerDateTime = Instant.now().minus((long)lowerSpanDays, ChronoUnit.DAYS);
		if (specLowerDateTimeString != null) {
			lowerDateTime = InstagramUtils.yyyyMMddToInstant(specLowerDateTimeString);
		}

		ProfilePage profilePage = api.getProfilePage(username, lowerDateTime);
		if (profilePage.getUpdateList().size() < lowerCount) {
			api.getMoreProfilePages(profilePage, lowerCount - profilePage.getUpdateList().size());
		}

		// すでにlikeを確認しているshortcodeを取得する
		Set<String> likedShortcodeSet = InstagramDao.getLikedShortcodes(accountId);

		int count = 0;
		for (Update update : profilePage.getUpdateList()) {
			count++;
			if (count > lowerCount && update.getTakenAtTimestamp().isBefore(lowerDateTime)) {
				break;
			}

			String shortcode = update.getShortcode();
			if (!likedShortcodeSet.contains(shortcode)) {
				try {
					checkLikedUpdate(accountId, username, shortcode, update.getTakenAtTimestamp(), null);
				} catch (Exception e) {
					logger.error("ユーザー[" + username + "] shortcode[" + shortcode + "] の確認に失敗しました。", e);
				}
			} else {
				logger.debug(String.format("ユーザー[%s] shortcode[%s] はすでに「いいね！」されています。",
						username, shortcode));
			}
		}

		// アカウントを確認対象として登録する
		InstagramDao.insertAccount(accountId, username, profilePage.getProfilePicUrl(), profilePage.getProfilePicUrlHd());
	}

	void checkLikedUpdate(String accountId, String username, String shortcode, Instant takenAtTimestamp,
			InstagramLastLiked instagramLastLiked) throws Exception {

		logger.info(String.format("Like探索を開始します。accountId=[%s] username=[%s] shortcode=[%s] takenAtTime=[%s]",
				accountId, username, shortcode, InstagramUtils.getDateTimeString(takenAtTimestamp)));

		Date checkedDate = new Date();

		String json = "";
		boolean likedByMeimei = false;

		try {
			PostPage postPage = api.getPostPage(shortcode);
			freqCon.setFirstSize(postPage.getLikeCount());

			EdgeLikedBy edgeLikedBy = null;
			// チェックする先頭の情報
			ProfilePage firstLikedProfile = null;
			String endCursor = null;
			int count = 0;
			boolean breakDo = false;
			do {
				json = "";
				edgeLikedBy = null;

				QueryResponse response = new QueryResponse();
				boolean networkSuccess = true;
				try {
					api.getShortcodeLikeUsers(shortcode, endCursor, freqCon, response);
				} catch (Exception e) {
					logger.warn(String.format(
							"データ受信時にエラーが発生しました。shortcode=[%s]", shortcode), e);
					networkSuccess = false;
				}
				freqCon.set(response, null);

				if (!networkSuccess) {
					continue;
				}

				json = response.getJson();
				if (json != null && !json.isEmpty() && response.getStatusCode() == 200) {
					JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
					if (jsonObject != null) {
						edgeLikedBy = EdgeLikedBy.build(jsonObject);
					} else {
						edgeLikedBy = null;
					}
//				freqCon.set(response, edgeLikedBy);
				}

				// エラーの確認
				if (edgeLikedBy == null || !"ok".equals(edgeLikedBy.getStatus())) {
					logger.warn(
							String.format("likeの取得でエラーが発生しました。 Status: %d %s / shortcode=[%s]",
									response.getStatusCode(), response.getReasonPhrase(), shortcode));
					continue;
				}

				// めいめいのLIKEの確認
				if (edgeLikedBy != null) {
					endCursor = edgeLikedBy.getEndCursor();
					if (firstLikedProfile == null && edgeLikedBy.getEdges().size() > 0) {
						firstLikedProfile = edgeLikedBy.getEdges().get(0);
					}
					for (ProfilePage profile : edgeLikedBy.getEdges()) {
						if (instagramLastLiked != null && instagramLastLiked.getLikedAccountId().equals(profile.getId())) {
							// 既に確認ずみのアカウントを見つけたので、これ以降はLIKEの確認はしない。
							logger.debug("既に確認済みのアカウントを見つけたので確認を終了します。"
									+ "チェックしたアカウント数=" + count);
							breakDo = true;
							break;
						}
						count++;
						if ("sakai__mei".equals(profile.getUsername())) {
							likedByMeimei = true;
							break;
						}
					}
				}

			} while (!likedByMeimei && !breakDo && (edgeLikedBy == null || edgeLikedBy.isHasNextPage()));

			// いいね！があった場合、それを記録する。
			if (likedByMeimei && !InstagramDao.existsLiked(shortcode)) {
				InstagramDao.registerLiked(postPage.getId(), shortcode,
						InstagramUtils.getDateTimeString(postPage.getTakenAtTimestamp()));

				// 投稿の写真を保存する
				savePics(postPage);
			}

			// 今回確認したLIKEのアカウントを保存する
			if (firstLikedProfile != null) {
				if (instagramLastLiked == null) {
					// 初回の確認の場合
					InstagramDao.registerLastLiked(shortcode, accountId, firstLikedProfile.getId(), checkedDate);
					logger.debug("最終LIKEアカウントの初回登録を行いました。count=" + count);

				} else if (!firstLikedProfile.getId().equals(instagramLastLiked.getLikedAccountId())) {
					// 最終LIKEアカウントが前回と違った場合
					InstagramDao.registerLastLiked(instagramLastLiked, firstLikedProfile.getId(), checkedDate);
					logger.debug("最終LIKEアカウントの継続登録を行いました。count=" + count);
				}
			}

		} catch (Exception e) {
			dumpShort();
			logger.warn("Response JSON=" + json);
			throw e;
		}

		logger.info(String.format(
				"Like探索が終了しました。accountId=[%s] username=[%s] shortcode=[%s] さかいさんのいいね！=[%s]",
				accountId, username, shortcode, likedByMeimei ? "あり★" : "なし"));
	}

	/**
	 * 投稿された写真を保存する。
	 * @param postPage
	 * @throws Exception
	 * @throws IOException
	 */
	public static void savePics(PostPage postPage) throws Exception, IOException {

		String shortcode = postPage.getShortcode();

		int thubmnailSize = InstagramUtils.getThumbnailSize();
		// 写真をダウンロードする
		List<String> fileNameList = new ArrayList<String>();
		int picIndex = 0;
		for (String url : postPage.getDisplayUrls()) {
			int index = url.lastIndexOf(".");
			String ext = ".jpg";
			if (index >= 0 && (index + 1) < url.length()) {
				ext = url.substring(index);
			}
			String toPath = InstagramUtils.getPicDir() + "/" + postPage.getId();
			String toFileName = postPage.getUsername() + "_"
					+ InstagramUtils.getyyyyMMddHHmmssString(postPage.getTakenAtTimestamp())
					+ "_" + (++picIndex)
					+ ext;

			// 写真をダウンロードする
			NetUtils.download(url, toPath + "/" + toFileName);

			// 写真のサムネイルを作成する
			Thumbnail.scaleImage(toPath + "/" + toFileName,
					toPath + "/t_" + toFileName, InstagramUtils.getThumbnailScale(),
					thubmnailSize, thubmnailSize, null);

			fileNameList.add(toFileName);
		}

		// 写真の情報を保存する
//		InstagramDao.insertPostAddInfo(shortcode, fileNameList);
		InstagramDao.registerPostInfo(shortcode, postPage.getText(), fileNameList);
	}

	/**
	 * 指定したアカウント（プロフィール）の処理タイプを確認して返す。
	 * @param profile
	 * @param accounts
	 * @return
	 */
	private ExecType getExecuteType(ProfilePage profile, List<InstagramAccount> accounts) {

		ExecType type = ExecType.New;
		for (InstagramAccount account : accounts) {
			if (profile.getId().equals(account.getAccountId())) {
				if (account.isExclutionFlag()) {
					type = ExecType.Skip;
				} else {
					type = ExecType.Continuous;
				}
				break;
			}
		}
		return type;
	}

	public void get(String shortcode) throws Exception {

		api = new InstagramApi();
		try {
			api.getFrontPage();
			api.login();

			// ページを取得する
			PostPage postPage = api.getPostPage(shortcode);
			for (String url : postPage.getDisplayUrls()) {
				logger.debug("@@ image url=" + url);
			}

			int firstSize = 2000;
			if (postPage != null) {
				firstSize = postPage.getLikeCount();
				logger.debug("@@@ likeCount=[" + firstSize + "]");
//				return;
			}

			freqCon = new FreqController();
			freqCon.setFirstSize(firstSize);

			String endCursor = null;
			EdgeLikedBy edgeLikedBy = null;
			boolean likedByMeimei = false;
			do {
				// likeを取得する
				QueryResponse response = new QueryResponse();
				api.getShortcodeLikeUsers(shortcode, endCursor, freqCon, response);
				freqCon.set(response, null);

				String json = response.getJson();
				if (json != null && !json.isEmpty()) {
					JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
					if (jsonObject != null) {
						edgeLikedBy = EdgeLikedBy.build(jsonObject);
					} else {
						edgeLikedBy = null;
					}
//					freqCon.set(response, edgeLikedBy);
				}

//				if (response.getStatusCode() != 200) {
//
////					if (response.getStatusCode() == 429) {
////						// Rate Limit Errorの場合
////
////					} else if (response.getStatusCode() == 502) {
////						// Gateway Errorの場合
////					}
//					return;
//				}

				// エラーの確認
				if (edgeLikedBy == null || !"ok".equals(edgeLikedBy.getStatus())) {
					throw new Exception(
							String.format("likeの取得でエラーが発生しました。 Status: %d %s / shortcode=[%s]",
									response.getStatusCode(), response.getReasonPhrase(), shortcode));
				}

				// めいめいのLIKEの確認
				if (edgeLikedBy != null) {
					endCursor = edgeLikedBy.getEndCursor();
					for (ProfilePage profile : edgeLikedBy.getEdges()) {
						if ("sakai__mei".equals(profile.getUsername())) {
							likedByMeimei = true;
							break;
						}
					}
				}
//				assertEquals("ok", edgeLikedBy.getStatus());

//				break;
			} while (!likedByMeimei && (edgeLikedBy == null || edgeLikedBy.isHasNextPage()));

			logger.info("@@@@@@@@@@ めいめいのLIKE=" + likedByMeimei);

		} finally {
			if (api != null) {
				api.logout();
			}
		}
	}
}
