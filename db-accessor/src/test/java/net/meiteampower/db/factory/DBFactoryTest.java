package net.meiteampower.db.factory;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetConnectionString() {

		try (Connection connection = DBFactory.getConnection("9pdj4iJD08s")) {
			ResultSet resultSet = connection.createStatement().executeQuery("SELECT NOW()");
			resultSet.next();
			System.out.println(resultSet.getString(1));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetPassword() {

		String p = "9pdj4iJD08s";
		try {
			String password = DBFactory.getPassword("558208" + p);
			System.out.println("passowrd=" + password);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
