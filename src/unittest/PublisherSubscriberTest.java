package unittest;

import org.junit.Before;
import org.junit.Test;

import event.PubSubListener;
import event.PublishEventType;
import event.Publisher;
import junit.framework.TestCase;

/**
 * This set of tests makes sure that events are being published properly
 * and subscribers can respond to those events.
 * 
 * @author Adi
 */
public class PublisherSubscriberTest extends TestCase {
	/**
	 * Clears everything from the publisher before running these tests.
	 */
	@Before
	public void clear() {
		Publisher.getInstance().clearInstance();
	}
	
    /**
     * Test that getInstance returns an instance of type Publisher.
     */
    public void testInstantiation() {
        Object instance = Publisher.getInstance();
        
        assertTrue(instance != null);
        assertTrue(instance instanceof Publisher);
    }
    
    /**
     * Tests that an invalid event type throws an exception.
     */
    @Test (expected = Exception.class)
    public void testBindSubscriberException() {
    	Publisher.getInstance().bindSubscriber(new TestPubSubListener(), null);
    }
    
    /**
     * Test that a valid event type is registered.
     */
    public void testBindSubscriberEvent() {
    	Publisher instance = Publisher.getInstance();
    	instance.bindSubscriber(new TestPubSubListener(), PublishEventType.PLAYER_DEATH);
    	
    	assertTrue(instance.getBindings().containsKey(PublishEventType.PLAYER_DEATH));
    	assertTrue(instance.getBindings().get(PublishEventType.PLAYER_DEATH).size() == 1);
    }
    
    /**
     * Test that a triggered event calls the event handler.
     */
    public void testEventTrigger() {
    	Publisher instance = Publisher.getInstance();
    	TestPubSubListener testListener = new TestPubSubListener();
    	instance.bindSubscriber(testListener, PublishEventType.ENEMY_DEATH);
    	instance.trigger(PublishEventType.ENEMY_DEATH);
    	
    	assertTrue(testListener.eventCalled == true);
    }
    
    /**
     * Test that clearing the instance removes all the things.
     */
    public void testClearInstance() {
    	Publisher instance = Publisher.getInstance();
    	instance.bindSubscriber(new TestPubSubListener(), PublishEventType.CHARACTER_DAMAGED);
    	
    	assertTrue(instance.getBindings().size() != 0);
    	instance.clearInstance();
    
    	assertTrue(instance.getBindings().size() == 0);
    }
    
    /**
     * Helper class for our tests.
     */
    class TestPubSubListener implements PubSubListener {
    	public boolean eventCalled = false;
    	
		@Override
		public void handleEvent() {
			eventCalled = true;
		}
    }
}