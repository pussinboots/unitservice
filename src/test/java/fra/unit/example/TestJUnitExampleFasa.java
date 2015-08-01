package fra.unit.example;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestJUnitExampleFasa {

	@Test
	public void testSuccess() {
		System.out.println("testSuccess run");
	}

	@Test
	public void testFail() {
		System.out.println("testFail run");
		assertEquals(1, 2);
	}

	@Test
	public void testError() {
		System.out.println("testError run");
		throw new RuntimeException("Eroro test");
	}
}