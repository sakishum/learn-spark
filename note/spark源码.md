## 入口 
以下面最简单的代码为参考对象逐步分析Spark

    val conf = new SparkConf()
                            .setAppName("Simple Application")  
                            .setMaster("spark://192.168.242.130:7077")  
    val sc = new SparkContext(conf)  
    val rdd = sc.parallelize(1 to 10000)
    //val rdd = sc.textFile()
    rdd.count()    

## SparkContext 创建 

### SparkConf

        val sc = new SparkContext(conf) 

SparkConf提供配置信息， SparkContext还有如下构造器  

    def this() = this(new SparkConf())

>>无参的SparkConf会在系统属性中读取配置信息 *Spark启动时候会在Shell设置属性*

    if (loadDefaults) {
        // Load any spark.* system properties
        for ((k, v) <- System.getProperties.asScala if k.startsWith("spark.")) {
          settings(k) = v
        }
     }


### 其它构造函数
N个构造函数 ......


### 配置判断

接着一堆配置配置判断之类的



### LiveListenerBus
    // An asynchronous listener bus for Spark events
    private[spark] val listenerBus = new LiveListenerBus   //TODO LiveListenerBus
>> 发送SparkListenerEvents给已注册的SparkListener

   _jobProgressListener = new JobProgressListener(_conf)
    listenerBus.addListener(jobProgressListener)



### SparkEnv
创建SparkEnv
    // Create the Spark execution environment (cache, map output tracker, etc)
    _env = createSparkEnv(_conf, isLocal, listenerBus)
    SparkEnv.set(_env)


###  SparkUI
SparkUI启动
    
    // Initialize the Spark UI, registering all associated listeners
    private[spark] val ui: Option[SparkUI] =
    if (conf.getBoolean("spark.ui.enabled", true)) {
      Some(new SparkUI(this))
    } else {
      // For tests, do not enable the UI
      None
    }

    // Bind the UI before starting the task scheduler to communicate
    // the bound port to the cluster manager properly
    ui.foreach(_.bind())


### Scheduler创建

    // Create and start the scheduler
    private[spark] var taskScheduler = SparkContext.createTaskScheduler(this, master)
    private val heartbeatReceiver = env.actorSystem.actorOf(
    Props(new HeartbeatReceiver(taskScheduler)), "HeartbeatReceiver")
    @volatile private[spark] var dagScheduler: DAGScheduler = _
    try {
        dagScheduler = new DAGScheduler(this)
    } catch {
        case e: Exception => throw
        new SparkException("DAGScheduler cannot be initialized due to %s".format(e.getMessage))
    }

    // start TaskScheduler after taskScheduler sets DAGScheduler reference in DAGScheduler's
    // constructor
    taskScheduler.start()

createTaskScheduler中根据不同的部署（Master URL）方式生成不同的SchedulerBackend和TaskScheduler的组合

#### TaskSchedulerImpl

taskScheduler.start()

此方法实际是调用了SchedulerBackend.start()来完成任务
而SchedulerBackend.start()不同的部署方式处理方式不同，Local方式只是创建了Actor
而集群方式还会有AppClient.start(),向Master注册 **Master.RegisterApplication**

APPClient

  def tryRegisterAllMasters() {
      for (masterUrl <- masterUrls) {
        logInfo("Connecting to master " + masterUrl + "...")
        val actor = context.actorSelection(Master.toAkkaUrl(masterUrl))
        actor ! RegisterApplication(appDescription)
      }
    }


**AKKA的Actor的preStart()**

*大量的AKKA的使用，SO，AKKA先搞熟练* 



## MetricsSystem

  val metricsSystem = env.metricsSystem

  // The metrics system for Driver need to be set spark.app.id to app ID.
  // So it should start after we get app ID from the task scheduler and set spark.app.id.
  metricsSystem.start()


## RDD创建

>>A Resilient Distributed Dataset (RDD), the basic abstraction in Spark. Represents an immutable, partitioned collection of elements that can be operated on in parallel. This class contains the basic operations available on all RDDs, such as `map`, `filter`, and `persist`

RDD  && Partition
创建RDD   SparkContext.textFile,parallelize 等.......


  override def getPartitions: Array[Partition] = {
    val slices = ParallelCollectionRDD.slice(data, numSlices).toArray
    slices.indices.map(i => new ParallelCollectionPartition(id, i, slices(i))).toArray
  }

计算Partition

[Job 逻辑执行图](https://github.com/JerryLead/SparkInternals/blob/master/markdown/2-JobLogicalPlan.md)

## Action执行


RDD:
  def count(): Long = sc.runJob(this, Utils.getIteratorSize _).sum


SpackContext
    runjon(.)
    runjon(..)
    runjon(...)
    .....

dagScheduler.runJob(rdd, cleanedFunc, partitions, callSite, allowLocal,
      resultHandler, localProperties.get)   


DAGScheduler

  def runJob[T, U: ClassTag](
      rdd: RDD[T],
      func: (TaskContext, Iterator[T]) => U,
      partitions: Seq[Int],
      callSite: CallSite,
      allowLocal: Boolean,
      resultHandler: (Int, U) => Unit,
      properties: Properties = null)
  {
    val start = System.nanoTime
    val waiter = submitJob(rdd, func, partitions, callSite, allowLocal, resultHandler, properties)
    waiter.awaitResult() match {
      case JobSucceeded => {
        logInfo("Job %d finished: %s, took %f s".format
          (waiter.jobId, callSite.shortForm, (System.nanoTime - start) / 1e9))
      }
      case JobFailed(exception: Exception) =>
        logInfo("Job %d failed: %s, took %f s".format
          (waiter.jobId, callSite.shortForm, (System.nanoTime - start) / 1e9))
        throw exception
    }
  }





整体结构

任务如何分解
任务如何合并
任务如何调度

TaskScheduler
SchedulerBackend
ExecutorBackend
Executor


每个 Worker 上存在一个或者多个 ExecutorBackend 进程。每个进程包含一个 Executor对象，该对象持有一个线程池，每个线程可以执行一个 task


## Action执行










## DAGScheduler




SparkContext.runJob   






## TODO  
### broadcast



### 基础 
#### Scala

#### AKKA

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorSystem, ActorLogging, Actor}

/**
 * Created by migle on 2014/10/28.
 */
object LocalAKKA {

  case class HiMsg(msg: String)

  case class GoodMsg(msg: String)

  val system = ActorSystem("testakka")

  class MsgActor extends Actor with ActorLogging {
    override def preStart(): Unit = {
      println("pre start:" + self.path)
    }

    override def receive: Receive = {
      case HiMsg(msg) => log.warning(" msg:" + msg + " FROM " + sender.path + " TO  " + self.path)
        //创建一个新的Actor,注意两种方式创建的Actor的路径
        //val actor2 = system.actorOf(Props[MsgActor], name = "actor2")
        val actor2  = context.actorOf(Props[MsgActor], name = "actor2")

        actor2 ! new GoodMsg("good news")

      case GoodMsg(msg) => log.warning(" msg:" + msg + " FROM " + sender.path + " TO  " + self.path)
        //给发送者返回一个消息，发送完毕后关闭系统
        sender ! "$$$$$$$!"
        // 消息接收完毕后才会关闭系统
        system.shutdown()

      case _ => log.warning("unknow msg:" + " FROM " + sender.path + " TO  " + self.path)
    }

    override def postStop(): Unit = {
      println("post stop:" + self.path)
    }

  }

  def main(args: Array[String]) {
    //val system = ActorSystem("testakka")

    //同一级别的actor的名字不能重复，且不能用$开头 
    //Actors are automatically started asynchronously when created
    val actor1 = system.actorOf(Props[MsgActor], name = "actor1")
    //val actor2 = system.actorOf(Props[MsgActor],name="actor2")
    //actor1 ! "hi! Msg Actor"

    actor1 ! new HiMsg("hi!")
    //system.shutdown()
    system.awaitTermination()
  }
}



//TODO
RemoteAKKA







如果一个Actor有多个属性，可以通过如下方式设置其值：
发送适当的消息
放到构造函数中
重写preStart方法


http://my.oschina.net/jingxing05/blog/287462



