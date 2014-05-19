import sys
import zmq

port = "5560"
# Socket to talk to server
context = zmq.Context()
socket = context.socket(zmq.SUB)
print "Collecting updates from server..."
socket.connect ("tcp://localhost:%s" % port)
bucket = "1"
socket.setsockopt(zmq.SUBSCRIBE, bucket)
while True:
    string = socket.recv()
    bucket, tasks = string.split()
    print(tasks)