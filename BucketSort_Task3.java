package fcds_lab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BucketSort_Task3 {

	ArrayList _bucket;
	
	BucketSort_Task3(ArrayList bucket) {
		_bucket = bucket;
	}
	
	void sort() {
		//ArrayList<String> buck = new ArrayList<String>();
		Collections.sort(_bucket, String.CASE_INSENSITIVE_ORDER);
	}
}
