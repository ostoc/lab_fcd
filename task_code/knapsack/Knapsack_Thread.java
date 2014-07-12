package knapsack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Knapsack_Thread {

	public static void main(String[] args) throws Exception {		
		System.out.print("Please enter the number of cores you want to use: ");
		Scanner scan = new Scanner(System.in);
		int threadNo = scan.nextInt();
		
		List<Integer> col1 = new ArrayList<Integer>();
		List<Integer> col2 = new ArrayList<Integer>();
		Scanner input = new Scanner(new File("knapsack3.in"));
		int i;
		Integer result = 0;

		while (input.hasNext()) {
			col1.add(input.nextInt());
			col2.add(input.nextInt());
		}
		
		input.close();
		
		int n = col1.get(0);
		int M = col2.get(0);
		int[] v = new int[n];
		int[] w = new int[n];
		
		for (i=0; i<n; i++) {
			v[i] = col1.get(i+1);
			w[i] = col2.get(i+1);
		}
		
		// Initiate
		Item_t[] itens = new Item_t[n];
		for (i=0; i<n; i++) {
			itens[i] = new Item_t(v[i], w[i], v[i]/w[i]);
		}
		
		List<Item_t> list = new ArrayList<Item_t>(Arrays.asList(itens));
		
		Collections.sort(list);
	
		//int result = knapsack(n, M, list);
		
//		for (i=0; i<n; i++)
//			System.out.println(i + "   " + list.get(i).getV() + "   " + list.get(i).getW() + "   " + list.get(i).getD());
		
		long time1 = System.currentTimeMillis();
		
		ExecutorService es = Executors.newFixedThreadPool(threadNo);
		
		List<Callable<Integer>> task = new ArrayList<Callable<Integer>>();
		
		for (i=0; i<n; i++) {
			//task.add(new Knapsack_Task(n-1, M-list.get(i).getW(), list.subList(1, list.size())));
			//List<Item_t> listCopy = new ArrayList<Item_t>(list);
			List<Item_t> listCopy = new ArrayList<Item_t>();
			for (int j = 0; j < n; j++) {
				Item_t ol = list.get(j);
				Item_t ne = new Item_t(ol._value, ol._weight, ol._density);
				listCopy.add(ne);
			}
			task.add(new Knapsack_Task(n-1, M-listCopy.get(i).getW(), listCopy.subList(1, listCopy.size())));
		}
		
		List<Future<Integer>> result_list = es.invokeAll(task);
		int[] result_array = new int[n];
		
		for (i=0; i<n; i++) {
			result_array[i] = result_list.get(i).get() + list.get(i).getV();
			//System.out.println(result_array[i]);
			
			if (result_array[i] > result)
				result = result_array[i];
		}
//		for (Future<Integer> f : result_set)
//			if ((f > result)
//				result = f;
		
		es.shutdown();
		
		long time2 = System.currentTimeMillis();
		
		System.out.println("Result: " + result);
		System.out.println("Time used: " + (time2-time1) + "ms");
	
	}
	
	
}

class Knapsack_Task implements Callable {
	
	private int _n, _M;
	private long time1;
	private List<Item_t> _list;

	/*static*/ int greater(int x, int y) {
		return (x > y) ? x : y;
	}

	/*static */int knapsack(int n, int M, List<Item_t> list) {

		int v = 0, w = 0;
		int r = 0;

		if (n < 1)
			return 0;

		while (M - w >= 0) {
			//r = greater(r, v + knapsack(n - 1, M - w, list.subList(1, list.size())));
			int temp = knapsack(n - 1, M - w, list.subList(1, list.size()));
			r = greater(r, temp+v);
			v += list.get(0).getV();
			w += list.get(0).getW();
		}
		
		return r;
	}

	public Knapsack_Task(int n, int M, List<Item_t> list) {
		_n = n;
		_M = M;
		_list = list;
		//time1 = System.currentTimeMillis();
	}
	
	public Integer call() throws Exception {
		//time1 = System.currentTimeMillis();
		int r = /*Knapsack_Thread.*/knapsack(_n, _M, _list);
		//long time2 = System.currentTimeMillis();
		//System.out.println("Time taken: " + (time2-time1));
		return r;
	}
}