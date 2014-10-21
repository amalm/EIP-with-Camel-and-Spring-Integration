package eip.camel.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import eip.common.entities.BacklogItem;
import eip.common.entities.Item;
import eip.common.entities.ItemType;
import eip.common.services.Backlog;
import eip.common.services.BacklogService;
import static org.testng.Assert.*;

/**
 * 
 * @author Michael Haslgrübler
 *
 */
@ContextConfiguration(locations = "classpath:backlog-ws.camel.spring.test.xml")
public class BacklogServiceSchedulerTest extends AbstractTestNGSpringContextTests {

    @Autowired(required = true)
    private BacklogService backlogService;

    @Test
    public void testBacklogScheduler() throws Exception {
        assertNotNull(backlogService);
        assertEquals(backlogService.getBacklogItems().size(), 0);
        backlogService.saveBacklogItems(new Backlog(new BacklogItem(new Item(ItemType.DRIVE, "Radl", "2"))));
        backlogService.saveBacklogItems(new Backlog(new BacklogItem(new Item(ItemType.DRIVE, "Radl", "2"))));
        assertTrue(backlogService.getBacklogItems().size() > 0);
        int count = 3;
        while (backlogService.getBacklogItems().size() > 0 && count > 0) {
            Thread.sleep(1000);
            count--;
        }
        assertEquals(backlogService.getBacklogItems().size(), 0);
    }
}
