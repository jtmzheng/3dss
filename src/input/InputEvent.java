package input;

/**
 * Abstract representation of an input event.
 * 
 * @author Adi
 */
public abstract class InputEvent {
	
	protected long timeStart;
	protected boolean valid = true;
	
	/**
	 * The time when the event occured.
	 * 
	 * @return system time when the event happened
	 */
	public long getTime() {
		return timeStart;
	}
	
	/**
	 * Sets the time this event started.
	 * 
	 * @param timeStart
	 */
	public void setTime(long timeStart) {
		this.timeStart = timeStart;
	}
	
	/**
	 * Determines whether this event is valid and should be
	 * transmitted to our event listeners.
	 * 
	 * @return true if the event is valid
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Invalidates this event. This wont get forwarded to listeners
	 * after this function is called.
	 */
	public void invalidate() {
		this.valid = false;
	}
	
}
