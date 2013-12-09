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
	
	// One-to-many mapping of event type to listeners.
	private Map<PublishEventType, List<PubSubListener>> bindings = new HashMap<PublishEventType, List<PubSubListener>>();
	
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
	 * @param eventType The type of the event.
	 */
	public void bindSubscriber (PubSubListener listener, PublishEventType eventType) {
		try {
			if (eventType == null || listener == null) {
				throw new Exception();
			}
			if (!bindings.containsKey(eventType)) {
				List<PubSubListener> listenerList = new ArrayList<PubSubListener>();
				listenerList.add(listener);
				bindings.put(eventType, listenerList);
			} else {
				bindings.get(eventType).add(listener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Triggers an event and notifies all listeners.
	 * 
	 * @param eventType The type of the event.
	 */
	public void trigger (PublishEventType eventType) {
		if (bindings.containsKey(eventType)) {
			for (PubSubListener listener : bindings.get(eventType)) {
				listener.handleEvent();
			}
		} else {
			System.out.println("No subscribers are listening to event " + eventType.toString());
		}
	}
	
	/**
	 * De-subscribes all listeners to a given event.
	 * 
	 * @param PublishEventType The type of the event.
	 */
	public void unbindAllSubscribers (PublishEventType eventName) {
		if (bindings.containsKey(eventName)) {
			bindings.get(eventName).clear();
		}
	}
	
	/**
	 * Clears the instance.
	 */
	public void clearInstance () {
		bindings = new HashMap<PublishEventType, List<PubSubListener>>();
		instance = null;
	}
	
	/**
	 * Returns the mapping.
	 */
	public Map<PublishEventType, List<PubSubListener>> getBindings() {
		return bindings;
	}
}