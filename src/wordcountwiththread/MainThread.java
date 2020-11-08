package wordcountwiththread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class MainThread extends Thread {

	static int sentencesCount = 0;
	static int averageWordCount = 0;
	
	private final BlockingQueue<String> queue;	
	
	public CountDownLatch latch;
	public Map<String, Integer> sortedMapForWords;
	
	public String filePath;
	
	StringBuilder str = new StringBuilder();
	

	MainThread(CountDownLatch latch, BlockingQueue<String> q, Map<String, Integer> sm, String filePath) {
		queue = q;
		this.latch = latch;
		sortedMapForWords = sm;
		this.filePath = filePath;
	}

	public void run() {

		System.out.println("LOG -- MainThread - Start Main Thread");
		readFileAndPutQueue(filePath);		

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		sortedMapForWords = MapUtil.sortByValues(sortedMapForWords);

		Iterator<SortedMap.Entry<String, Integer>> itr = sortedMapForWords.entrySet().iterator();		
		
		while (itr.hasNext()) {
			SortedMap.Entry<String, Integer> entry = itr.next();
			str.append(entry.getKey());
			str.append("  ");
			str.append(entry.getValue());
			str.append("\n");
		}
		
		System.out.println(str.toString());
	}

	private void readFileAndPutQueue(String filePath) {
		try {
			File file = new File(filePath);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			int tmpWordCount = 0;

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.isEmpty() && !"".equals(line))
					break;				
				
				str.append(line);
				str.append("\n");
				
				String[] sentencesInLine = line.split("[.]|[?]|[!]");
				
				for(String sentence : sentencesInLine) {
					StringTokenizer tokens = new StringTokenizer(sentence);
				    tmpWordCount += tokens.countTokens();
				    
				    System.out.println("LOG -- MainThread - Data has been put to queue with Main Thread  " + "New queue size : "+ queue.size() + " --- "  + sentence);
				    queue.put(sentence);
				}		
			    		    				
			    sentencesCount += sentencesInLine.length;				
			}

			// to mark the end of queue
			queue.put("$");
			
			averageWordCount = tmpWordCount / sentencesCount;
			
			str.append("Sentence Count : ");
			str.append(sentencesCount);
			str.append("\n");
			str.append("Avg. Word Count : ");
			str.append(averageWordCount);
			str.append("\n");
			
			
			bufferedReader.close();
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
