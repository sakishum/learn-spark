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

rpcenv!!

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

- [ ] DAGScheduler,TaskScheduler,SchedulerBackend三者之前的关系？
>>DAGScheduler面向stage的调度，TaskScheduler面向task的调度，SchedulerBackend是TaskScheduler的后端，不同部署方式有不同实现。
遇到action算子，就会创建一个JOB，JOB同进就会被提交到DAGScheduler里，DAGScheduler会把JOB划分为多个stag，然后每个stag创建一个taskSet(里面是每个task),会把taskSet提交给TaskScheduler里，TaskScheduler会把taskSet里的每个taskr提交到executor上执行(task分配算法)

      val (sched, ts) = SparkContext.createTaskScheduler(this, master)
         _schedulerBackend = sched
         _taskScheduler = ts
         _dagScheduler = new DAGScheduler(this)
         _heartbeatReceiver.ask[Boolean](TaskSchedulerIsSet)
      
         // start TaskScheduler after taskScheduler sets DAGScheduler reference in DAGScheduler's
         // constructor
         _taskScheduler.start()

taskScheduler.start()实际是调用了SchedulerBackend.start()来完成任务
而SchedulerBackend.start()会根据不同的部署方式处理方式不同，Local方式只是创建了Actor
而集群方式还会有AppClient.start(),向Master注册 **Master.RegisterApplication**
createTaskScheduler中根据不同的部署（Master URL）方式生成不同的SchedulerBackend和TaskScheduler的组合

在Spark standlone环境中SchedulerBackend的start方法时，先通过AppClient进行ClientEndPoint,ClientEndPoint的preStart方法又调用AppClient的onstart方法，onstart中调用注册Master,注册后向Master发送RegisterApplication
Master通过rpc给Driver发送RegisteredApplication，作业开状态成RUNNING



- [ ] Job划分stage
- [ ] Task分配算法
#### TaskSchedulerImpl








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

Spark中的rpc
SparkEnv.RpcEnv

每个 Worker 上存在一个或者多个 ExecutorBackend 进程。每个进程包含一个 Executor对象，该对象持有一个线程池，每个线程可以执行一个 task


## Action执行




## DAGScheduler




SparkContext.runJob   







## 其它
1. IDEA中调试Driver程序
IDEA中调度程序时需要加入
sc.addJar("xxx.jar")
可以在IDEA中设置一下，运行前package一下
另外：pom.xml中需要添加scala插件


如果一个Actor有多个属性，可以通过如下方式设置其值：
发送适当的消息
放到构造函数中
重写preStart方法


http://my.oschina.net/jingxing05/blog/287462


http://www.jianshu.com/p/bc1e9755143e




