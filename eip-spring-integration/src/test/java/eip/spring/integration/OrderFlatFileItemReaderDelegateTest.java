package eip.spring.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import eip.common.entities.Order;

public class OrderFlatFileItemReaderDelegateTest {
	private OrderFlatFileItemReaderDelegate target;
	@BeforeMethod
	public void beforMethod()
	{
		
		PatternMatchingCompositeLineTokenizer tokenizer = new PatternMatchingCompositeLineTokenizer();
		Map<String, LineTokenizer> tokenizers = new HashMap<String, LineTokenizer>();
		DelimitedLineTokenizer orderTokenizer = new DelimitedLineTokenizer();
		orderTokenizer.setNames(Arrays.asList("recType", "customerName", "orderNumber").toArray(new String[0]));
		orderTokenizer.setDelimiter(";");
		tokenizers.put("ORDER*", orderTokenizer);
		DelimitedLineTokenizer orderItemTokenizer = new DelimitedLineTokenizer();
		orderItemTokenizer.setNames(Arrays.asList("recType", "itemType","name","number").toArray(new String[0]));
		orderItemTokenizer.setDelimiter(";");
		tokenizers.put("ITEM*", orderItemTokenizer);
		tokenizer.setTokenizers(tokenizers);

		FlatFileItemReader<FieldSet> delegate = new FlatFileItemReader<FieldSet>();
		DefaultLineMapper<FieldSet> lineMapper = new DefaultLineMapper<FieldSet>();
		lineMapper.setFieldSetMapper(new PassThroughFieldSetMapper());
		lineMapper.setLineTokenizer(tokenizer);
		delegate.setLineMapper(lineMapper);
		target = new OrderFlatFileItemReaderDelegate(delegate);
	}
	@Test
	public void read() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception
	{
	    // Given an order file
		target.setResource(new ClassPathResource("orders/order1.csv"));
		target.open(new ExecutionContext());
		
		// When the first record is read
		Order order = target.read();
		// Then the record is parsed into an Order bean
		assertEquals(order.getCustomerName(), "Bike support");
		assertEquals(order.getNumber(), "1");
		assertEquals(order.getOrderItems().size(), 2);
		order = target.read();
		assertEquals(order.getCustomerName(), "Bike specialists");
		assertEquals(order.getNumber(), "2");
		assertEquals(order.getOrderItems().size(), 1);
		assertNull(target.read());
		
	}
	
}
