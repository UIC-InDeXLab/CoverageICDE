package timing;

public class DataCollectionTimeout {

	private final long timeoutMilliSeconds;
	private long timeoutInteval = 10;

	public DataCollectionTimeout(long timeoutSeconds) {
		this.timeoutMilliSeconds = timeoutSeconds * 1000;
	}

	public void addBlock(Runnable runnable) throws Throwable {
		long collectIntervals = 0;
		Thread timeoutWorker = new Thread(runnable);
		timeoutWorker.start();
		do {
			if (collectIntervals >= this.timeoutMilliSeconds) {
				
				// Sleep 1 second
				Thread.sleep(1000);
				
				// Then interrupt
				timeoutWorker.interrupt();
	
				throw new Exception("<<<<<<<<<<****>>>>>>>>>>> Timeout Block Execution Time Exceeded In "
						+ timeoutMilliSeconds + " Milli Seconds. Thread Block Terminated.");
			}
			collectIntervals += timeoutInteval;
			Thread.sleep(timeoutInteval);

		} while (timeoutWorker.isAlive());
		// System.out.println("<<<<<<<<<<####>>>>>>>>>>> Timeout Block Executed
		// Within "+collectIntervals+" Milli Seconds.");
	}

	/**
	 * @return the timeoutInteval
	 */
	public long getTimeoutInteval() {
		return timeoutInteval;
	}

	/**
	 * @param timeoutInteval
	 *            the timeoutInteval to set
	 */
	public void setTimeoutInteval(long timeoutInteval) {
		this.timeoutInteval = timeoutInteval;
	}
}