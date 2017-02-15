#-*- coding:utf-8 -*-
import jieba
jieba.load_userdict("./mydict.txt")

#jieba.add_word('剑逆狂神')
words = jieba.cut("剑逆狂神免费全文")
print "/".join(words)

seg = jieba.cut("使用结巴来对中文进行分词")
print "/".join(seg)
