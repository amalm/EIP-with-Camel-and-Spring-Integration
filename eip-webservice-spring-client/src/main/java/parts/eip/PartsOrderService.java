package parts.eip;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

/**
 * Implements the WebService Client for PartsOrder
 * 
 * @author Michael Haslgrübler
 *
 */
public class PartsOrderService extends WebServiceGatewaySupport {

    public OrderResponse order(OrderRequest orderRequest) {
        OrderResponse response = (OrderResponse) getWebServiceTemplate().marshalSendAndReceive(orderRequest);
        return response;
    };

}
