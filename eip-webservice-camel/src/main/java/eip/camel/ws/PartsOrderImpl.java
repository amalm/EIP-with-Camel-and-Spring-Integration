package eip.camel.ws;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import parts.eip.OrderRequest;
import parts.eip.OrderResponse;
import parts.eip.PartsOrder;

public class PartsOrderImpl implements PartsOrder {

    private Map<UUID, OrderRequest> orders;

    public PartsOrderImpl() {
        orders = Collections.synchronizedMap(new HashMap<UUID, OrderRequest>());
    }

    @Override
    public OrderResponse order(OrderRequest orderRequest) {
        if (orderRequest.getOder().getItem().size() > 0) {
            OrderResponse orderResponse = new OrderResponse();
            UUID orderUuid = UUID.randomUUID();
            orderResponse.setOrderNumberUuid(orderUuid.toString());
            orders.put(orderUuid, orderRequest);
            return orderResponse;
        } else {
            return null;
        }
    }
}
