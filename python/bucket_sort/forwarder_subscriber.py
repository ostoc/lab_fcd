import zmq


port = "5560"
# Socket to talk to server
context = zmq.Context()
socket = context.socket(zmq.PULL)
print "Collecting updates from server..."
socket.connect ("tcp://localhost:%s" % port)

string = socket.recv()
bs = eval(string)
bs.sort()
print string
print bs
socket.send = ('%s' % bs)

