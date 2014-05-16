package fcds_lab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class BucketSort_Task2 extends RecursiveAction {
	
	static final int threshold = 20;
	BucketSort_Problem _problem;
	public ArrayList<String> _words;
	
	public BucketSort_Task2(ArrayList<String> words) {
		_words = words;
	}
	
	private void sortSeq (ArrayList<String> words) {
		Collections.sort(words, String.CASE_INSENSITIVE_ORDER);
	}

	public void compute() {
		if (_words.size() < threshold)
			sortSeq(_words);
		else {
			int offset = partition(_words);
			
			BucketSort_Task2 task1 = new BucketSort_Task2(new ArrayList<String>(_words.subList(0, offset-1)));
			BucketSort_Task2 task2 = new BucketSort_Task2(new ArrayList<String>(_words.subList(offset+1, _words.size())));
			invokeAll(task1, task2);
			
//			task1.getResult().addAll(task2.getResult());
//			_result = task1.getResult();
		}
	}
	
	private int partition(ArrayList<String> words) {
		int lastPo = words.size()-1;
		String x = words.get(lastPo);
		
		int i=-1;
		for (int j=0; j<lastPo; j++) {
			if (words.get(j).compareTo(x) <= 0) {
				i++;
				Collections.swap(words, i, j);
			}
		}
		
		Collections.swap(words, i+1, lastPo);
		return i+1;
	}
	
//	private void swap(ArrayList<String> words, int i, int j) {
//		if (i!=j) {
//			String temp = words.get(i);
//			words.get(i) = words.get(j);
//			words.get(j) = temp;
//		}
//	}
	
	public static void main(String[] args) throws Exception {
	    
		String[] list = load_list("C:/Rossi/Files/Java/fcds_lab/input/bucketsort2.in");
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(list)); 
		
		System.out.println("size: " + words.size() + "\n");
		
		// Sort in sequential
		//words = problem.sort();
		
		// Sort in parallel
		BucketSort_Task2 task = new BucketSort_Task2(words);
		ForkJoinPool pool = new ForkJoinPool();
		//pool.invoke(task);
		pool.submit(task);
		pool.shutdown();
		
		for (String s: words) {
			System.out.println(s);
		}

		System.out.println("\nend...");
        
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
