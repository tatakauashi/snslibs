package net.meiteampower.instagram.entity;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FreqControllerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		FreqController freqCon = new FreqController();

		QueryResponse response = new QueryResponse();
		response.setStatusCode(200);
		response.setReasonPhrase("OK");
		response.setContentLength(0);
		freqCon.set(response, null);

		response = new QueryResponse();
		response.setStatusCode(200);
		response.setReasonPhrase("OK");
		response.setContentLength(0);
		freqCon.set(response, null);

		response = new QueryResponse();
		response.setStatusCode(429);
		response.setReasonPhrase("-");
		response.setContentLength(0);
		freqCon.set(response, null);

		assertEquals(300000, freqCon.getSleepTimeMillis());

		response = new QueryResponse();
		response.setStatusCode(200);
		response.setReasonPhrase("OK");
		response.setContentLength(0);
		freqCon.set(response, null);
		assertEquals(2000, freqCon.getSleepTimeMillis());
	}

}
