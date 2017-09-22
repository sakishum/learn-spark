#!/usr/bin/env python
# -*- coding:utf-8 -*-
import hashlib
import requests

BAIDU_AK='5b563d231ae5ec8fb6af7ba4494d0d28'
BAIDU_SK='B08b392e55105b46b49f4e3c032dd4f4'
BASE_URL="http://api.map.baidu.com"
BASE_SEARCH_URL="/place/v2/search?"

class baidudata(object):
    def __init__(self,query):
        self.path=BASE_SEARCH_URL+"q="+query \
              + "&region=北京" \
              + "&output=json" \
              + "&ak=" + BAIDU_AK
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
        data = requests.get(self.data_url)
        print(data.json())

if __name__ == '__main__':
    baidudata = baidudata("清真")
    baidudata.getdata()
