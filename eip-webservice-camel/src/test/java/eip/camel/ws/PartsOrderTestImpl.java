package eip.camel.ws;

import parts.eip.OrderRequest;
import parts.eip.OrderResponse;
import parts.eip.PartsOrder;

public class PartsOrderTestImpl implements PartsOrder {

    @Override
    public OrderResponse order(OrderRequest orderRequest) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumberUuid("1234");
        return orderResponse;
    }

}
