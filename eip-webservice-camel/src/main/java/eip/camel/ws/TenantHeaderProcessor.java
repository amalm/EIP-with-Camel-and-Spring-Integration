package eip.camel.ws;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantHeaderProcessor implements Processor
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantHeaderProcessor.class);
    @Override
    public void process(Exchange exchange) throws Exception
    {
        String tenantId = (String) exchange.getIn().getHeader("TENANT_ID");
        if (tenantId != null) {
            LOGGER.info(tenantId);
        }
        tenantId = (String) exchange.getIn().getHeader("tenant_id");
        if (tenantId != null) {
            LOGGER.info(tenantId);
        }
        
    }
}
