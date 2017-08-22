package net.meiteampower.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReshapeJsonTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecuteFromFile() {

		try {
			new ReshapeJson().executeFromFile("samples/json.txt");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
