package eip.camel;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import eip.common.entities.StockItem;
import eip.common.services.StockService;
import eip.common.testutil.CountDownLatchAnswer;

@ContextConfiguration(locations = {
		"classpath:META-INF/services.memory.spring.xml",
		"classpath:META-INF/camel.spring.xml",
		"classpath:deliverynote.camel.spring.test.xml" })
public class DeliveryNoteCsvImportTest extends AbstractTestNGSpringContextTests {
	@Autowired
	private StockService stockService;
	private Path src, destDir, destFile;
	private FileSystem fileSystem;
	
	@BeforeMethod
	public void beforeMethod() throws IOException
	{
		fileSystem = FileSystems.getDefault();
		src = fileSystem.getPath("../eip-common/src/main/resources/deliverynotes/deliverynote1.csv");
		destDir = fileSystem.getPath("target", "deliverynotes");
		destFile = destDir.resolve(src.getFileName());
		if (!Files.isDirectory(destDir))
			Files.createDirectory(destDir);
	}

	@Test
	public void check() throws Exception {
		destFile = Files.copy(src, destFile, StandardCopyOption.REPLACE_EXISTING);
		CountDownLatch latch = new CountDownLatch(2);
		doAnswer(new CountDownLatchAnswer().countsDownLatch(latch)).when(stockService)
				.addStockItem(Mockito.any(StockItem.class));
		assertTrue(latch.await(3, TimeUnit.SECONDS), "latch interupted");
		verify(stockService, times(2)).addStockItem(Mockito.any(StockItem.class));
	}
}
