package timing;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

import dataCollectionNew.PatternValueNode;
import pattern.Pattern;

public class DataCollectionRunnable implements Runnable {

	public final Queue<PatternValueNode> resultQueue;

	public DataCollectionRunnable(Queue<PatternValueNode> queue) {
		this.resultQueue = queue;
	}

	public void run() {
	}
}
