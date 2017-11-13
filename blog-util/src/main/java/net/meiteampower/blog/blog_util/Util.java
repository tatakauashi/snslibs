package net.meiteampower.blog.blog_util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class Util
{
	private static final Pattern URL_PATTERN = Pattern.compile("^(.*?)(src|href)\\=\"(.*?)\"(.*)$");
	private Util() {}

	public static final void execute(String root, String toRootDirName, FileInfo fileInfo) throws IOException {

		String path = fileInfo.getPath();

//		// バックアップをとる
//		copyHtml(path);
		// 出力先のフォルダを作成する
		String toPath = createToFolder(root, toRootDirName, path);

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(path), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(toPath), "UTF-8"))) {

			String readLine;
			boolean skip = false;
			while ((readLine = reader.readLine()) != null) {

				StringBuilder toLine = new StringBuilder();

				if (!skip) {
					// このブログを読んでいる人は<br>このメンバーのブログも読んでいます。
					if (readLine.contains("このブログを読んでいる人は<br>このメンバーのブログも読んでいます。")) {
						skip = true;
					} else {
						// URLの変換
						String line = readLine;
						Matcher m = URL_PATTERN.matcher(line);
						while (m.matches()) {
							toLine.append(m.group(1));
							toLine.append(m.group(2));
							toLine.append("=");
							toLine.append("\"");

							String url = m.group(3);
							StringBuilder toUrl = new StringBuilder();

							// "/"で終わっている→ブログ記事（id[[co]]が含まれる）でない場合は変換なし
							if (url.startsWith("/")) {
								if (url.endsWith("/") && !url.contains("id:")) {
									toUrl.append("http://www2.ske48.co.jp");
									toUrl.append(url);
								} else {
									if (url.endsWith("/") && url.contains("id:")) {
										url = url.substring(0, url.length() - 1) + ".html";
									} else if (url.startsWith("/blog/")) {
										url = url + ".html";
									}

									for (int i = 0; i < fileInfo.getDepth() - 2; i++) {
										toUrl.append("../");
									}
									toUrl.append(url.replaceAll(":", "[[co]]"));
								}
							} else {
								for (int i = 0; i < fileInfo.getDepth() - 1; i++) {
									toUrl.append("../");
								}
								toUrl.append(url.replace("http://", ""));
							}

							toLine.append(toUrl.toString().replaceAll("\\.\\.//", "../"));
							toLine.append("\"");

							line = m.group(4);
							m = URL_PATTERN.matcher(line);
						}

						toLine.append(line);

						writer.write(toLine.toString().replaceAll("</li></li>", "</li>"));
						writer.write("\n");
					}
				} else {
					// </ul> が見つかるまでスキップ
					if (readLine.contains("</ul>")) {
						skip = false;
					}
				}
			}
		}
	}

	public static final String createToFolder(String root, String toRootDirName, String path) throws IOException {

		String relativePath = path.replace(root + "\\", "");
		String toPath = root + "\\" + toRootDirName + "\\" + relativePath.substring(relativePath.indexOf('\\') + 1);
		System.out.println("toPath=" + toPath);

		int lastIndex = toPath.lastIndexOf('\\');
		String createPath = toPath.substring(0, lastIndex);
		System.out.println("createPath=" + createPath);

		if (!new File(createPath).exists()) {
			Path createPaths = java.nio.file.Paths.get(createPath);
			java.nio.file.Files.createDirectories(createPaths);
		}

		return toPath;
	}

	public static final Path copyHtml(String path) throws IOException {

		String toPath = path.replace(".html", "_bak.html");
		Path to = java.nio.file.Paths.get(toPath);
		if (new File(toPath).exists()) {
			return to;
		}

		Path from = java.nio.file.Paths.get(path);

		return java.nio.file.Files.copy(from, to);
	}

	public static final List<FileInfo> getHtmls(String root) throws IOException {

		File rootFile = new File(root);
		if (!rootFile.exists()) {
			throw new FileNotFoundException(root + " is not found.");
		} else if (!rootFile.isDirectory()) {
			throw new IOException(root + " is not a directory.");
		}

		List<FileInfo> fileInfoList = new ArrayList<FileInfo>();

		File[] files =  rootFile.listFiles();
		for (File f : files) {
			if (!f.getAbsolutePath().contains("www2.ske48.co.jp")) {
				continue;
			}
			getFiles(root, f, fileInfoList);
		}

		return fileInfoList;
	}

	private static void getFiles(String root, File file, List<FileInfo> fileInfoList) throws IOException {
		if (file.isDirectory()) {
			if (file.getName().startsWith("id[[co]]")) {
				return;
			}
			for (File f : file.listFiles()) {
				getFiles(root, f, fileInfoList);
			}
		} else if (file.getName().endsWith(".html")) {
			fileInfoList.add(new FileInfo(root, file.getAbsolutePath()));
		}
	}

	public static void main(String[] args) {
		try {
			List<FileInfo> htmls = getHtmls(args[0]);

			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream("c:/temp/blog_files.txt")))) {

				for (FileInfo fi : htmls) {
					writer.write(fi.getDepth() + " - " + fi.getPath() + "\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
