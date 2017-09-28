#!/usr/bin/env python
# -*- coding:utf-8 -*-
import requests
########################################
#
# @author migle 2017-9-27 16:37:29
# 调高德API抓取行政区域数据
#######################################

def get_district_data(key):
    #中国 100000
    param = {'keywords':'100000','key':key,'subdistrict':'3'}
    data = requests.get('http://restapi.amap.com/v3/config/district',param)
    #print(data.url)
    #print(data.json())
    ##直接保存数据
    #print("-----"*5)
    districts=[]
    for country in data.json()["districts"]:
        #cous="{citycode}|{adcode}|{name}|{center}|{level}|".format(**country)
        for province in country["districts"]:
            #print(p)
            #ps=cous+"{citycode}|{adcode}|{name}|{center}|{level}|".format(**province)
            #ps = "{adcode}|{name}|".format(**province)
            for city in  province["districts"]:
                #cs = ps+"{citycode}|{adcode}|{name}|{center}|{level}|".format(**city)
                #cs  = "{adcode}|{name}|".format(**city)
                for district in city["districts"]:
                    #print(cs+"{citycode}|{adcode}|{name}|{center}|{level}".format(**district))
                    districts.append((province["adcode"],province["name"],
                                     city["adcode"],city["name"],
                                     district["adcode"],district["name"]))
                    #print(ps+cs+"{adcode}|{name}".format(**district))

    #print("-----"*5)
    return districts
if __name__ == '__main__':
    dd=get_district_data('b6d1a9382739c4e33d33cf3a906a9117')
    for d in dd:
        print(dd)