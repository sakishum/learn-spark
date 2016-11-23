
###输出压缩文件

```                
        import org.apache.hadoop.io.compress.SnappyCodec
        rdd.saveAsTextFile("codec/snappy",classOf[SnappyCodec])
```

