package net.meiteampower.blog.crawler.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.meiteampower.db.factory.DBFactory;

public class CrawlerDaoTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadUrl() {

		String path = "C:/Users/kie/git2/blog-crawler/tmp/www2.ske48.co.jp/blog/detail/id[[co]]20171202002014858.html";
		try {
			List<String> list = new ArrayList<String>();
			CrawlerDao.readUrl(path, list);
			assertTrue(list.size() > 0);
			for (String url : list) {
				System.out.println("URL=" + url);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testExists() {

		String link = "http://www2.ske48.co.jp/blog/detail/id:20171201112121178";
		try {
			boolean actual = CrawlerDao.exists(DBFactory.getConnection(), link);
			assertTrue(actual);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
