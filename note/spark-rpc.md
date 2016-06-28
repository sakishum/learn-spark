## RPC
SparkEnv创建RpcEnv,
RpcEnv相当于容器，RpcEndpoint注册其中，通过RpcEndpointRef对其进行消息发送
RpcEndpointRef\RpcEndpoint\RpcEnv

  private def getRpcEnvFactory(conf: SparkConf): RpcEnvFactory = {
    val rpcEnvNames = Map(
      "akka" -> "org.apache.spark.rpc.akka.AkkaRpcEnvFactory",
      "netty" -> "org.apache.spark.rpc.netty.NettyRpcEnvFactory")
    val rpcEnvName = conf.get("spark.rpc", "netty")
    val rpcEnvFactoryClassName = rpcEnvNames.getOrElse(rpcEnvName.toLowerCase, rpcEnvName)
    Utils.classForName(rpcEnvFactoryClassName).newInstance().asInstanceOf[RpcEnvFactory]
  }