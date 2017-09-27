#!/usr/bin/env python3
# -*- coding:utf-8 -*-
import sys
import hashlib
import requests
from urllib import parse
######################
#https://mp.weixin.qq.com/s/graUxob6ItsZL-NTQh96Gw
#
#百度开发指南：http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-placeapi
#百度拾取坐标系统:http://api.map.baidu.com/lbsapi/getpoint/index.html
########################
class Baidudata(object):
    BASE_URL="https://api.map.baidu.com"
    BASE_SEARCH_URL="/place/v2/search?"

    def __init__(self,query,region=None,bounds=None,output="json",scope=1,page_size=20,page_num=0):
        self.__query=query
        self.__region = region
        self.__bounds=bounds
        self.__output=output
        self.__scope=scope
        self.__page_size=page_size
        self.__page_num=page_num
        self.__ak='5b563d231ae5ec8fb6af7ba4494d0d28'
        self.__sk='B08b392e55105b46b49f4e3c032dd4f4'
        self.__path=""
        self.__url=""
        ########+ "&coord_type=2&ret_coordtype=gcj02ll" 国家测绘局坐标
    def __sn(self):
        encodedStr = parse.quote(self.__path, safe="/:=&?#+!$,;'@()*[]")
        rawStr = encodedStr + self.__sk
        sn=hashlib.md5(parse.quote_plus(rawStr).encode("utf-8")).hexdigest()
        self.__path="%s&sn=%s" % (self.__path,sn)

    def build_url(self):
        self.__path= Baidudata.BASE_SEARCH_URL + "q=" +self.__query

        if(self.__bounds == None):
            if self.__region == None:
                print("bounds 和 region 不能同时为None")
                sys.exit(-1)
            self.__path = self.__path+"&region="+self.__region
        else:
            self.__path = self.__path+"&bounds="+self.__bounds    
            
        self.__path= "%s&output=%s&scope=%d&page_size=%d&page_num=%d&ak=%s" % (
                       self.__path,
                       self.__output,
                       self.__scope,
                       self.__page_size,
                       self.__page_num,
                       self.__ak)
        self.__sn()
        self.__url = Baidudata.BASE_URL+self.__path

    def getdata(self,file=None):
        self.build_url()
        #print(self.__url)
        data = requests.get(parse.quote(self.__url, safe="/:=&?#+!$,;'@()*[]"))
        
        if(data.json()["status"] != 0):
            print("error request:")
            print("\turl:%s" % self.__url)
            print("\tstatus:%d" % data.json()["status"])
            print("\tmessage:%s" % data.json()["message"])
            return
        for item in data.json()["results"]:
            #print(item["name"])
            line = "%s|%s|%s|%s|%s|%s|%s" % (item["name"], 
            item.get("address",""), 
            item["location"]["lat"],item["location"]["lng"],
            item.get("uid",""),
            item.get("street_id",""),
            item.get("telephone","")
            )
            print(line)
            if ( file != None and not file.closed ):
                file.write(line+"\n")
        ##total>=400的时候要拆分区域！
        if(data.json()["total"]>((self.__page_num+1)*self.__page_size)):
            print("total:%d,next" % data.json()["total"]) #如理>20还需要继续请求
            self.__page_num+=1
            self.getdata()

def fetch_by_bounds(query,start_min_lng,start_max_lng,start_min_lat,start_max_lat):
    file = open("e:/bmapdata.txt","w")
    for i in range(int(start_min_lng*100),int(start_max_lng*100),5):
        for j in range(int(start_min_lat*100),int(start_max_lat*100),5):
            #print(i/100.000,(i+5)/100.000,j/100.000,(j+5)/100.000) 
            bds=",".join([str(d) for d in (j/100.000,i/100.000,(j+5)/100.000,(i+5)/100.000)])
            Baidudata(query,bounds=bds).getdata(file)
    file.close()
if __name__ == '__main__':
    #Baidudata(query="牛不比",bounds="30.40,103.6,30.70,104.5").getdata()
    #Baidudata(query="清真",bounds="30.590156,104.010097,30.790156,104.080097").getdata()
    #Baidudata(query="东方宫",region="北京").getdata()
    start_min_lng=103.6000
    start_max_lng=104.5000
    start_min_lat=30.4000
    start_max_lat=30.7000
    fetch_by_bounds("清真寺",start_min_lng,start_max_lng,start_min_lat,start_max_lat)
