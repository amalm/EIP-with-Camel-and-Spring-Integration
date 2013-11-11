package eis.domain.si;

import java.util.Arrays;
import java.util.List;

import org.springframework.integration.Message;
import org.springframework.integration.config.xml.SplitterParser;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;

import eis.domain.entities.Item;

public class CsvTransformer implements Transformer {

	@Override
	public Message<?> transform(Message<?> message) {
		List<String> l = Arrays.asList(((String)message.getPayload()).split(";"));
		Item item = new Item(l.get(0), l.get(1), Double.valueOf(l.get(2)));
		Object[] payload = new Object[] {item, Integer.valueOf(l.get(3))};
		return MessageBuilder.withPayload(item).build();
	}

}
