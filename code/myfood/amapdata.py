#!/usr/bin/env python
# -*- coding:utf-8 -*-
import sys
import requests
##
## @author migle 2017-09-27 00:27:50
##http://restapi.amap.com/v3/place/text?key=eaaf1cd5acfefbae2030f5f5cba85626&keywords=%E6%B8%85%E7%9C%9F%E7%81%AB%E9%94%85&types=&city=%E6%88%90%E9%83%BD&children=1&offset=20&page=1&extensions=base

class AMapdata:
    baseurl="http://restapi.amap.com/v3/place/text?"
    def __init__(self,key,keywords,page_size=20,page_num=1):
        self.__key=key
        self.__keywords = keywords
        self.__page_size = page_size
        self.__page_num = page_num
        self.__url=""
    def __sig(self):
        pass

    def build_url(self):
        #self.__url =  "http://restapi.amap.com/v3/place/text?key=eaaf1cd5acfefbae2030f5f5cba85626&keywords=%E6%B8%85%E7%9C%9F%E7%81%AB%E9%94%85&types=&city=%E6%88%90%E9%83%BD&children=1&offset=20&page="+str(self.__page_num)+"&extensions=base"
        self.__url =  "http://restapi.amap.com/v3/place/text?key=eaaf1cd5acfefbae2030f5f5cba85626&keywords=清真&types=&city=%E6%88%90%E9%83%BD&children=1&offset=20&page="+str(self.__page_num)+"&extensions=base"
    def getdata(self):
        self.build_url()
        print(self.__url)
        data = requests.get(self.__url)
    
        #print('*'*20)
        #print(data.json())
        print("-"*20)
        #print(data.json()["info"])
        if(data.json()["status"] != '1'):
            print("error request:")
            print("\turl:%s" % self.__url)
            print("\tstatus:%d" % data.json()["status"])
            print("\tmessage:%s" % data.json()["info"])
            return
        
        for item in data.json().get('pois',""):
            print(item.get('name',''))

        if(int(data.json()["count"]) > self.__page_num*self.__page_size ):
            print("total:%s,next" % data.json()["count"]) #如理>page_size还需要再请求一次
            self.__page_num+=1
            self.getdata()

if __name__ == '__main__':    
    key="eaaf1cd5acfefbae2030f5f5cba85626"
    keywords="清真"
    amap = AMapdata(key,keywords)
    amap.getdata()
