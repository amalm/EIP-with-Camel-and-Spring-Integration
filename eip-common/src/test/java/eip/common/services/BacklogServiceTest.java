package eip.common.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

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
	private List<BacklogItem> backlogItems;
	
	@BeforeMethod
	public void beforeMathod()
	{
		MockitoAnnotations.initMocks(this);
		target = new BacklogServiceDb(repository);
		backlogItems = Arrays.asList(new BacklogItem(new Item(ItemType.DRIVE, "name1", "number1")),
				new BacklogItem(new Item(ItemType.DRIVE, "name2", "number2")));
	}
	
	@Test
	public void saveBacklogItems() throws FileNotFoundException
	{
		target.saveBacklogItems(new Backlog(backlogItems));
		verify(repository).save(backlogItems);
	}
	
	@Test
	public void getBacklogItems()
	{
		when(repository.findAll()).thenReturn(backlogItems);
		assertEquals(target.getBacklogItems().size(), backlogItems.size());
		
	}
}
