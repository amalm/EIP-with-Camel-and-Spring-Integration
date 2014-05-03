package eip.camel;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.util.Assert;

import eip.common.entities.Item;
import eip.common.entities.ItemType;
import eip.common.entities.StockItem;

public class CsvToStockItemProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String csvString = exchange.getIn().getBody(String.class);
		csvString = csvString.replace("[", "");
		csvString = csvString.replace("]", "");
		List<String> strings = Arrays.asList(csvString.split(","));
		Assert.isTrue(strings.size() == 5);
		String type = strings.get(0).trim();
		ItemType itemType = ItemType.OTHER;
		if (type.equals(ItemType.FRAME.toString()))
			itemType = ItemType.FRAME;
		else if (type.equals(ItemType.DRIVE.toString()))
			itemType = ItemType.DRIVE;
		else if (type.equals(ItemType.WHEEL.toString()))
			itemType = ItemType.WHEEL;

		Item item = new Item(itemType, strings.get(1).trim(), strings.get(2).trim(), Double.valueOf(strings.get(3).trim()));
		StockItem stockItem = new StockItem(item, Integer.parseInt(strings.get(4).trim()));
		exchange.getIn().setBody(stockItem);
	}
}
