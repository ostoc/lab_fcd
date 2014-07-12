package friendly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Friendly_Thread {

	public static void main(String[] args) throws Exception {
		int start = 1;
		int end = 262143;
		int size = end-start+1;
		int count_size = 0;
		int threadNo = 4;
		ArrayList<Pair> all = new ArrayList<Pair>();
		
		long time1 = System.currentTimeMillis();
		
		/* Parallelize 1st part */
		ExecutorService es = Executors.newFixedThreadPool(threadNo);
		
		List<Callable<ArrayList<Pair>>> lst = new ArrayList<Callable<ArrayList<Pair>>>();
		
		if (size < 10000)
			lst.add(new ExecuteCallable(start, end));
		else {
			for (int i=0; i<=(size/10000); i++) {
				if (i == size/10000)
					lst.add(new ExecuteCallable(start+i*10000, end));
				else
					lst.add(new ExecuteCallable(start+i*10000, start+i*10000+9999));
			}
		}
		
		List<Future<ArrayList<Pair>>> results = es.invokeAll(lst);
		
		for (Future<ArrayList<Pair>> result : results) {
			for (int i=0; i<result.get().size(); i++) {
				all.add(new Pair(result.get().get(i).getNo(), result.get().get(i).getNum(), result.get().get(i).getDen()));
			}
		}
		
		es.shutdown();
		/* Parallelize 1st part */
		
//		for (int i=0; i<size; i++) {
//		for (int j=i+1; j<size; j++)
//			if (all.get(i).getNum()==all.get(j).getNum() && all.get(i).getDen()==all.get(j).getDen())
//				//System.out.println(all.get(i).getNo() + " and " + all.get(j).getNo() + " are FRIENDLY");
//				count_size++;
//		}
		
		/* Parallelize 2nd part */
		ExecutorService es2 = Executors.newFixedThreadPool(threadNo);

		List<Callable<Integer>> lst2 = new ArrayList<Callable<Integer>>();
		
		if (size < 10000)
			lst2.add(new ExecuteFind(all, 0));
		else {
			if (size % 10000 == 0) {
				for (int i=0; i<(size/10000); i++)
					lst2.add(new ExecuteFind(all, i));
			} else {
				for (int i=0; i<=(size/10000); i++)
					lst2.add(new ExecuteFind(all, i));
			}
		}
		
		
		List<Future<Integer>> ssum = es2.invokeAll(lst2);
		
		int sum = 0;
		for (Future<Integer> itg : ssum)
			sum += itg.get();
		
		es2.shutdown();
		/* Parallelize 2nd part */
		
		System.out.println("Friendly with " + threadNo + " threads finish.");

		System.out.println(sum + " Friendly pairs found.");
		
		long time2 = System.currentTimeMillis();
		
		System.out.println("\nTime used: " + (time2-time1) + "ms");
	}
	
}

class Pair {	
	int _no, _num, _den;
	
	Pair(int no, int num, int den) {
		_no = no;
		_num = num;
		_den = den;
	}

	public int getNo() {return _no;}
	public int getNum() {return _num;}
	public int getDen() {return _den;}
	
	public static ArrayList<Pair> calculate (int start, int end) {
		int size = end - start + 1;
		int count_size = 0;
		int num_temp, den_temp, i, j, factor, ii, sum, n, done;
		ArrayList<Pair> alp = new ArrayList<Pair>();
		
		for (i = start; i <= end; i++) {
			ii = i - start;
			sum = 1 + i;
			done = i;
			factor = 2;
			while (factor < done) {
				if ((i % factor) == 0) {
					sum += (factor + (i / factor));
					if ((done = i / factor) == factor)
						sum -= factor;
				}
				factor++;
			}
			num_temp = sum;
			den_temp = i;
			n = gcd(num_temp, den_temp);
			num_temp /= n;
			den_temp /= n;
			
			alp.add(new Pair(i, num_temp, den_temp));
		}
		
		return alp;
	}
	
	static int gcd(int u, int v) {
		if (v == 0)
			return u;
		return gcd(v, u % v);
	}
}

// Thread for 1st part
class ExecuteCallable implements Callable<ArrayList<Pair>> {
	
	private int _no1, _no2;

	public ExecuteCallable(int no1, int no2) {
		_no1 = no1;
		_no2 = no2;
	}
	
	public ArrayList<Pair> call() throws Exception {		
		return Pair.calculate(_no1, _no2);
	}
}

//Thread for 2nd part
class ExecuteFind implements Callable {
	ArrayList<Pair> _all;
	int _block, count_size;

	ExecuteFind (ArrayList<Pair> all, int block) {
		_all = all;
		_block = block;
	}

	public Integer call() {
		int size = _all.size();
		int i, j;
		
		if (_block == size / 10000) {
			for (i=_block*10000; i<size; i++)
				for (j=i+1; j<size; j++)
					if (_all.get(i).getNum()==_all.get(j).getNum() && _all.get(i).getDen()==_all.get(j).getDen()) {
						//System.out.println(_all.get(i).getNo() + " and " + _all.get(j).getNo() + " are FRIENDLY");
						count_size++;
					}
		} else {
			for (i=_block*10000; i<_block*10000+9999; i++)
				for (j=i+1; j<size; j++)
					if (_all.get(i).getNum()==_all.get(j).getNum() && _all.get(i).getDen()==_all.get(j).getDen())
						//System.out.println(_all.get(i).getNo() + " and " + _all.get(j).getNo() + " are FRIENDLY");
						count_size++;
		}

		return count_size;
	}
}