import time
timestop1 = time.clock()
with open('bucketsort.in', 'r') as content_file:
    content = content_file.readlines()
continer = []
aa = []
for line in content:
    continer.append(line.split())
timestop2 = time.clock()
timeslot1 = timestop2 - timestop1
print("reading time is %s" % timeslot1)
continer.sort()
timestop3 = time.clock()
timeslop2 = timestop3 - timeslot1
print("sort tiem is %s" % timeslop2)

#for line in range(0,len(continer)):
#    print(continer[line])
