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

import eip.common.services.Backlog;
import eip.common.services.BacklogService;
import eip.common.testutil.CountDownLatchAnswer;

/**
 * Integration testing order reception by mocking the services.
 * 
 * @author Anders Malmborg
 * 
 */
@ContextConfiguration(locations = {
		"classpath:META-INF/services.memory.spring.xml",
		"classpath:META-INF/springintegration.spring.xml",
		"classpath:order.spring.test.xml" })
public class OrderCsvImportTest extends AbstractTestNGSpringContextTests {

	private Path src, destDir, destFile;
	private FileSystem fileSystem;

	@Autowired
	private BacklogService backlogService;

	@BeforeMethod
	public void beforeMethod() throws IOException {
		fileSystem = FileSystems.getDefault();
		src = fileSystem
				.getPath("../eip-common/src/main/resources/orders/order1.csv");
		destDir = fileSystem.getPath("target", "orders");
		destFile = destDir.resolve(src.getFileName());
		if (!Files.isDirectory(destDir))
			Files.createDirectory(destDir);

	}
	@AfterClass(alwaysRun=false)
	public void afterMethod() throws IOException
	{
		if (Files.exists(destFile))
			Files.delete(destFile);		
	}

	@Test
	public void check() throws InterruptedException, IOException {
		// To train the objects before the processing starts, copy the file
		// first after training
		destFile = Files.copy(src, destFile,
				StandardCopyOption.REPLACE_EXISTING);
		CountDownLatch latch = new CountDownLatch(2);
		doAnswer(new CountDownLatchAnswer().countsDownLatch(latch)).when(
				backlogService).saveBacklogItems(Mockito.any(Backlog.class));
		assertTrue(latch.await(3, TimeUnit.SECONDS), "latch interupted");
		verify(backlogService, Mockito.times(2)).saveBacklogItems(
				Mockito.any(Backlog.class));
	}
}
