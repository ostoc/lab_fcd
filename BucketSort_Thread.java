package fcds_lab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BucketSort_Thread {

	public static void main(String[] args) throws Exception {
		int bucketNo = 128;
		int threadNo = 16;
		ArrayList[] buckets = new ArrayList[bucketNo];
		ArrayList result = new ArrayList();
		
		String[] words = load_list("C:/Rossi/Files/Java/fcds_lab/input/bucketsort.in");
		
		for (String s: words) {
			int fChar = s.charAt(0);
			int re = fChar % bucketNo;

			if (buckets[re] == null)
				buckets[re] = new ArrayList();
			
			buckets[re].add(s);
		}
		
		ExecutorService es = Executors.newFixedThreadPool(threadNo);
		
		long start = System.currentTimeMillis();
		
		for (int i=0; i<buckets.length; i++) {
			//System.out.println("bucket " + i + ": " + buckets[i] + " ");
			
			if (buckets[i] != null)
				es.execute(new BucketThread(new BucketSort_Task3(buckets[i])));
						
//			try {
//				task.wait();
//			} catch (InterruptedException exc) {
//				System.out.println(exc);
//			}			
		}
		
		es.shutdown();
		es.awaitTermination(1, TimeUnit.NANOSECONDS);
		
//		for (String s: words) {
//			System.out.println(s);
//		}
		
		for (int i=0; i<buckets.length; i++) {
			if (buckets[i] != null) {
				for (int j=0; j<buckets[i].size(); j++)
					result.add(buckets[i].get(j));
			}
		}
		
		for (int i=0; i<result.size(); i++)
			System.out.println(result.get(i));
		
		long end = System.currentTimeMillis();
		
		System.out.println("Time used: " + (end-start) + "ms");
	}
	
	public static String[] load_list(String filename) throws IOException {
		File fileReader = new File(filename);
		Scanner inputFile = new Scanner(fileReader);
		List<String> L = new ArrayList<String>();
		while (inputFile.hasNextLine()) {
			L.add(inputFile.nextLine());
		}
		return L.toArray(new String[L.size()]);
	}
}

class BucketThread implements Runnable {
	BucketSort_Task3 _task;	

	BucketThread(BucketSort_Task3 task) {
		_task = task;
		new Thread(this);
	}

	public void run() {
		_task.sort();
	}
}
