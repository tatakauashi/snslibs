package net.meiteampower.instagram.service.post;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.entity.PostPage;

public class PostServiceTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() {

		try {
			InstagramApi api = new InstagramApi();
			PostService service = new PostService(api);

			String username = "17kg_official";
			List<PostPage> list = service.get(username, 20);
			assertEquals(1, list.size());
			System.out.println("list.get(0).getText()=" + list.get(0).getText());

//			list = service.get(username, 10);
//			assertEquals(0, list.size());


		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
