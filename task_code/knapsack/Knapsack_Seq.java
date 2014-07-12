package knapsack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Knapsack_Seq {

	public static void main(String[] args) throws Exception {
		
		List<Integer> col1 = new ArrayList<Integer>();
		List<Integer> col2 = new ArrayList<Integer>();
		Scanner scan = new Scanner(new File("knapsack.in"));
		int i;

		while (scan.hasNext()) {
			col1.add(scan.nextInt());
			col2.add(scan.nextInt());
		}
		
		scan.close();
		
		int n = col1.get(0);
		int M = col2.get(0);
		int[] v = new int[n];
		int[] w = new int[n];
		
		for (i=0; i<n; i++) {
			v[i] = col1.get(i+1);
			w[i] = col2.get(i+1);
		}
		
		// Print out inputs
//		for(int i1=0; i1<input.length; i1++) {
//             for(int j1=0; j1<input[0].length; j1++)
//                System.out.print(input[i1][j1] + " ");
//             System.out.println();
//        }
		
		// Initiate
		Item_t[] itens = new Item_t[n];
		for (i=0; i<n; i++) {
			itens[i] = new Item_t(v[i], w[i], v[i]/w[i]);
		}
		
		// Print out data structure
//		for (Item_t it : itens)
//			System.out.println(it.getV() + "  " + it.getW() + "  " + it.getD());
//		System.out.println("------------------------------");
		
		List<Item_t> list = new ArrayList<Item_t>(Arrays.asList(itens));
//		for (Item_t it : list)
//			System.out.println(it.getV() + "   " + it.getW() + "   " + it.getD());
		
		Collections.sort(list);

//		for (i=0; i<n; i++)
//			System.out.println(i + "   " + list.get(i).getV() + "   " + list.get(i).getW() + "   " + list.get(i).getD());
	
		int result = knapsack(n, M, list);
		
		System.out.println("Result: " + result);
	
	}
	
	static int greater(int x, int y) {
		return (x > y) ? x : y;
	}
	
	static int knapsack(int n, int M, List<Item_t> list) {

//		for (int j=0; j<list.size(); j++)
//			System.out.println("     " + list.get(j).getV() + "  " + list.get(j).getW());
		
		int v = 0, w = 0;
		int r = 0;

		if (n < 1)
			return 0;

		while (M - w >= 0) {
			//System.out.println("inside loop...");
			//r = greater(r, v + knapsack(n - 1, M - w, list.subList(1, list.size())));
			//System.out.println("v: " + v + "   ");
			int temp = knapsack(n - 1, M - w, list.subList(1, list.size()));
			//System.out.println("temp: " + temp);
			r = greater(r, temp+v);
			//System.out.println("r = " + r + "   n = " + n);
//			for (int j=0; j<list.size(); j++)
//				System.out.println("     " + list.get(j).getV() + "  " + list.get(j).getW());
			//System.out.println("     ---------------");
			v += list.get(0).getV();
			w += list.get(0).getW();
		}

		//System.out.println("--------------------");
		
		return r;
	}
}

class Item_t implements Comparable<Item_t> {
	int _value, _weight;
	float _density;
	
	public Item_t (int value, int weight, float density) {
		_value = value;
		_weight = weight;
		_density = density;
	}
	
	public int getV() {return _value;}
	public int getW() {return _weight;}
	public float getD() {return _density;}
	
	public int compareTo(Item_t it) {
		return (int) (_density - it._density); 
	}
}