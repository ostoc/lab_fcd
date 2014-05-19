import zmq
import random
import sys
import time

port = "5559"
context = zmq.Context()
socket = context.socket(zmq.PUSH)
socket.connect("tcp://localhost:%s" % port)
socket.connect("tcp://localhost:5565")
timestop1 = time.clock()
with open('test', 'r') as resouces:
    works = resouces.readlines()
tasks = []
for line in works:
    tasks.append(line.split())
timestop2 = time.clock()
timeslot1 = timestop2 - timestop1
print("reading time is %s" % timeslot1)

nbucket = 3
bsize = len(tasks) / nbucket
# define the bucket parameters
buckets = [[] for x in range(0, nbucket)]
split = 256 / nbucket
for i in range(0, len(tasks)):
    for j in range(0, nbucket):
        if split * (j - 1) < ord(tasks[i][0][0]) < split * j:
            buckets[j].append(tasks[i][0])
        else:
            ++j
##for i in range(nbucket):
##    buckets[i].sort()
server_message = "sending bucket"
print server_message


while True:
    socket.send('%s' % buckets[1])
    time.sleep(1)
    socket.send('%s' % buckets[2])
    time.sleep(1)
    socket.send('%s' % buckets[0])
    time.sleep(1)