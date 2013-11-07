package event;

/**
 * Interface for any listeners to our Publisher.
 * TODO(adi): Pass in some information to handleEvent.
 * 
 * @author Adi
 */
public interface PubSubListener {
	public void handleEvent (); 
}
