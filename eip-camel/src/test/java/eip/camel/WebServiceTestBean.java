package eip.camel;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parts.eip.OrderResponse;

/**
 * a bean to response to web service calls from Apache camel
 * 
 * @author Michael Haslgrübler
 *
 */
public class WebServiceTestBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceTestBean.class);

    public void processSOAP(Exchange exchange) {
        LOGGER.info("webservice testbean called");
        // OrderRequest orderRequest =
        // exchange.getIn().getBody(OrderRequest.class);
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumberUuid("1234");
        exchange.getOut().setBody(orderResponse);
    }

}
