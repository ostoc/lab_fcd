
 ************************************************************
 *                                                          *
 *    Foundations of Concurrent and Distributed Systems     *
 *                                                          *
 *                           LAB                            *
 *                                                          *
 *                                                          *
 *                  Team: Xi Luo, Junyu Pu                  *
 *                                                          *
 *                        July 2014                         *
 *                                                          *
 ************************************************************



 ********** Description **********

 This lab is to use concurrent programming to solve 5 tasks
 assigned in the 7th Marathon of Parallel Programming, 2012

 Note: all codes have been successfully compiled and tested.
 Source code available: https://github.com/pujunyu/fcd



 ********** Test Instruction **********


 ----- Task 1: Bucket Sort -----

 1. Test steps:

    - Go inside the path:
      '.../xi_junyu/bucket_sort/'
    - Run the Bucketsort_Thread.java code:
      '.../xi_junyu/bucket_sort$java BucketSort_Thread'
    - Type in the number of cores you want to use for test
      (1, 2, 4, 8 and 16 etc), for example:
      'Please enter the number of cores you want to use: 4'

 2. Check result:

    The program will print out the results, including number
    of threads used, time consumed and a message of 
    successfully outputing the result file. The output file 
    Bucketsort.out can be found in current path.

 3. Test other input:

    Upload your new test file onto the current path, and 
    change the 29th line code of the BucketSort_Thread.java
    file:
    'String[] words = read_file("YourFileName.in");'
    Then re-compile BucketSort_Thread.java and run it again.


 ----- Task 2: Friendly Numbers -----
 
 1. Test steps:

    - Go inside the path:
      '.../xi_junyu/friendly_numbers/'
    - Run the Friendly_Thread.java code:
      '.../xi_junyu/friendly_numbers$java Friendly_Thread'
    - Type in the number of cores you want to use for test (1,
      2, 4, 8 and 16 etc), for example:
      'Please enter the number of cores you want to use: 4'
    - Type in the range of numbers in which you want to find
      friendly numbers, for example 1 to 262143:
      'Enter the start number: 1'
      'Enter the end number: 262143'

 2. Check result:
 
    The program will print out the results, including number
    of threads used, time consumed and the number of friendly
    number pairs found.

    Note that the current program is set defaultly to only
    print out the number of friendly numbers found. If you
    want to see all the friendly numbers found, simply
    un-comment the 192nd and 199th lines of code in
    Friendly_Thread.java, both are the same:
    'System.out.println(_all.get(i).getNo() + " and " + 
     _all.get(j).getNo() + " are FRIENDLY");'
    But in this case the measured time is not trustable
    because it also measures the time used for printing.


 ----- Task 3: Haar Wavelets -----

 1. Test steps:
    - Go inside the path:
      '../xi_junyu/haar_transform/'
    - Run 'run_haar_tran':
      './run_haar_tran'
    - Type in the number of cores you want to use for test
      (1, 2, 4, 8 and 16 etc), for example:
      'Please enter the number of cores you want to use: 4'

 2. Check result:
    The program will write the result in 'image.out' file.
    Use diff to compare with the ¡®image.out¡¯ which calculated by 
    the sequential code.


 ----- Task 4: Unbounded Knapsack -----

 1. Test steps:

    - Go inside the path:
      '.../xi_junyu/knapsack/'
    - Run the Knapsack_Thread.java code:
      '.../xi_junyu/knapsack$java Knapsack_Thread'
    - Type in the number of cores you want to use for test
      (1, 2, 4, 8 and 16 etc), for example:
      'Please enter the number of cores you want to use: 4'

 2. Check result:

    The program will print out the results, including number
    of threads used, the optimized result and time consumed.

 3. Test other input:

    Upload your new test file onto the current path, and 
    change the 22nd line code of the Knapsack_Thread.java
    file:
    'Scanner input = new Scanner(new File("YourFileName.in"));'
    Then re-compile Knapsack_Thread.java and run it again.


 ----- Task 5: 3 SAT -----

 1. Test steps:

    - Go inside the path:
      ¡®../xi_junyu/3sat¡¯
    - Run the c_3sat.py code:
      ¡®python c_3sat.py¡¯
    - Type in the number of cores you want to use for test
      (1, 2, 4, 8 and 16 etc), for example:
      ¡®Number of core: 4'

 2. Check result:

    The program will print out the results, including number
    of threads used, the optimized result and time consumed.



 ********** Sample Test Result **********

 Tests are conducted on a 8-core machine of the server:
 fcdsrl08.zih.tu-dresden.de

 Test cases:
 Bucket Sort: test file with 2000039 unsorted strings
 Friendly Number: 1 to 262143
 Haar Wavelets: image.in file generated with size 0x600
 Knapsack: test file with the 120 entries
 3 SAT: 125 clause, 24 values in 3sat.in

 Time consumed in 5 tasks (average of 5 times experiments in ms):

 No. of Threads   |       1 |      2 |     4 |     8 |    16
                  |         |        |       |       |
 Bucket Sort      |    1553 |    885 |   559 |   520 |   576   
                  |         |        |       |       |  
 Friendly Number  |  192105 | 111493 | 75482 | 47628 |  58475
                  |         |        |       |       |
 Haar Wavelets    |   95642 |  92420 | 90168 | 88558 |  87709
                  |         |        |       |       |
 Knapsack         |  201459 | 123960 | 90056 | 91030 |  91962
                  |         |        |       |       |
 3 SAT            |  175981 |  94769 | 48067 | 24796 |  23216       