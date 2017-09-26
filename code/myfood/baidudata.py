#!/usr/bin/env python
# -*- coding:utf-8 -*-
import sys
import hashlib
import requests
######################
#https://mp.weixin.qq.com/s/graUxob6ItsZL-NTQh96Gw
#
#百度开发指南：http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-placeapi
#百度拾取坐标系统:http://api.map.baidu.com/lbsapi/getpoint/index.html
########################



class Baidudata(object):
    BASE_URL="https://api.map.baidu.com"
    BASE_SEARCH_URL="/place/v2/search?"

    def __init__(self,query,bounds=None,output="json",scope=1,page_size=20,page_num=0):
        self.__query=query 
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
        from urllib import parse
        encodedStr = parse.quote(self.__path, safe="/:=&?#+!$,;'@()*[]")
        rawStr = encodedStr + self.__sk
        sn=hashlib.md5(parse.quote_plus(rawStr).encode("utf-8")).hexdigest()
        self.__path="%s&sn=%s" % (self.__path,sn)

    def build_url(self):
        #self.__path=baidudata.BASE_SEARCH_URL+"q="+query \
        #      + "&bounds="+bounds \
        #      + "&output=json" \
        #      + "&page_size=20" \
        #      + "&ak=" + BAIDU_AK
        self.__path= "%sq=%s&bounds=%s&output=%s&scope=%d&page_size=%d&page_num=%d&ak=%s" % (Baidudata.BASE_SEARCH_URL,
                       self.__query,
                       self.__bounds,
                       self.__output,
                       self.__scope,
                       self.__page_size,
                       self.__page_num,
                       self.__ak)
        self.__sn()
        self.__url = Baidudata.BASE_URL+self.__path

    def getdata(self):
        self.build_url()
        #print(self.__url)
        data = requests.get(self.__url)
        
        if(data.json()["status"] != 0):
            print("error request:")
            print("\turl:%s" % self.__url)
            print("\tstatus:%d" % data.json()["status"])
            print("\tmessage:%s" % data.json()["message"])
            return
        for item in data.json()["results"]:
            #print(item["name"])
            print("%s|%s|%s|%s|%s|baidu" % 
            (item["name"], 
            item["address"], 
            item["location"]["lat"],item["location"]["lng"],
            item["uid"]
            )) 
        ##total>=400的时候要拆分区域！
        #print("total：%d" % data.json()["total"])
        if(data.json()["total"]>((self.__page_num+1)*self.__page_size)):
            print("total:%d,next" % data.json()["total"]) #如理>20还需要再请求一次
            self.__page_num+=1
            self.getdata()

if __name__ == '__main__':
    #Baidudata(query="清真",bounds="30.40,103.6,30.70,104.5").getdata()
    start_min_lng=103.6
    start_max_lng=104.5
    start_min_lat=30.4
    start_max_lat=30.7
    for i in range(int(start_min_lng*100),int(start_max_lng*100),5):
        for j in range(int(start_min_lat*100),int(start_max_lat*100),5):
            bds=",".join([str(d) for d in (j*0.01,i*0.01,(j+5)*0.01,(i+5)*0.01)])
            Baidudata(query="清真",bounds=bds).getdata()
            