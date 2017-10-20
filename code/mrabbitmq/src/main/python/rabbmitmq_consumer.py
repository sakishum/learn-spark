#!/usr/bin/env python
# -*- coding:utf-8 -*-
import pika
credentials = pika.PlainCredentials('cqcrm', 'cqcrm')
# 链接rabbit
connection = pika.BlockingConnection(pika.ConnectionParameters('192.168.99.131',5672,'/',credentials))
# 创建频道
channel = connection.channel()
##channel.queue_delete(queue='hello')
channel.queue_declare(queue='hello')


def callback(ch, method, properties, body):
    print(" [x] Received %r" % body)
    import time
    time.sleep(1)
    print('ok')
    ch.basic_ack(delivery_tag=method.delivery_tag)  # 主要使用此代码


channel.basic_consume(callback,
                      queue='hello',
                      no_ack=False)

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()
