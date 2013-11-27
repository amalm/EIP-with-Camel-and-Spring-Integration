package eis.domain.services;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import eis.domain.entities.Item;
import eis.domain.entities.ItemType;

public class BacklogServiceTest {
	private BacklogService target;
	@BeforeMethod
	public void beforeMathod()
	{
		FlatFileItemWriter<Item> writer = new FlatFileItemWriter<Item>();
		DelimitedLineAggregator<Item> lineAggregator = new DelimitedLineAggregator<Item>();
		lineAggregator.setDelimiter(";");
		BeanWrapperFieldExtractor<Item> fieldExtractor = new BeanWrapperFieldExtractor<Item>();
		fieldExtractor.setNames(Arrays.asList("name", "number").toArray(new String[0]));
		lineAggregator.setFieldExtractor(fieldExtractor);
		writer.setLineAggregator(lineAggregator);
		File backlogDir = new File("target");
		target = new BacklogService(writer, backlogDir);
	}
	
	@Test
	public void orderBacklogItems() throws FileNotFoundException
	{
		List<Item> backlogItems = Arrays.asList(new Item(ItemType.DRIVE, "name1", "number1"),
												new Item(ItemType.DRIVE, "name2", "number2"));
		target.orderBacklogItems(new Backlog(backlogItems));
		Scanner scanner = new Scanner(BacklogService.getLastFile());
		assertEquals(scanner.nextLine(), "name1;number1");
		assertEquals(scanner.nextLine(), "name2;number2");
		scanner.close();
	}
}
