#include <stdio.h> 
#include <stdlib.h> 
#include <math.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <sys/types.h>
#include <sys/msg.h>
#include <string.h>
#include <sys/ipc.h>
#include <semaphore.h>
#include <sys/time.h>


#define pixel(x,y) pixels[((y)*size)+(x)]
#define MAX_MSG_SIZE 1024

#define MSG_TYPE_SEND_TASK 1
#define MSG_TYPE_FINISH_THREAD 2


int max_threads; // change the thread number here

int *pixels=0;

void *thread_function (void *ptr);

key_t key_send_task; //Key to send tasks from main thread to slaves
int msgqueue_id_send_task;//msg que for sending tasks from main thread to slaves

//key_t key_finish_task; //key to send finish from slave to main thread
//int msgqueue_id_finish_task; //key to send finish from slave to main thread



struct msgbuf_send_task {
        long mtype;  //Message type
		char mtext[MAX_MSG_SIZE];
        //int x_min, x_max; //x dimension constraints
				//int y_min, y_max; //y dimension constraints
};

sem_t mutex_finish_task, mutex_shared_data;



struct msgbuf_send_task *list_of_tasks=0; 

int no_of_completions = 0;

long long int size;

double time_diff(struct timeval x , struct timeval y)
{
    double x_ms , y_ms , diff;
     
    x_ms = (double)x.tv_sec*1000000 + (double)x.tv_usec;
    y_ms = (double)y.tv_sec*1000000 + (double)y.tv_usec;
     
    diff = (double)y_ms - (double)x_ms;
     
    return diff;
}

int main(int argc, char *argv[]){
      printf("Please enter the number of cores you want to use: ");
      scanf("%d", &max_threads);      
      FILE *in;
      FILE *out;
			int i, iret1;
			pthread_t thread_id[max_threads];

      in = fopen("image.in", "rb"); 
      if (in == NULL) {
            perror("image.in"); 
            exit(EXIT_FAILURE);
      }
      out = fopen("image.out", "wb"); 
      if (out == NULL) {
            perror("image.out"); 
            exit(EXIT_FAILURE);
      }

      long long int s, mid;
      int x, y;
      long long int a, d;
      double SQRT_2;
      int ret_r = fread(&size, sizeof(size), 1, in);  
      int ret_w = fwrite(&size, sizeof(size), 1, out);     
      pixels = (int *) malloc(size * size * sizeof(int)); 
			//int pixels[size*size];

      if (!fread(pixels, size * size * sizeof(int), 1, in)) { 
      perror("read error");
      exit(EXIT_FAILURE);
      }
			// fork thread 

			if ( sem_init (&mutex_finish_task,0,0) < 0 )
			{
				perror("sem_init");
				exit(1);
			}

			if ( sem_init (&mutex_shared_data,0,1) < 0 )
			{
				perror("sem_init");
				exit(1);
			}

			//Create message queues now...
			key_send_task = ftok(".", 'a');
			if(key_send_task <0)
			{
				perror("ftok");
				exit(1);
			}
     /* Open the queue - create if necessary */
      if((msgqueue_id_send_task = msgget(key_send_task, IPC_CREAT|0660)) == -1) {
                perror("msgget");
                exit(1);
        }


			//Allocate memory for list of tasks;
			list_of_tasks = (struct msgbuf_send_task *)malloc(sizeof(struct msgbuf_send_task) );
			if(list_of_tasks ==0)
			{
				printf("\n error allocating memory for list of tasks..");
			}

			//thread creation...
			for (i=0; i<max_threads; i++)
			{
				iret1 = pthread_create( &thread_id[i], NULL, thread_function, &i);

     		if(iret1)

     		{

         fprintf(stderr,"Error - pthread_create() return code: %d\n",iret1);

         exit(EXIT_FAILURE);
     		}

			}

      SQRT_2 = sqrt(4);
			struct timeval before , after;
	    gettimeofday(&before , NULL);


      for (s = size; s > 1; s /= 2) {
 
      mid = s / 2;
      // row-transformation
      for (y = 0; y < mid; y++) {
			list_of_tasks->mtype = MSG_TYPE_SEND_TASK;		
			sprintf (list_of_tasks->mtext, "%d %d %d %d %d", 0, (int)mid, y, y, (int)mid); 
			//x_min, x_max, y_min, y_max, mid
			//send a message to a task
			if((msgsnd(msgqueue_id_send_task, (struct msgbuf_send_task *)(list_of_tasks),
                strlen(list_of_tasks->mtext)+1, 0)) ==-1)
        {
                perror("msgsnd");
                exit(1);
        }

			/*
      for (x = 0; x < mid; x++) {
                   a = pixel(x,y);
                   a = (a+pixel(mid+x,y))/SQRT_2;
                   d = pixel(x,y);
                   d = (d-pixel(mid+x,y))/SQRT_2;
                   pixel(x,y) = a;
                   pixel(mid+x,y) = d;
            } 
    	*/
			}

			//Now we wait for all processes to finish their task
			for(y=0; y<mid; y++)
			{
				if(sem_wait(&mutex_finish_task) <0)
				{
					perror("sem_wait");
					exit(1);
				}
			}
      
      // column-transformation
			for (x = 0; x < mid; x++) {

				list_of_tasks->mtype = MSG_TYPE_SEND_TASK;

			sprintf (list_of_tasks->mtext, "%d %d %d %d %d", x, x, 0, (int)mid, (int)mid); //x_min, x_max, y_min, y_max, mid
			//send a message to a task
			if((msgsnd(msgqueue_id_send_task, (struct msgbuf_send_task *)(list_of_tasks),
                strlen(list_of_tasks->mtext)+1, 0)) ==-1)
        {
                perror("msgsnd");
                exit(1);
        }


			/*
      for (y = 0; y < mid; y++) {
      
                   a = pixel(x,y);
                   a = (a+pixel(x,mid+y))/SQRT_2;
                   d = pixel(x,y);
                   d = (d-pixel(x,mid+y))/SQRT_2;
                   pixel(x,y) = a;
                   pixel(x,mid+y) = d;
            }
			*/

			}
			//Now we wait for processes to finish the task
			for(x=0; x<mid; x++)
			{
				if(sem_wait(&mutex_finish_task) <0)
				{
					perror("sem_wait");
					exit(1);
				}
			}

      }
			gettimeofday(&after , NULL);
 
	    printf("\nTotal time elapsed : %.0lf us\n" , time_diff(before , after) );

      fwrite(pixels, size * size * sizeof(int), 1, out);
      free(pixels); 
      fclose(out); 
      fclose(in);

			printf("Thread %d, working...", max_threads);

			for (i=0; i <max_threads ; i++)
			{
				list_of_tasks->mtype = MSG_TYPE_FINISH_THREAD;



			if((msgsnd(msgqueue_id_send_task, (struct msgbuf_send_task *)(list_of_tasks),
                strlen(list_of_tasks->mtext)+1, 0)) ==-1)
        {
                perror("msgsnd");
                exit(1);
        }

			}


			for(i=0;i<max_threads; i ++)
			{
				pthread_join( thread_id[i], NULL);
			}
			// end time
      
printf("\nFinished\n");
return EXIT_SUCCESS;

      
}


void *thread_function(void * ptr)
{
 
	int x_min, x_max, y_min, y_max, mid;
	struct msgbuf_send_task rx_msg;
  int *temp_array = 0;
	int len_temp_array =0;
	int x,y, a,d;
  int SQRT_2 = 2;
  int index_temp_array = 0;
	if(len_temp_array < (size))
			  {
					temp_array  = (int *)realloc(temp_array, (size) );
					len_temp_array = size;
					
				}
	memset(temp_array, 0, (sizeof(int)) * len_temp_array); 

	while(1)
	{	
		if (msgrcv(msgqueue_id_send_task, &rx_msg, MAX_MSG_SIZE, 0, 0) < 0) {
        perror("msgrcv");
        exit(1);
    }
	switch (rx_msg.mtype){

	case MSG_TYPE_SEND_TASK:
		x_min = -1;
		x_max = -1;
		y_min = -1;
		y_max = -1;
    mid = -1;

		sscanf	(rx_msg.mtext, "%d %d %d %d %d", 	&x_min, &x_max, &y_min, &y_max, &mid);

    //put a sanity check to make sure they are all >0

		//Lock the shared data structure and make a copy	
		/*
		if(sem_wait(&mutex_shared_data) <0){
			perror("sem_wait:");
			exit(0);
		}
		*/

		//make a copy of data struct
		/*
		index_temp_array = 0;
		
		if(x_min==x_max)
			for (y=y_min; y<size; y++)
				{
					temp_array[index_temp_array] = pixel(x_min,y);
					index_temp_array++;
				}						
		if(y_min==y_max)
			for (x=x_min; x<size; x++)
				{
					temp_array[index_temp_array] = pixel(x,y_min);
					index_temp_array++;
				}		
		*/
		//unlock the data structure		
		/*
		if(sem_post(&mutex_shared_data) <0){
			perror("sem_post:");
			exit(0);
		}
		*/
		/*
    for (x = 0; x < mid; x++) {
          a = temp_array[x];
          a = (a+temp_array[mid+x])/SQRT_2;
          d = temp_array[x];
          d = (d-temp_array[mid+x])/SQRT_2;
          temp_array[x] = a;
          temp_array[mid+x] = d;
            } 
		*/
		if(y_min == y_max){
					y = y_min;
	       for (x = 0; x < mid; x++) {
                   a = pixel(x,y);
                   a = (a+pixel(mid+x,y))/SQRT_2;
                   d = pixel(x,y);
                   d = (d-pixel(mid+x,y))/SQRT_2;
                   pixel(x,y) = a;
                   pixel(mid+x,y) = d;
            }
			}
		if(x_min == x_max){
				x=x_min;
				for (y = 0; y < mid; y++) {
      
                   a = pixel(x,y);
                   a = (a+pixel(x,mid+y))/SQRT_2;
                   d = pixel(x,y);
                   d = (d-pixel(x,mid+y))/SQRT_2;
                   pixel(x,y) = a;
                   pixel(x,mid+y) = d;
            }	 
			}
		//Lock the shared data structure and make a copy
		/*		
		if(sem_wait(&mutex_shared_data) <0){
			perror("sem_wait:");
			exit(0);
		}
		*/
		//copy the data structures back to main..
		/*	  
		index_temp_array = 0;
		if(x_min==x_max)
			for (y=y_min; y<size; y++)
				{
					pixel(x_min,y) = temp_array[index_temp_array] ;
					index_temp_array++;
				}						
		if(y_min==y_max)
			for (x=x_min; x<size; x++)
				{
					pixel(x,y_min) = temp_array[index_temp_array] ;
					index_temp_array++;
				}		

		*/
		//unlock the data structure		
		/*		
		if(sem_post(&mutex_shared_data) <0){
			perror("sem_post:");
			exit(0);
		}
		*/

		if(sem_post(&mutex_finish_task) <0)
				{
					perror("sem_wait");
					exit(1);
				}

		break;

	case 	MSG_TYPE_FINISH_THREAD:
		return NULL;

   default:
		printf("\n Unrecognized message %d, MsgData = %s", (int)rx_msg.mtype, rx_msg.mtext);
		return NULL;
	}
}
}
