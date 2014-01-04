package eip.common.services;

import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import eip.common.entities.BacklogItem;
import eip.common.entities.Item;
import eip.common.entities.ItemType;
import eip.common.repositories.BacklogItemRepository;

public class BacklogServiceTest {
	private BacklogService target;
	@Mock
	private BacklogItemRepository repository;
	@BeforeMethod
	public void beforeMathod()
	{
		MockitoAnnotations.initMocks(this);
		target = new BacklogService(repository);
	}
	
	@Test
	public void orderBacklogItems() throws FileNotFoundException
	{
		List<BacklogItem> backlogItems = Arrays.asList(new BacklogItem(new Item(ItemType.DRIVE, "name1", "number1")),
												new BacklogItem(new Item(ItemType.DRIVE, "name2", "number2")));
		target.orderBacklogItems(new Backlog(backlogItems));
		verify(repository).save(backlogItems);
	}
}
