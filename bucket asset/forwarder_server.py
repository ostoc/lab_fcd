import zmq
import random
import sys
import time

port = "5559"
context = zmq.Context()
socket = context.socket(zmq.PUB)
socket.connect("tcp://localhost:%s" % port)
publisher_id = random.randrange(0, 9999)
bucket = int()
while True:
    works = random.randrange(1, 199)
    work = int(works)
    if 0 < work < 100:
        bucket = 1
    elif 101 < work < 200:
        bucket = 2
    messagedata = "work is %s in %s bucket" % (work, bucket)
    print messagedata
    socket.send("%d %s" % (bucket, work))
    time.sleep(1)
