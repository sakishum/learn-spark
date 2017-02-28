#-*- coding:utf-8 -*-
import jieba
import jieba.posseg as pseg
jieba.load_userdict("./mydict.txt")

with open("./test-data-1.txt","r") as data:
    for line in data:
        print "keyword:" + line
        print "segment:" +"|".join([w.word + " " + w.flag for w in pseg.cut(line)]),
        #for w in pseg.cut(line):
        #    print w.word + "/" + w.flag
        print "---------------------"