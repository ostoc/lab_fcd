__author__ = 'Junyu'

import zmq
import time
from multiprocessing import Process

nThread = raw_input('Number of threads:')
nThread = int(nThread)

with open('3sat.in', 'rb') as in_file:
    works = in_file.readlines()

inputs = []

for lines in works:
    inputs.append(lines.split())
# define the number of clauses
nClause = int(inputs[0][0])

# slice the first element
clauses = inputs[1:]

# get the number of different values, N
d = sum(clauses, [])
d = map(int, d)
for i in range(0, len(d)):
    d[i] = abs(d[i])
nVal = len(set(d))


# create a compare table depends on the biggest value
nCmp = max(d)
cmp = []
for i in range(0, nCmp):
    cmp.append(pow(2, i))

# define the max circle for finding solution
nMax = pow(2, nVal)

print('it has %d Clauses, has %d Values, max loops %d' % (nClause, nVal, nMax))
time1 = time.time();

def ventilator():
    context = zmq.Context()

    # Set up a channel to send work, port = 1557
    ventilator_send = context.socket(zmq.PUSH)
    ventilator_send.bind("tcp://127.0.0.1:5557")

    # Give everything a second to spin up and connect
    time.sleep(1)

    # Publish the clause
    for n in range(nThread):
        work_message = clauses
        ventilator_send.send_json(work_message)

    time.sleep(1)


def worker(wrk_num):

    # Initialize a zeromq context
    context = zmq.Context()

    # Set up a channel to receive work from the ventilator
    work_receiver = context.socket(zmq.PULL)
    work_receiver.connect("tcp://127.0.0.1:5557")

    # Set up a channel to send result of work to the results reporter
    results_sender = context.socket(zmq.PUSH)
    results_sender.connect("tcp://127.0.0.1:5558")

    # Set up a channel to receive control messages over
    control_receiver = context.socket(zmq.SUB)
    control_receiver.connect("tcp://127.0.0.1:5559")
    control_receiver.setsockopt(zmq.SUBSCRIBE, "")

    # Set up a poller to multiplex the work receiver and control receiver channels
    poller = zmq.Poller()
    poller.register(work_receiver, zmq.POLLIN)
    poller.register(control_receiver, zmq.POLLIN)


    socks = dict(poller.poll())

    # If solve the clause
    if socks.get(work_receiver) == zmq.POLLIN:
        work_message = work_receiver.recv_json()
        clauses = work_message
        start_n = nMax / nThread * wrk_num
        end_n = nMax / nThread * (wrk_num + 1) - 1
        print ('Worker %d is working form %d to %d' % (wrk_num, start_n, end_n))
        c = 0
        for n in range(start_n, end_n):
            for c in range(0, nClause):
                var = int(clauses[c][0])
                if var > 0 and (n & cmp[var - 1]) > 0:
                    continue    # clause true
                elif var < 0 and (n & cmp[-var - 1]) == 0:
                    continue

                var = int(clauses[c][1])
                if var > 0 and (n & cmp[var - 1]) > 0:
                    continue
                elif var < 0 and (n & cmp[-var - 1]) == 0:
                    continue

                var = int(clauses[c][2])
                if var > 0 and (n & cmp[var - 1]) > 0:
                    continue
                elif var < 0 and (n & cmp[-var - 1]) == 0:
                    continue
                break   # clause false

            if c == nClause - 1:
                answer_message = {'result': n + 1, 'worker': wrk_num}
                results_sender.send_json(answer_message)
                return

        answer_message = {'result': -1, 'worker': wrk_num}
        results_sender.send_json(answer_message)

    # If the message came over the control channel, shut down the worker.
    if socks.get(control_receiver) == zmq.POLLIN:
        control_message = control_receiver.recv()
        if control_message == "FINISH":
            print("Worker %i received FINISH, quitting!" % wrk_num)
    






def result_manager():
    # Initialize a zeromq context
    context = zmq.Context()

    # Set up a channel to receive results
    results_receiver = context.socket(zmq.PULL)
    results_receiver.bind("tcp://127.0.0.1:5558")

    # Set up a channel to send control commands, port 5559
    control_sender = context.socket(zmq.PUB)
    control_sender.bind("tcp://127.0.0.1:5559")


    for k in range(nThread):
        result_message = results_receiver.recv_json()
        if result_message['result'] > 0:
            print "Worker %i found solution: [%i]" % (result_message['worker'], result_message['result']),
            for i in range(0, nVal):
                res = (result_message['result'] & pow(2, i)) / pow(2, i)
                print(res),

            break
        else:
            print("Work %i finish Job and no result found" % (result_message['worker']))
            continue
    control_sender.send("FINISH")
    print('\nShut down the workers')
    time2 = time.time()
    print(time2 - time1)
    time.sleep(3)

if __name__ == "__main__":
   
    # Create a pool of workers to distribute work to
    for wrk_num in range(nThread):
        Process(target=worker, args=(wrk_num,)).start()

    # Fire up our result manager...
    result_manager = Process(target=result_manager, args=())
    result_manager.start()

    # Start the ventilator!
    ventilator = Process(target=ventilator, args=())
    ventilator.start()
