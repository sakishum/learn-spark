#!/usr/bin/env python
# -*- coding:utf-8 -*-
start_min_lng=103.6
start_max_lng=104.5

start_min_lat=30.4
start_max_lat=30.7



#for i in range(int(start_min_lng*100),int(start_max_lng*100),5):
for j in range(int(start_min_lat*100),int(start_max_lat*100),5):
        print(j*0.01)
        #print(",".join([str(d) for d in (j*0.1,i*0.1,(j+5)*0.1,(i+5)*0.1)]))
