package eip.camel.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import parts.eip.ItemType;
import parts.eip.OrderRequest;
import parts.eip.OrderResponse;
import parts.eip.OrderType;
import parts.eip.PartsOrder;
import eip.common.entities.BacklogItem;
import eip.common.entities.Item;
import eip.common.services.Backlog;
import eip.common.services.BacklogService;

/**
 * @see BacklogServiceMemory
 * @author Michael Haslgrübler
 *
 */
public class BacklogTestServiceSchedulerImpl implements BacklogService {

    @Autowired
    @Qualifier(value = "partsOrderServiceClient")
    protected PartsOrder partsOrder;

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
            BacklogItem backlogItem = list.get(0);
            OrderResponse orderResponse = partsOrder.order(toOrderRequest(backlogItem));
            if (orderResponse != null && StringUtils.hasText(orderResponse.getOrderNumberUuid())) {
                list.remove(backlogItem);
                LOGGER.warn("removed backlogitem because we ordered it with #"+orderResponse.getOrderNumberUuid()+": " + backlogItem);
            } else {
                LOGGER.warn("removed backlogitem failed because no valid orderResponse was given: " + backlogItem);
            }
        }
    }

    private ItemType toItemType(Item item) {
        ItemType itemType = new ItemType();
        itemType.setNumber(item.getNumber());
        itemType.setQuantity(1);
        return itemType;
    }

    private OrderRequest toOrderRequest(BacklogItem backlogItem) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOder(new OrderType());
        orderRequest.getOder().getItem().add(toItemType(backlogItem.getItem()));
        return orderRequest;
    }

}
