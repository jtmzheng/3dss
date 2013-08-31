package system;

/*
 * Timer class built on top of System.nanoTime()
 * Instances of this can be used separately, which is good. 
 */
public class Timer {
	
	/*
	 * Time when this timer was created
	 */
	private long initialTime;
	
	public Timer () {
		initialTime = System.nanoTime();
	}
	
	public long getTimeSinceReset () {
		return System.nanoTime() - initialTime;
	}
	
	public long getInitialTime () {
		return initialTime;
	}
	
	public void reset () {
		initialTime = System.nanoTime();
	}
}
