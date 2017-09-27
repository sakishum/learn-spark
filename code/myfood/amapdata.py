#!/usr/bin/env python3
# -*- coding:utf-8 -*-
import sys
import logging
import requests

logging.basicConfig(
                level=logging.DEBUG,
                format='%(asctime)s [line:%(lineno)d] %(levelname)s %(threadName)s %(message)s',
                datefmt='%Y-%m-%d %H:%M:%S')

ch = logging.StreamHandler() 
ch.setLevel(logging.DEBUG)

ch2 = logging.FileHandler("amappoidata.log")
ch.setLevel(logging.DEBUG)

logger = logging.getLogger(__name__)

logger.addHandler(ch)
logger.addHandler(ch2)

####################################################
##
## @author migle 2017-09-27 00:27:50
##http://lbs.amap.com/api/webservice/guide/api/search
##
## TODO: 自动换key,代理,数字签名
##
#####################################################

class AMapPoidata:
    baseurl="http://restapi.amap.com/v3/place/text?"
    def __init__(self,key,keywords,city='',types='',region='',page_size=10,page_num=1,extensions='all',output='JSON'):
        """参数说明:http://lbs.amap.com/api/webservice/guide/api/search#text"""
        self.__key=key
        self.__keywords = keywords
        self.__types=types
        self.__city=city
        self.__page_size = page_size
        self.__page_num = page_num
        self.__extensions = extensions #all/base
        self.__output=output
        self.__payload ={}

    def __sig(self):
        ##没有sig也可以！
        pass

    def build_url(self):
        #self.__url =  "http://restapi.amap.com/v3/place/text?key=eaaf1cd5acfefbae2030f5f5cba85626&keywords=%E6%B8%85%E7%9C%9F%E7%81%AB%E9%94%85&types=&city=%E6%88%90%E9%83%BD&children=1&offset=20&page="+str(self.__page_num)+"&extensions=base"
        self.__payload ={'key'      :    self.__key,
               'keywords' :    self.__keywords,
               'types'    :    self.__types,
               'city'     :    self.__city,
               'offset'   :    self.__page_size,
               'page'     :    self.__page_num,          ##最大翻页数100
               'extensions'    :    self.__extensions,
               'output'    :self.__output}
    
    def getdata(self,callback):
        self.build_url()
        data = requests.get(AMapPoidata.baseurl,self.__payload)
        logging.info(data.url)
        print("-"*20)
        #print(data.json()["info"])
        if(data.json()["status"] != '1'):
            logging.error("error request! url:%s status:%s infocode:%s message:%s",
                    data.url,data.json()["status"],
                    data.json()["infocode"],
                    data.json()["info"])
            
            #key不正确或过期
            if(data.json()["infocode"] == "10001"):
                logger.error("key不正确或过期")

            #10003 DAILY_QUERY_OVER_LIMIT 访问已超出日访问量
            elif(data.json()["infocode"] == "10003"):
                logger.error("访问已超出日访问量，需要换key")

            #10004 ACCESS_TOO_FREQUENT 单位时间内访问过于频繁
            elif(data.json()["infocode"] == "10001"):
                logger.error("单位时间内访问过于频繁,要等等")
            else:
                pass
            
            return
        
        for item in data.json().get('pois',""):
            line=self.__format_item(item)
            #print(line)
            callback(line)
            #if(file != None and not file.closed):
            #    file.write(line+"\n")

        if(int(data.json()["count"]) > self.__page_num*self.__page_size ):
            logging.debug("total:%s,next" % data.json()["count"]) #如理>page_size还需要再请求一次
            self.__page_num+=1
            self.getdata(callback)

    def __format_item(self,item):
        """TODO:数据格式转换"""
        return str(item)
        #return "{name}|{address}".format(**item)

if __name__ == '__main__':    
    key="eaaf1cd5acfefbae2030f5f5cba85626"
    keys=['207737a40231f8157771c03e5ce4ea60','910394336cdca243935ba36ffa1a2b41','3d9e8be61e1d5a84e1d081b6780bf501','e2c515b8978a231216c7a4fc95e83fff']
    keywords="清真"
    #amap = AMapPoidata(key,keywords,"510725")
    from amapdistrict import get_district_data
    from random import shuffle
    districts = get_district_data(key)
    for district in districts:
        file = open(u"~/poidata/amap/"+district[1]+".dat","a+")
        shuffle(keys)
        amap = AMapPoidata(keys[0],keywords,district[4])
        def savedata(line):
            print(line)
            file.write(line+"\n")
    
        amap.getdata(savedata)
        file.close()

    #amap = AMapPoidata(key,keywords,"620525")
    #file = open("e:/amap_poi_data.txt","a")
    #
    #def savedata(line):
    #    print(line)
    #    file.write(line)
    #
    #amap.getdata(savedata)
    #file.close()
