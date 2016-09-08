spark streaming


 KafkaUtils.createStream读kafka中数据中kafka-consumer-offset-checker.sh中的offset和lag不变!!


##foreachRDD 

```
        dstream.foreachRDD { rdd =>
          val where1 = "on the driver"
            rdd.foreach { record =>
              val where2 = "on different executors"
            }
          }
        }

```
//http://allegro.tech/2015/08/spark-kafka-integration.html