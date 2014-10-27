package eip.webservice;

import java.util.UUID;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import parts.eip.ObjectFactory;
import parts.eip.OrderRequest;
import parts.eip.OrderResponse;

/**
 * Demo Web Service for EIP.
 * @author Anders Malmborg
 */
@Endpoint
public class PartsEndpoint
{
    private static final String NAMESPACE_URI = "http://eip.parts";
    private final ObjectFactory fct = new ObjectFactory();

    /**
     * Returns the CustomerType for this session or a new one if no session.
     * @param request customer UUID
     * @return CustomerType
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "OrderRequest")
    @ResponsePayload
    public OrderResponse getCustomerRequest(@RequestPayload OrderRequest request)
    {
    	OrderResponse orderResponse = fct.createOrderResponse();
    	orderResponse.setOrderNumberUuid(UUID.randomUUID().toString());
        return orderResponse;
    }
}
