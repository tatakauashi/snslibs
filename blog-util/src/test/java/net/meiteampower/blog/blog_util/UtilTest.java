package net.meiteampower.blog.blog_util;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Test;

public class UtilTest {

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void test() {

		try {
//			String root = "C:\\temp\\blog";
//			String root = "C:\\temp\\blog_Rion";
			String root = "C:\\temp\\blog_Risako";
//			String toRootDirName = "www2.ske48.co.jp_";
			String toRootDirName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

			List<FileInfo> list = Util.getHtmls(root);
			for (FileInfo fi : list) {
				Util.execute(root, toRootDirName, fi);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCopy() {

		try {
			FileInfo fileInfo = new FileInfo("C:\\temp\\blog",
					"C:\\temp\\blog\\www2.ske48.co.jp\\blog\\detail\\id[[co]]20110119163117092.html");
			Util.copyHtml(fileInfo.getPath());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCreateToPath() {

		try {
			//createToFolder(String root, String toRoot, String path)
			String root = "C:\\temp\\blog";
			String toRoot = "20171020194300";
			String path = "C:\\temp\\blog\\www2.ske48.co.jp\\blog\\detail\\id[[co]]20110119163117092.html";

			Util.createToFolder(root, toRoot, path);


		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testExecute() {

		try {
			String root = "C:\\temp\\blog";
			String toRoot = "20171020194301";
//			String path = "C:\\temp\\blog\\www2.ske48.co.jp\\blog\\detail\\id[[co]]20110119163117092.html";
//			String path = "C:\\temp\\blog\\www2.ske48.co.jp\\blog\\member\\page[[co]]1\\writer[[co]]sakai_mei.html";
//			String path = "C:\\temp\\blog\\www2.ske48.co.jp\\blog\\member\\writer[[co]]sakai_mei.html";
			String path = "C:\\Users\\kie\\git2\\blog-crawler\\blog_Kannon_original";
//			String path = "C:\\temp\\blog\\www2.ske48.co.jp\\blog\\detail\\id[[co]]20170331225713641.html";
//			String path = "C:\\temp\\blog\\www2.ske48.co.jp\\blog\\memberMonth\\writer[[co]]sakai_mei.html";
//			String path = "C:\\temp\\blog\\www2.ske48.co.jp\\blog\\memberMonth\\writer[[co]]sakai_mei\\year[[co]]2017.html";
			FileInfo fileInfo = new FileInfo(root, path);

			Util.execute(root, toRoot, fileInfo);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
