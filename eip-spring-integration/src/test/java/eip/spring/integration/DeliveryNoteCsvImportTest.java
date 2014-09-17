package eip.spring.integration;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import eip.common.entities.StockItem;
import eip.common.services.StockService;
import eip.common.testutil.CountDownLatchAnswer;

/**
 * Integration testing delivery note reception by mocking the ItemWriter.
 * 
 * @author Anders Malmborg
 * 
 */
@ContextConfiguration(locations = {
		"classpath:META-INF/services.memory.spring.xml",
		"classpath:META-INF/springintegration.spring.xml",
		"classpath:deliverynote.spring.test.xml" })
public class DeliveryNoteCsvImportTest extends AbstractTestNGSpringContextTests {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DeliveryNoteCsvImportTest.class);
	private Path src, destDir, destFile;
	private FileSystem fileSystem;

	@Autowired
	private StockService stockService;

	@BeforeMethod
	public void beforeMethod() throws IOException {
		fileSystem = FileSystems.getDefault();
		src = fileSystem
				.getPath("../eip-common/src/main/resources/deliverynotes/deliverynote1.csv");
		destDir = fileSystem.getPath("target", "deliverynotes");
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
	public void check() throws Exception {
		// To train the objects before the processing starts, copy the file
		// first after training
		destFile = Files.copy(src, destFile,
				StandardCopyOption.REPLACE_EXISTING);
		LOGGER.info("Copied {} to {}", src.toString(), destFile.toString());
		CountDownLatch latch = new CountDownLatch(2);
		doAnswer(new CountDownLatchAnswer().countsDownLatch(latch)).when(
				stockService).addStockItem(Mockito.any(StockItem.class));
		assertTrue(latch.await(3, TimeUnit.SECONDS), "latch interupted");
		verify(stockService, times(2)).addStockItem(
				Mockito.any(StockItem.class));
	}
}
