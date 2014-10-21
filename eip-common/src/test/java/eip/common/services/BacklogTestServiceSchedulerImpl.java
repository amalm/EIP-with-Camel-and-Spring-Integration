package eip.common.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import eip.common.entities.BacklogItem;
import eip.common.services.Backlog;
import eip.common.services.BacklogService;

/**
 * @see BacklogServiceMemory
 * @author Michael Haslgrübler
 *
 */
public class BacklogTestServiceSchedulerImpl implements BacklogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BacklogTestServiceSchedulerImpl.class);
    private final List<BacklogItem> list;

    public BacklogTestServiceSchedulerImpl() {
        list = new ArrayList<BacklogItem>();
    }

    @Override
    public void saveBacklogItems(Backlog backlog) {
        for (BacklogItem backlogItem : backlog.getItems()) {
            LOGGER.info("Saving Backlog item:{}", backlogItem.toString());
            list.add(backlogItem);
        }
    }

    @Override
    public List<BacklogItem> getBacklogItems() {
        return Collections.unmodifiableList(list);
    }

    @Scheduled(fixedDelay = 1000)
    public void processBacklog() {
        LOGGER.info("processing Backlog");
        if (list.size() > 0) {
            BacklogItem backlogItem = list.remove(0);
            LOGGER.warn("removed backlogitem: " + backlogItem);
        }
    }

}
