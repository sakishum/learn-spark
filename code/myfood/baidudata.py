#!/usr/bin/env python
# -*- coding:utf-8 -*-
import hashlib
import requests
######################
#https://mp.weixin.qq.com/s/graUxob6ItsZL-NTQh96Gw
#
#百度开发指南：http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-placeapi
#百度拾取坐标系统:http://api.map.baidu.com/lbsapi/getpoint/index.html
########################
BAIDU_AK='5b563d231ae5ec8fb6af7ba4494d0d28'
BAIDU_SK='B08b392e55105b46b49f4e3c032dd4f4'
BASE_URL="https://api.map.baidu.com"
BASE_SEARCH_URL="/place/v2/search?"

class baidudata(object):
    def __init__(self,query,bounds):
        self.path=BASE_SEARCH_URL+"q="+query \
              + "&bounds="+bounds \
              + "&output=json" \
              + "&page_size=20" \
              + "&ak=" + BAIDU_AK

               ########+ "&coord_type=2&ret_coordtype=gcj02ll" 国家测绘局坐标
    def __sk(self):
        from urllib import parse
        encodedStr = parse.quote(self.path, safe="/:=&?#+!$,;'@()*[]")
        rawStr = encodedStr + BAIDU_SK
        sn=hashlib.md5(parse.quote_plus(rawStr).encode("utf-8")).hexdigest()
        self.path=self.path+"&sn="+sn
    def build_url(self):
        self.__sk()
        self.data_url = BASE_URL+self.path

    def getdata(self):
        self.build_url()
        print("data url:%s" % self.data_url)
        data = requests.get(self.data_url)
       
        if(data.json()["status"] != "0"):
            print(data.json()["status"])
            return;
        #print(data.json()["message"])
        print("total:"+ data.json()["total"]) #如理>20还需要再请求一次
        for item in data.json()["results"]:
            #print(item["name"])
            print("%s|%s|%s|%s|%s|baidu" % 
            (item["name"], 
            item["address"], 
            item["location"]["lat"],item["location"]["lng"],
            item["uid"]
            )) 

if __name__ == '__main__':
    baidudata(query="清真",bounds="bds").getdata()
    start_min_lng=103.6
    start_max_lng=104.5

    start_min_lat=30.4
    start_max_lat=30.7
    for i in range(int(start_min_lng*100),int(start_max_lng*100),5):
        for j in range(int(start_min_lat*100),int(start_max_lat*100),5):
            print(i*0.01,(i+5)*0.01,j*0.01,(j+5)*0.01)
            bds=",".join([str(d) for d in (j*0.01,i*0.01,(j+5)*0.01,(i+5)*0.01)])
            #baidudata(query="清真",bounds=bds).getdata()
