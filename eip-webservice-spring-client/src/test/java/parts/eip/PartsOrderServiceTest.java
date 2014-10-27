package parts.eip;

import static org.testng.AssertJUnit.assertEquals;

import javax.xml.transform.Source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.client.RequestMatchers;
import org.springframework.ws.test.client.ResponseCreators;
import org.springframework.xml.transform.StringSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Michael Haslgrübler
 *
 */
@ContextConfiguration(locations = "classpath:META-INF/spring.ws.client.xml")
public class PartsOrderServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected PartsOrderService partsOrder;

    private MockWebServiceServer mockServer;

    @BeforeMethod(firstTimeOnly = true)
    public void createServer() throws Exception {
        mockServer = MockWebServiceServer.createServer(partsOrder);
    }

    @Test
    public void testWebserviceCall() throws Exception {
        Source responsePayload = new StringSource("<OrderResponse xmlns='http://eip.parts'><orderNumberUuid>1234</orderNumberUuid></OrderResponse>");
        mockServer.expect(RequestMatchers.anything()).andRespond(ResponseCreators.withPayload(responsePayload));
        OrderResponse rp = partsOrder.order(new OrderRequest());
        assertEquals("1234", rp.getOrderNumberUuid());
    }
}
