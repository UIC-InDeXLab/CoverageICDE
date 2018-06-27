package timing;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

import pattern.Pattern;

public class MupSearchRunnable implements Runnable {

	public final Queue<Pattern> resultQueue;

	public MupSearchRunnable(Queue<Pattern> queue) {
		this.resultQueue = queue;
	}

	public void run() {
	}
}
