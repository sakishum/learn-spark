#!/usr/bin/env python3
# -*- coding:utf-8 -*-
import os
import logging
import requests
import time
####################################################
##
# @author migle 2017-09-27 00:27:50
# http://lbs.amap.com/api/webservice/guide/api/search
##
# TODO: 自动换key,代理,数字签名
##
#####################################################
fmt = logging.Formatter(
    fmt='%(asctime)s [line:%(lineno)d] %(levelname)s %(threadName)s %(message)s', datefmt='%Y-%m-%d %H:%M:%S')
ch = logging.StreamHandler()
ch.setFormatter(fmt)

ch2 = logging.FileHandler("amappoidata.log")
ch2.setFormatter(fmt)

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logger.addHandler(ch)
logger.addHandler(ch2)

class AMapPoidata:
    baseurl = "http://restapi.amap.com/v3/place/text"
    def __init__(self, key, kw, city='', types='', page_size=20, page_num=1, extensions='all', output='JSON'):
        """参数说明:http://lbs.amap.com/api/webservice/guide/api/search#text"""
        self.__key = key
        self.__keywords = kw
        self.__types = types
        self.__city = city
        self.__page_size = page_size
        self.__page_num = page_num
        self.__extensions = extensions  # all/base
        self.__output = output
        self.__payload = {}

    def __sig(self):
        # 没有sig也可以！
        pass

    def build_url(self):
        #self.__url =  "http://restapi.amap.com/v3/place/text?key=eaaf1cd5acfefbae2030f5f5cba85626&keywords=%E6%B8%85%E7%9C%9F%E7%81%AB%E9%94%85&types=&city=%E6%88%90%E9%83%BD&children=1&offset=20&page="+str(self.__page_num)+"&extensions=base"
        self.__payload = {'key':    self.__key,
                          'keywords':    self.__keywords,
                          'types':    self.__types,
                          'city':    self.__city,
                          'offset':    self.__page_size,
                          'page':    self.__page_num,  # 最大翻页数100
                          'extensions':    self.__extensions,
                          'output': self.__output}

    def getdata(self, callback):
        """callback 数据处理回调函数"""
        self.build_url()
        data = requests.get(AMapPoidata.baseurl, self.__payload)
        logger.info(data.url)
        # print(data.json()["info"])
        if(data.json()["status"] != '1'):
            logger.info("error request! url:%s status:%s infocode:%s message:%s",
                        data.url, data.json()["status"],
                        data.json()["infocode"],
                        data.json()["info"])
            # key不正确或过期
            if(data.json()["infocode"] == "10001"):
                logger.error("key不正确或过期")
            # 10003 DAILY_QUERY_OVER_LIMIT 访问已超出日访问量
            elif(data.json()["infocode"] == "10003"):
                logger.error("访问已超出日访问量。5个小时后再试")
                time.sleep(5*60*60)
                self.getdata(callback)
            # 10004 ACCESS_TOO_FREQUENT 单位时间内访问过于频繁
            elif(data.json()["infocode"] == "10001"):
                logger.error("单位时间内访问过于频繁,要等等。5秒后再试")
                time.sleep(5)
                self.getdata(callback)
            else:
                pass
            return
        for item in data.json().get('pois', ""):
            callback(item)
        if(int(data.json()["count"]) > self.__page_num * self.__page_size):
            logger.info("total:%s,next", data.json()[
                        "count"])  # 如理>page_size还需要再请求一次
            self.__page_num += 1
            self.getdata(callback)


def poi_data_format(item):
    """格式化数据"""
    return "{i[id]}\t{i[tag]}\t{i[name]}\t{i[type]}\t{i[typecode]}\t{i[biz_type]}\t{i[address]}\t{i[location]}\t{i[distance]}\t{i[tel]}\t{i[pcode]}\t{i[pname]}\t{i[citycode]}\t{i[cityname]}\t{i[adcode]}\t{i[adname]}\t{i[biz_ext][rating]}\t{t}".format(i=item,t=time.time())

#抓取行政区域数据
def get_district_data(key):
    #中国 100000
    param = {'keywords':'100000','key':key,'subdistrict':'3'}
    data = requests.get('http://restapi.amap.com/v3/config/district',param)
    districts=[]
    for country in data.json()["districts"]:
        for province in country["districts"]:
            for city in  province["districts"]:
                for district in city["districts"]:
                    districts.append((province["adcode"],province["name"],
                                     city["adcode"],city["name"],
                                     district["adcode"],district["name"]))

    return districts

if __name__ == '__main__':
    keys = ['b6d1a9382739c4e33d33cf3a906a9117','dd1c0d2257f9e69374beef3366056b6a',
    '10f22dff5a4fb84bbfc3150bbbd346ee','9a2e40e406c899efb40a339e837b1e28',
    '80da8c87335c216428643a9f9019ea75','8a82592bca77567d9643d719dea89b94',
    '470f5a03a2b10fbc682599a82383ffff','402e4839f3396ab80ab03054e2546992',
    'c2316f598d46e1c004eb5cab9e4204d8','6d8336f604a072e87e00b2a1b1d474ba']
    keywords = "清真"
    from random import shuffle
    districts = get_district_data(keys[0])
    #districts = [('110000', '北京市', '110100', '北京城区', '110102', '西城区')]
    for district in districts:
        #if(district[1] in ['北京市','甘肃省','安徽省','福建省','广东省','河南省','内蒙古自治区','宁夏回族自治区','山东省','山西省','上海市','天津市']):
        #    continue
        logger.debug("搜索区域:(%s:%s)",district[5],district[4])
        filename = u"./poidata/amap/" + district[1] + ".dat"
        if(os.path.exists(filename)):
            file = open(filename, "a")
        else:
            file = open(filename, "w")
        shuffle(keys)
        amap = AMapPoidata(keys[0], keywords, district[4])

        def savedata(item):
            line = poi_data_format(item)
            # print(line)
            file.write(line + "\n")
        try:
            amap.getdata(savedata)
        except Exception as ex:
            logger.error(ex)
            retry = open('./retry_district.dat', "a")
            retry.writelines('|'.join(district))
            retry.close()
        finally:
            file.close()    
        
        
    logger.info("搜索完成")



#简单的key管理器
# class KM:
#     keys = ['207737a40231f8157771c03e5ce4ea60', '910394336cdca243935ba36ffa1a2b41','e2c515b8978a231216c7a4fc95e83fff']
#     def __init__(self):
#         self.__cur_keys = KM.keys.copy()
#     def get_new_key(self,old=None):
#         if(old != None):
#             self.__cur_keys.remove(old)
#         if(len(self.__cur_keys) <= 0):
#             time.sleep(5*60*60)
#             print("old")
#             self.__cur_keys = KM.keys.copy()
#             return self.get_new_key()
#         else:
#             from random import shuffle
#             shuffle(self.__cur_keys)
#             return self.__cur_keys[0]