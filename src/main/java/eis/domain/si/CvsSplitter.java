package eis.domain.si;

import java.io.File;
import java.util.Arrays;

import org.springframework.integration.Message;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.util.Assert;

import eis.domain.entities.Item;

public class CvsSplitter extends AbstractMessageSplitter {

	@Override
	protected Object splitMessage(Message<?> message) {
		Assert.isTrue(message.getPayload() instanceof File, "payload should be a File");
		return Arrays.asList(new Item("name1", "nr1", null), new Item("name2", "nr2", null));
	}

}
