#!/usr/bin/env python
# -*- coding:utf-8 -*-
import requests
########################################
#
# @author migle 2017-9-27 16:37:29
# 调高德API抓取行政区域数据
#######################################


def get_district_data(key):
    districts=[]
    #中国 100000
    param = {'keywords':'100000','key':key,'subdistrict':'1'}
    data = requests.get('http://restapi.amap.com/v3/config/district',param)
    #print(data.url)
    provinces=[(province["adcode"],province["name"]) for province in data.json()["districts"][0]["districts"]]
    #print(provinces)
    for province in provinces:
        param['subdistrict']='2'
        param['keywords']=province[0]
        citys = requests.get('http://restapi.amap.com/v3/config/district',param)

        for city in  citys.json()["districts"][0]["districts"]:
            if(city['level']=='district'):
                districts.append((province[0],province[1],province[0],province[1],city['adcode'],city['name']))
            else:
                for district in city["districts"]:
                    districts.append((province[0],province[1],city['adcode'],city['name'],district['adcode'],district['name']))
    return districts
if __name__ == '__main__':
    dd=get_district_data('b6d1a9382739c4e33d33cf3a906a9117')
    for d in dd:
        print(d)