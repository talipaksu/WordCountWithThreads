package wordcountwiththread;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class SupporterThread extends Thread {

	public static boolean shouldContinue = true;

	private int id;

	public CountDownLatch latch;

	private final BlockingQueue<String> queue;

	public Map<String, Integer> sortedMapForWords;

	SupporterThread(int id, CountDownLatch latch, BlockingQueue<String> q, Map<String, Integer> sm) {
		this.id = id;
		this.latch = latch;
		queue = q;
		sortedMapForWords = sm;
	}

	public synchronized void run() {
		try {
			System.out.println("LOG -- SupporterThread - Start Supporter Thread with id : " + id);
			String value = "";
			do {
				if (!shouldContinue)
					break;

				value = queue.take();

				System.out.println("LOG -- SupporterThread - Read data from the queue with thread id : " + id
						+ "  New queue size : " + queue.size() + " --- " + value);

				if (value.equals("$")) {
					shouldContinue = false;
					queue.put("$");
					break;
				}
				// remove all punctuation marks
				for (String keyValue : value.replaceAll("[^a-zA-Z0-9 ]", "").split(" ")) {
					if (!sortedMapForWords.containsKey(keyValue)) {
						sortedMapForWords.put(keyValue, 1);
					} else {
						int tmpValue = sortedMapForWords.get(keyValue);
						sortedMapForWords.put(keyValue, tmpValue + 1);
					}
				}
			} while (shouldContinue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			latch.countDown();
			System.out.println("LOG -- SupporterThread - Finish of the thread with id : " + id);
		}

	}

}
