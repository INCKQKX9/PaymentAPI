package com.amway.support;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;


/**
 * Stopwatch the elapsed/start time of the stop watch
 * 
 */
public class StopWatch {
	
//	private static Stopwatch sw = Stopwatch.createStarted().start();

//	private static Stopwatch sw = new Stopwatch().start();
	
	/**
	 * Returns the start time of the stop watch
	 * 
	 * @return time in seconds
	 */
	public static long startTime() {
		Stopwatch ss = Stopwatch.createStarted();
		return ss.elapsed(TimeUnit.MILLISECONDS);
	}

	/**
	 * Returns the elapsed time of the stop watch.
	 * 
	 * @param startTime
	 *            - start time
	 * 
	 * @return elapsed time in seconds
	 */
	public static long elapsedTime(long startTime) {
		return (long) (Stopwatch.createStarted().elapsed(TimeUnit.MILLISECONDS) - startTime) / 1000;
	}

}
