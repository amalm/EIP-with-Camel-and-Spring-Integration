package eis.domain.si;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import eis.domain.entities.Stock;

@ContextConfiguration(locations="classpath:META-INF/spring.integration.xml")
public class CsvImport extends AbstractTestNGSpringContextTests {
	@Autowired
	private Stock stock;
	
	@Test
	public void check() throws InterruptedException
	{
		Thread.sleep(300000);
	}

}
