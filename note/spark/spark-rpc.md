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




### RpcEnvFactory
RpcEnvFactory有两个实现
1. AkkaRpcEnvFactory
2. NettyRpcEnvFactory


SparkEnv.create  //TODO:仔细看

    val actorSystemName = if (isDriver) driverActorSystemName else executorActorSystemName
    val rpcEnv = RpcEnv.create(actorSystemName, hostname, port, conf, securityManager,
      clientMode = !isDriver)
 //MNOTE:为啥中只有driverActorSystemName 和 executorActorSystemName ？



RpcEnv: AkkaRpcEnv\NettyRpcEnv

//注册RpcEndpoint
def setupEndpoint(name: String, endpoint: RpcEndpoint): RpcEndpointRef 



RpcEndpoint: N多实现
>>RpcEndpint有对应的RpcEndpintRef， RpcEndpintRef做为调用着向RpcEndpoint发送消息，
>>RpcEndpoint做为被调用着接收调用着发送的消息，根据不同的消息做出不同的操作
>>Master,Worker，Driver都实现了RpcEndpoint

def receiveAndReply(context: RpcCallContext): PartialFunction[Any, Unit] = {
    case _ => context.sendFailure(new SparkException(self + " won't reply anything"))
  }

 def receive: PartialFunction[Any, Unit] = {
    case _ => throw new SparkException(self + " does not implement 'receive'")
  }

还有几个重要方法

```
        **
         * Invoked when any exception is thrown during handling messages.
         */
        def onError(cause: Throwable): Unit = {
          // By default, throw e and let RpcEnv handle it
          throw cause
        }

        /**
         * Invoked when `remoteAddress` is connected to the current node.
         */
        def onConnected(remoteAddress: RpcAddress): Unit = {
          // By default, do nothing.
        }


        /**
         * Invoked before [[RpcEndpoint]] starts to handle any message.
         */
        def onStart(): Unit = {
          // By default, do nothing.
        }

```

### ActorySystem
ActorSystem是一个Actor容器，Actor通过name->Actor在ActorSystem中注册，请求时通过 name获得Actor
ActorSystem和一组Actor构成一个完整的Server 
Client通过host:port与ActorSystem建议连接，通过name和其对应的Actor进行通信，Client就是ActorRef

在spark中
RpcEnv -> ActorSystem
RpcEndpoint -> Actor
RpcEndpointRef -> ActorRef




