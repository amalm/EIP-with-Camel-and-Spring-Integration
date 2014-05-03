package eip.spring.integration;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import eip.common.entities.Order;
import eip.common.services.OrderService;
import eip.common.testutil.CountDownLatchAnswer;

/**
 * Integration testing order reception by mocking the services.
 * 
 * @author Anders Malmborg
 * 
 */
@ContextConfiguration(locations={"classpath:META-INF/order.spring.xml",
								 "classpath:order.spring.test.xml"})
public class OrderCsvImportTest extends AbstractTestNGSpringContextTests {
	
	private Path src, destDir, destFile;
	private FileSystem fileSystem;

	@Autowired
	private OrderService orderService;
	
	@BeforeMethod
	public void beforeMethod() throws IOException
	{
		fileSystem = FileSystems.getDefault();
		src = fileSystem.getPath("../eip-common/src/main/resources/orders/order1.csv");
		destDir = fileSystem.getPath("target", "orders");
		destFile = destDir.resolve(src.getFileName());
		if (!Files.isDirectory(destDir))
			Files.createDirectory(destDir);
		
	}
	@AfterClass(alwaysRun=true)
	public void afterMethod() throws IOException
	{
		if (Files.exists(destFile))
			Files.delete(destFile);		
	}

	@Test
	public void check() throws InterruptedException, IOException
	{
		// To train the objects before the processing starts, copy the file
		// first after training
		destFile = Files.copy(src, destFile, StandardCopyOption.REPLACE_EXISTING);
		CountDownLatch latch = new CountDownLatch(2);
		doAnswer(new CountDownLatchAnswer().countsDownLatch(latch)).when(orderService)
				.handleOrder(Mockito.any(Order.class));
		assertTrue(latch.await(3, TimeUnit.SECONDS), "latch interupted");
		verify(orderService, Mockito.times(2)).handleOrder(Mockito.any(Order.class));
	}
}
