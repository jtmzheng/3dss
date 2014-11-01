package system;

import org.lwjgl.Sys;

/**
 * Timer class using System.nanoTime().
 *
 * @author Adi
 */
public class Timer {
	/**
	 * Time when this timer was created.
	 */
	private long initialTime;

	/**
	 * Creates the Timer.
	 */
	public Timer () {
		initialTime = System.nanoTime();
	}

	/**
	 * Gets the time elapsed since last reset
	 * @return the time since the last reset
	 */
	public long getTimeSinceReset () {
		return System.nanoTime() - initialTime;
	}

	/**
	 * Gets the initial time
	 * @return time when the Timer was created
	 */
	public long getInitialTime () {
		return initialTime;
	}

 	/**
	 * Resets the timer.
	 */
	public void reset () {
		initialTime = System.nanoTime();
	}
}