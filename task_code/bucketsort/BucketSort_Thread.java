package fcds_lab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BucketSort_Thread {

	public static void main(String[] args) throws Exception {
		int bucketNo = 94;
		int threadNo = 4;
		ArrayList[] buckets = new ArrayList[bucketNo];
		
		String[] words = read_file("bucketsort.in");
		
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
			// Test buckets allocation
			//System.out.println("bucket " + i + ": " + buckets[i] + " ");
			
			if (buckets[i] != null)
				es.execute(new BucketThread(new BucketSort_Task(buckets[i])));
		
		}
		
		es.shutdown();
		es.awaitTermination(100, TimeUnit.SECONDS);

		long end = System.currentTimeMillis();
		
		System.out.println("Bucket Sort with " + threadNo + " threads finish.");
		System.out.println("\nTime used: " + (end-start) + "ms");
		
		// Print out sorted elements
//		for (int i=0; i<buckets.length; i++)
//			if (buckets[i] != null)
//				for (int j=0; j<buckets[i].size(); j++)
//					System.out.println(buckets[i].get(j));
		
		// Write to a file
		if (write_file(buckets) == true)
			System.out.println("\nFile write finish.");
	
	}
	
	public static String[] read_file(String filename) throws IOException {
		File fileReader = new File(filename);
		Scanner inputFile = new Scanner(fileReader);
		List<String> L = new ArrayList<String>();
		while (inputFile.hasNextLine()) {
			L.add(inputFile.nextLine());
		}
		return L.toArray(new String[L.size()]);
	}
	
	public static boolean write_file(ArrayList[] buckets) throws IOException {
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter("bucketsort.out"));
		
		for (int i=0; i<buckets.length; i++) {
			if (buckets[i] != null)
				for (int j=0; j<buckets[i].size(); j++) {
					fileWriter.write(buckets[i].get(j).toString());
					fileWriter.newLine();
				}
		}
		
		fileWriter.flush();
		fileWriter.close();
		
		return true;
	}
}

class BucketSort_Task {

	ArrayList _bucket;
	
	BucketSort_Task(ArrayList bucket) {
		_bucket = bucket;
	}
	
	void sort() {
		Collections.sort(_bucket);
	}
}

class BucketThread implements Runnable {
	BucketSort_Task _task;	

	BucketThread(BucketSort_Task task) {
		_task = task;
		new Thread(this);
	}

	public void run() {
		_task.sort();
	}
}
