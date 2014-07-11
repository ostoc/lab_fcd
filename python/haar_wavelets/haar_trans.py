__author__ = 'Junyu'

import zmq
import sys
import struct
import math

with open('a.in', 'rb') as in_file:
    filecontent = in_file.readlines()
pixels = []
for line in filecontent:
    pixels.append(line.split())

# define pixel(x,y) pixels[((y)*size)+(x)]

sqrt2 = math.sqrt(4)
size = 100
def pixel(x, y):
    return pixels[y * size + x]

def row_trans(y):    # the column value doesnt change during this trans, each worker get N col
    mid = size / 2
    for y in range(0, y):
        for x in range(0, size):
            a = pixel(x, y)
            a = (a+pixel(mid+x, y))/sqrt2
            d = pixel(x, y)
            d = (d-pixel(mid+x, y))/sqrt2




print(piexs)