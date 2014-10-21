package eip.camel.ws;



import static org.testng.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import parts.eip.OrderRequest;
import parts.eip.OrderResponse;
import parts.eip.PartsOrder;

/**
 * a simple web service test to see the web service routing through camel
 * 
 * @author Michael Haslgrübler
 *
 */
@ContextConfiguration(locations = "classpath:order-ws.camel.spring.test.xml")
public class WebServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier(value="partsOrderServiceClient")
    protected PartsOrder partsOrder;

    @Test
    public void testWebserviceCall() throws Exception {
        OrderResponse rp = partsOrder.order(new OrderRequest());
        assertEquals(rp.getOrderNumberUuid(), "1234");
    }
}
