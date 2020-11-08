package wordcountwiththread;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		Scanner scanner = new Scanner(System.in);

		String filePath = validateFilePathInput(scanner);

		int supporterThreadCount = validateInputSupporterThreadCount(scanner);

		scanner.close();

		BlockingQueue<String> q = new LinkedBlockingQueue<String>();

		Map<String, Integer> synmap = new LinkedHashMap<String, Integer>();
		Map<String, Integer> map = Collections.synchronizedMap(synmap);

		ExecutorService executorService = Executors.newFixedThreadPool(supporterThreadCount + 1);
		CountDownLatch latch = new CountDownLatch(supporterThreadCount);

		executorService.submit(new MainThread(latch, q, map, filePath));

		for (int i = 1; i <= supporterThreadCount; i++) {
			executorService.submit(new SupporterThread(i, latch, q, map));
		}

		executorService.shutdown();
	}

	private static int validateInputSupporterThreadCount(Scanner scanner) {
		System.out.println("Please Enter Thread Count (Default 5) :");
		String tmpThreadCount = scanner.nextLine();
		int supporterThreadCount;
		if (tmpThreadCount.length() == 0) {
			supporterThreadCount = 5;
		} else {
			supporterThreadCount = Integer.parseInt(tmpThreadCount);
		}

		System.out.println("Supporter Thread Count is: " + supporterThreadCount);
		return supporterThreadCount;
	}

	private static String validateFilePathInput(Scanner scanner) {
		String filePath;
		File f;

		do {
			System.out.println("Please Enter a Valid File Path :");
			filePath = scanner.nextLine();
			f = new File(filePath);
		} while (!f.exists());

		System.out.println("FilePath is: " + filePath);
		return filePath;
	}

}
