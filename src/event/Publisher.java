package event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton to implement a publisher-subscriber pattern for events. To publish events,
 * bind an instance of a class that implements PubSubListener to a string that represents
 * the event. Events can then be triggered and responded to from listeners.
 * 
 * @author Adi
 */
public class Publisher {
	private static Publisher instance = null;
	
	// One-to-many mapping of event names to listeners.
	private Map<String, List<PubSubListener>> bindings = new HashMap<String, List<PubSubListener>>();
	
	private Publisher () {}
	
	public static Publisher getInstance() {
		if (instance == null) {
			instance = new Publisher();
		}
		return instance;
	}
	
	/**
	 * Binds a listener to an event.
	 * 
	 * @param listener Event listener which listens to "eventName" type events.
	 * @param eventName The name of the event.
	 */
	public void bindSubscriber (PubSubListener listener, String eventName) {
		if (!bindings.containsKey(eventName)) {
			List<PubSubListener> listenerList = new ArrayList<PubSubListener>();
			listenerList.add(listener);
			bindings.put(eventName, listenerList);
		} else {
			bindings.get(eventName).add(listener);
		}
	}
	
	/**
	 * Triggers an event and notifies all listeners.
	 * 
	 * @param eventName The name of the event.
	 */
	public void trigger (String eventName) {
		if (bindings.containsKey(eventName)) {
			for (PubSubListener listener : bindings.get(eventName)) {
				listener.handleEvent();
			}
		} else {
			System.out.println("No subscribers are listening to event " + eventName);
		}
	}
	
	/**
	 * De-subscribes all listeners to a given event.
	 * 
	 * @param eventName The name of the event.
	 */
	public void unbindAllSubscribers (String eventName) {
		if (bindings.containsKey(eventName)) {
			bindings.get(eventName).clear();
		}
	}
}