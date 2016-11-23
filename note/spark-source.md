-----------------------------------------------------
@date:2016-10-29 
@title:Spark源码分析 
@author:migle
-------------------------------------------------------


## 分析之前
1. 运行环境(Master,Worker启动)
2. 作业提交
3. 作业执行
4. 返回结果

## Master启动
主类：Master.scala

```
 def main(argStrings: Array[String]) {
    SignalLogger.register(log)
    val conf = new SparkConf
    val args = new MasterArguments(argStrings, conf)
    val (rpcEnv, _, _) = startRpcEnvAndEndpoint(args.host, args.port, args.webUiPort, conf)
    rpcEnv.awaitTermination()
  }
```

main方法中很简单，中是向master中发送了一个BoundPortsRequest消息，但是Master是实现 了RpcEndpoint的会回调onstart，所以onstart方法中才是主要启内容

启动master上服务，等待Worker的注册


## Worker 启动
和Master类似不同是启动后去Master注册

 def main(argStrings: Array[String]) {
    SignalLogger.register(log)
    val conf = new SparkConf
    val args = new WorkerArguments(argStrings, conf)
    val rpcEnv = startRpcEnvAndEndpoint(args.host, args.port, args.webUiPort, args.cores,
      args.memory, args.masters, args.workDir, conf = conf)
    rpcEnv.awaitTermination()
  }