__author__ = 'Junyu'

import multiprocessing

nThread = 4

with open('test.in', 'rb') as in_file:
    works = in_file.readlines()

inputs = []

for lines in works:
    inputs.append(lines.split())
# define the number of clauses
nClause = int(inputs[0][0])

# slice the first element
clauses = inputs[1:]

# todo: send clause to other clients


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



def solveClause(number):
    c = 0
    for c in range(0, nClause):
        var = long(clauses[c][0])
        if var > 0 and (number & cmp[var - 1]) > 0:
            continue    # clause true
        elif var < 0 and (number & cmp[-var - 1]) == 0:
            continue

        var = int(clauses[c][1])
        if var > 0 and (number & cmp[var - 1]) > 0:
            continue
        elif var < 0 and (number & cmp[-var - 1]) == 0:
            continue

        var = int(clauses[c][2])
        if var > 0 and (number & cmp[var - 1]) > 0:
            continue
        elif var < 0 and (number & cmp[-var - 1]) == 0:
            continue
        break   # clause false
    return c


def worker(start_n, end_n):
    # todo: distribute number of work to workers
    for n in range(start_n, end_n):
        if solveClause(n) == nClause - 1:
            return n + 1
    return -1
    print(n)



if __name__ == "__main__":
    work_n = nMax / nThread
    p = multiprocessing.Process(target=worker, args=(0, 8))
    p.start()

    print(p)


    if sink > 0:
        print('solution is [%s]:' % sink),
        for i in range(0, nVal):
            res = (sink & pow(2, i)) / pow(2, i)
            print(res),
    else:
        print('solution no found')
