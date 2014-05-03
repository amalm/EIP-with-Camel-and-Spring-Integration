package eip.common.testutil;

import java.util.concurrent.CountDownLatch;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Counts down the latch each time the mocked method is called.
 * @author Anders Malmborg
 *
 */
public class CountDownLatchAnswer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CountDownLatchAnswer.class);
	@SuppressWarnings("rawtypes")
	public Answer countsDownLatch(final CountDownLatch latch)
	{
		return new Answer() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				
				LOGGER.info("decrease latch count from {}", latch.getCount());
				latch.countDown();
				return null;
			}
		};
	}
}
