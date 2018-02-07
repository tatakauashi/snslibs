package net.meiteampower.blog.crawler.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.net.UrlAnalyzer;
import net.meiteampower.net.UrlInfo;
import net.meiteampower.util.MPUtils;

/**
 * Crawlerテーブルを操作する。
 *
 * @author kie
 */
public class CrawlerDao {

	private static final Logger logger = LoggerFactory.getLogger(CrawlerDao.class);

    private static final Pattern UrlRegexForHtml = Pattern.compile("<\\w+[^>]*?(href|src)\\=\"(.*?)\".*?>(.*?)$");
    private static final Pattern UrlRegexForCss = Pattern.compile(".*?(url)\\((.*?)\\)(.*?)$");

    /**
	 * ステータスを調査中に変更する。
	 *
	 * @param connection
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public static int changeStatusToGetting(Connection connection, int id) throws SQLException {

		String sql = "UPDATE crawl_url SET status = 1, start_time = NOW() WHERE id = ? AND status = 0 ";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setInt(1, id);

		return ps.executeUpdate();
	}

	/**
	 * ステータスを成功に変更する。
	 *
	 * @param connection
	 * @param id
	 * @param filePath
	 * @param contentType
	 * @return
	 * @throws SQLException
	 */
	public static int changeStatusToCompleted(Connection connection, int id, String filePath, String contentType)
			throws SQLException {

		String sql = "UPDATE crawl_url SET status = 2, file_path = ?, end_time = NOW(), content_type = ? "
				+ " WHERE id = ? AND status = 1 ";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, filePath);
		ps.setString(2, contentType);
		ps.setInt(3, id);

		return ps.executeUpdate();
	}

	/**
	 * ステータスを失敗に変更する。
	 *
	 * @param connection
	 * @param id
	 * @param error
	 * @return
	 * @throws SQLException
	 */
	public static int changeStatusToFailed(Connection connection, int id, String error)
			throws SQLException {

		String sql = "UPDATE crawl_url SET status = -1, description = ?, end_time = NOW() "
				+ " WHERE id = ? AND status = 1 ";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, error);
		ps.setInt(2, id);

		return ps.executeUpdate();
	}

	/**
	 * ステータスをスキップに変更する。
	 *
	 * @param connection
	 * @param id
	 * @param error
	 * @return
	 * @throws SQLException
	 */
	public static int changeStatusToSkipped(Connection connection, int id,
			String filePath, String contentType, String error)
			throws SQLException {

		String sql = "UPDATE crawl_url SET status = 3, file_path = ?, description = ?, end_time = NOW(), content_type = ? "
				+ " WHERE id = ? AND status = 1 ";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, filePath);
		ps.setString(2, error);
		ps.setString(3, contentType);
		ps.setInt(4, id);

		return ps.executeUpdate();
	}

	/**
	 * ステータスをワーニングに変更する。
	 *
	 * @param connection
	 * @param id
	 * @param error
	 * @return
	 * @throws SQLException
	 */
	public static int changeStatusToWarning(Connection connection, int id,
			String contentType, String error)
			throws SQLException {

		String sql = "UPDATE crawl_url SET status = 4, description = ?, end_time = NOW(), content_type = ? "
				+ " WHERE id = ? AND status = 1 ";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, error);
		ps.setString(2, contentType);
		ps.setInt(3, id);

		return ps.executeUpdate();
	}

	/**
	 * ステータスをスキップから完了に変更する。
	 *
	 * @param connection
	 * @param id
	 * @param error
	 * @return
	 * @throws SQLException
	 */
	public static int changeStatusFromSkippedToCompleted(Connection connection, int id, String error)
			throws SQLException {

		String sql = "UPDATE crawl_url SET status = 2, description = ?, end_time = NOW() "
				+ " WHERE id = ? AND status = 3 ";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, error);
		ps.setInt(2, id);

		return ps.executeUpdate();
	}

	/**
	 * 指定したリンクが既に登録されているか確認する。
	 *
	 * @param connection
	 * @param id
	 * @param error
	 * @return
	 * @throws SQLException
	 */
	public static boolean exists(Connection connection, String link)
			throws SQLException {

		String sql = "SELECT link FROM crawl_url WHERE link = ? ";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, link);

		boolean result = false;
		try (ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * URLを追加する。
	 *
	 * @param connection
	 * @param parentId
	 * @param href
	 * @param link
	 * @param fullUrl
	 * @throws SQLException
	 */
	public static void add(Connection connection, int parentId, String href, String link, String fullUrl)
			throws SQLException {

		String sql = "INSERT INTO crawl_url (parent_id, href, link, full_url, regist_time) "
				+ " VALUES (?, ?, ?, ?, NOW())";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, String.valueOf(parentId));
		ps.setString(2, href);
		ps.setString(3, link);
		ps.setString(4, fullUrl);

		ps.executeUpdate();
	}

	public static CrawlUrlEntity getNext(Connection connection) throws SQLException {

		String sql = "SELECT id, full_url FROM crawl_url WHERE status = 0 ORDER BY regist_time LIMIT 1";
		try (ResultSet rs = connection.createStatement().executeQuery(sql)) {
			if (rs.next()) {
				int id = rs.getInt(1);
				String fullUrl = rs.getString(2);
				if (id > 0 && fullUrl != null && !fullUrl.isEmpty()) {
					return new CrawlUrlEntity(id, fullUrl);
				}
			}
		}

		return null;
	}

	/**
	 * URLを解析し、保存先のディレクトリとファイル名を取得する。
	 * @param url
	 * @return
	 */
	public static UrlAnalysisResult analyzeUrl(String url) {

		UrlAnalysisResult result = null;
		UrlInfo urlInfo = null;
		if ((urlInfo = UrlAnalyzer.analyze(url)) != null) {
			String detailPath = urlInfo.getHostPort();
			String fileName;

			String path = urlInfo.getPath();
			if (path.endsWith("/")) {
				detailPath += path;
				fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			} else {
				// パスの末尾の文字列をファイル名扱いする
				int lastSlashIndex = path.lastIndexOf("/");
				detailPath += path.substring(0, lastSlashIndex + 1);
				fileName = path.substring(lastSlashIndex + 1);
			}

			// パスの文字列を安全化する
			detailPath = MPUtils.replaceWindowsSafetyChar(detailPath);

			// ファイル名にクエリ文字列とフラグメントを加味し、全体を安全化する
			String query = urlInfo.getQuery();
			if (query != null && query.length() > 0) {
				fileName += "?" + query;
			}
			String fragment = urlInfo.getFragment();
			if (fragment != null && fragment.length() > 0) {
				fileName += "#" + fragment;
			}
			fileName = MPUtils.replaceWindowsSafetyChar(fileName);

			result = new UrlAnalysisResult(detailPath, fileName);
		}

		return result;
	}

	/**
	 * コンテントタイプから拡張子を取得する。
	 * @param contentType
	 * @return
	 */
	public static String getExtension(String contentType) {

        String wRetVal = ".bin";

        if (contentType != null)
        {
            if (contentType.toLowerCase().startsWith("text/html")) {
                wRetVal = ".html";
            } else if (contentType.toLowerCase().startsWith("text/plain")) {
                wRetVal = ".txt";
            }
        }
        return wRetVal;
	}

	/**
	 * ファイルの中身を確認し、次を返す。
	 * <dd>1: 探しているメンバーのブログであり、その内容からリンクをたどってアクセスする。
	 * <dd>2: セッションが切れたため、ブログの内容が見られず不完全である。
	 * <dd>3: CSSファイルであり、リンクのたどり方が異なる。
	 * @param filePath
	 * @param titleIncludesString
	 * @param contentsIncludesString
	 * @return
	 * @throws IOException
	 */
	public static int checkBlogFile(String filePath, String titleIncludesString, String contentsIncludesString) throws IOException {

		if (filePath.endsWith(".css")) {
			return 3;
		}

        int retVal = 1;
        boolean isOk = false;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), Charset.forName("UTF-8"))) {
        	String line = null;
        	while ((line = reader.readLine()) != null) {
        		String lowerLine = line.toLowerCase();
        		if (!isOk && lowerLine.indexOf("<title>") >= 0) {
        			if (line.indexOf(titleIncludesString) < 0) {
            			// 探しているメンバーのブログではないことを確認
        				retVal = 1;
        				break;
        			} else {
            			// 探しているメンバーのブログであることを確認
        				retVal = 0;
        				isOk = true;
        			}
        		}
        		if (!isOk && line.indexOf(contentsIncludesString) >= 0) {
        			// 探しているメンバーのブログであることを確認
        			retVal = 0;
        			isOk = true;
        		}
        		if (lowerLine.indexOf("続きを見る</span></a>") >= 0) {
        			// セッションが切れているためブログが見られない。
        			retVal = 2;
        			break;
        		}
        	}
        }

//        if (retVal == 0 && !isOk) {
//        	// 未確認→対象として返す。
//        	retVal = 1;
//        }

        return retVal;
	}

	public static void readUrl(String path, List<String> urlList) throws IOException {

		// URLを抽出する正規表現
		Pattern pattern = UrlRegexForHtml;
		if (path.endsWith(".css")) {
			pattern = UrlRegexForCss;
		}

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), Charset.forName("UTF-8"))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				readUrlFromLine(line, urlList, pattern);
			}
		}
	}

	private static void readUrlFromLine(String line, List<String> urlList, Pattern pattern) {

		String wLine = line;
		while (wLine.length() > 0) {
			Matcher matcher = pattern.matcher(wLine);
			wLine = "";
			if (matcher.find()) {
				String url = matcher.group(2);
				wLine = matcher.group(3);

				if (url.startsWith("#")) {
					continue;
				} else if (url.startsWith("\"") && url.endsWith("\"")
					|| url.startsWith("'") && url.endsWith("'")) {
					url = url.substring(1, url.length() - 1);
				}

				urlList.add(url);
			}
		}

	}

	public static String getFullUrl(String parentUrl, String nextUrl) {

		String retVal = "";

		UrlInfo parentUrlInfo = UrlAnalyzer.analyze(parentUrl);
		UrlInfo nextUrlInfo = UrlAnalyzer.analyze(nextUrl);
		if (parentUrlInfo != null && nextUrlInfo != null) {
			String nextScheme = nextUrlInfo.getScheme();
			if (nextScheme == null || nextScheme.isEmpty()) {
				nextScheme = parentUrlInfo.getScheme();
			}
			String nextHostPort = nextUrlInfo.getHostPort();
			if (nextHostPort == null || nextHostPort.isEmpty()) {
				nextHostPort = parentUrlInfo.getHostPort();
			}

			String nextPath = nextUrlInfo.getPath();
            if (!nextPath.startsWith("/"))
            {
    			String parentPath = parentUrlInfo.getPath();
                if (!parentPath.endsWith("/"))
                {
                    int index = parentPath.lastIndexOf("/");
                    parentPath = parentPath.substring(0, index + 1);
                }
                nextPath = parentPath + nextPath;

                nextPath = removeDotsAndSlashes(nextPath);
            }

            retVal = nextScheme + "://" + nextHostPort + nextPath;
			String nextQuery = nextUrlInfo.getQuery();
            if (nextQuery != null && nextQuery.length() > 0)
            {
            	retVal += "?" + nextQuery;
            }
			String nextFragment = nextUrlInfo.getFragment();
            if (nextFragment != null && nextFragment.length() > 0)
            {
            	retVal += "#" + nextFragment;
            }
	}

		return retVal;
	}

	private static String removeDotsAndSlashes(String path) {

        String wPath = path;
        while (wPath.indexOf("/./") >= 0)
        {
            wPath = wPath.replaceAll("/./", "/");
        }

        Pattern wHopExp = Pattern.compile("^(.*?)/[^/]+/\\.\\./(.*?)$");
        Matcher wMatch;
        while ((wMatch = wHopExp.matcher(wPath)).matches()) {
            wPath = wMatch.group(1) + "/" + wMatch.group(2);
        }
        while (wPath.startsWith("/../"))
        {
            wPath = wPath.substring(3);
        }
        return wPath;
	}


}
