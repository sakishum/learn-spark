#Scala简明教程

--------------------------------------------------------------------

## 环境 

1. sudo dpkg -i scala-xxxx  
2. sudo dpkg -i sbt  
3. sbt console  or scala filename.scala    

		print "hello scala"

----------------------------------------------------------------------   

## 基础

###hello scala

	object StartScala {
	  def main(arg: Array[String]) {
	  	val msg = "hello scala"    //val msg: String = "hello scala"
    	val mint = 123             //val mint: Int = 123

    	System.out.println(msg)
    	println(mint);
	  }
	}


* 程序入口！object.main(args:Array[String]){....}，*文件名可以和类名不同，但不建议*
>> 也可以用以下方式
	

		object SubApp extends App{  
 			println("hello scala")   //main方法内容   
		} 



* 用val或var声明变量
>>Scala有两种变量，val和var。val类似于Java里的final变量。一旦初始化了，val就不能再赋值了。与之对应的，var如同Java里面的非final变量。var可以在它生命周期中被多次赋值
* 类型自动推断,如果显式写出的话类型名称写在分号“:”后面
* 句末分号是可选的
* 可以直接调Java类,以下包是默认导入的
	import java.lang._ // java.lang包的所有东西
	import scala._ // scala包的所有东西
	import Predef._


### 类型
1. 基本类型与Java中基本一致
>>Int\RichInt String\RichString ......
2. 原始字符串 *"""  XXXX """*  赞一个
3. 符号文本
4. 数组
	
		val a =  Array[String](3)   //new Array[String](3)
		a(0) = "SPARK"  //数据索引在圆括号中！其实只是方法调用
		val d = Array("e1","32")
		pritnln(d(0))

5. 类型系统:所有类型都直接或间接地由 Any 类继承而来
6. 类型别名 
	
		type dd = Double
		val x:dd = 5

>> 操作符：Scala里的操作符不是特殊的语言语法：任何方法都可以是操作符。使用方法的方式使它成为操作符。如果写成s.indexOf('o')，indexOf就不是操作符。不过如果写成，s indexOf 'o'，那么indexOf就是操作符了，因为你以操作符标注方式使用它

7. 根类Any有两个子类：AnyVal和AnyRef。AnyVal是Scala里每个内建值类的父类。有九个这样的值类：Byte，Short，Char，Int，Long，Float，Double，Boolean和Unit。其中的前八个对应到Java的原始类型，它们的值在运行时表示成Java的原始值。Scala里这些类的实例都写成文本

### 类
1. 成员默认为public，Scala里禁止在同一个类里用同样的名称定义字段和方法，而在Java里这样做被允许。
2. object， 如果没有显式的extends任何类的话，Scala编译器隐式地假设你的类扩展自scala.AnyRef，在Java平台上与java.lang.Object一致
3. Application
4. 构造器
		Scala编译器将把你放在类内部的任何不是字段的部分或者方法定义的代码，编译进主构造器
		字段访问
		从构造器：Scala的类里面，只有主构造器可以调用超类的构造器
				每个Scala类里的每个从构造器都是以“this(...)”形式开头的。被调用的构造器既可以是主构造器，也可以是从文本上来看早于调用构造器的其它从构造器
5. 作用域
Scala里的访问修饰符可以通过使用修饰词增加。格式**为private[X]或protected[X]**的修饰符表示“直到”X的私有或保护，这里X指代某些外围的包，类或单例对象

  		private[spark] val executorEnvs = HashMap[String, String]()   *SparkContext.scala*

6. 参数化字段(parametric field)

6. 抽象类

	abstract class ClassName {
		def methodName: Array[String]
	}

>> 
1. 方法不需要加特殊修饰符：如果方法没有实现（也就是说，没有等号或方法体），它就是抽象的


6. 伴生对象：companion object
当单例对象与某个类共享同一个名称时，他被称作是这个类的伴生对象：companion object。你必须在同一个源文件里定义类和它的伴生对象。类被称为是这个单例对象的伴生类：companion class。类和它的伴生对象可以互相访问其私有成员

7.  == 
Scala的==与Java的有何差别Java 里的既可以比较原始类型也可以比较参考类型。对于原始类型，Java 的==比较值的相等性，如Scala。然而对于参考类型，Java 的==比较了参考相等性：reference equality，也就是说这两个变量是否都指向于JVM堆里的同一个对象。Scala也提供了这种机制，名字是eq。不过，eq和它的反义词ne，仅仅应用于可以直接映射到Java的对象。eq和ne的全部细节将在11.1节和11.2节给出。还有，可以看一下第二十八章，了解如何编写好的equals方法


### 函数/方法

	def fun_name(p1:Type1,p2:Type2):rType = {
		do something
	}

* 函数参数必须指明类型
* 函数返回值是函数体最后一句的值不需要return，没事别乱写return
>>在Java里，从方法里返回的值的类型被称为返回类型。在Scala里，同样的概念被叫做结果类型  

*  如果没有"=" 则返回类型为 Unit;如果有"="会自推导返回类型,所以最好写上，当表达式没有返回值时，默认返回Unit
*　如果方法仅带一个参数，可以不带点或括号的调用它

**函数参数始终是val不会是var**
	
### Trait  
>> Scala 里相当于 Java 接口的是特征(Trait)。Trait 的英文意思是特质和性状（本文称其为特征），实际
上他比接口还功能强大。与接口不同的是，它还可以定义属性和方法的实现。Scala 中特征被用于服务于
单一目的功能模块的模块化中。通过混合这种特征（模块）群来实现各种应用程序的功能要求，Scala 也
是按照这个构想来设计的

连着多个 with 语句来混合多个特征到一个类中。第一个被继承源用 extends，第二个
以后的就用 with 语句。正如大家所知道的，可以生成实例的是非抽象(abstract)的类。另外请注意一下从
特征是不可以直接创建实例的



### 常用容器类

Array：数组是可变的同类对象序列；
Set：无序不重复集合类型，有可变和不可变实现；
Map：键值对的映射，有可变和不可变实现；
Tuple：可以包含不同类元素，不可变实现；
List：Scala的列表是不可变实现的同类对象序列，因应函数式编程特性的需要。
Seq:

http://docs.scala-lang.org/zh-cn/overviews/collections/overview

上面的这些都是trait，在可变(mutable)包和不可变(immutable)包里都有对应的实现，同时也有其他特殊用途的实现
http://www.importnew.com/4543.html

#### map
val mdata = Map(x -> 123, y -> 456)

遍历
	
	//1
	mdata.foreach{case (k,v) => println(k+"->"+v)}
	//2
	mdata foreach (kv => println (kv._1 + "->" + kv._2))
	//3
	for((k,v) <- mdata){println(k+"->"+v)}


```
        scala> val numbers = Map(1 -> "one", 2 -> "two")
        numbers: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two)
        
        scala> numbers.get(2)
        res1: Option[String] = Some(two)
        
        scala> print(numbers.get(2))
        Some(two)
        scala> print(numbers.get(2).get)
        two

```

#### List
val list = List(1,2,3,4)

#### Tuple
>>tuple与List一样也是不可变的，但与Lisst不同，tuple可以包含不同类型的元素。而List应该是List[Int]或List[String]的样子，元组可以同时拥有Int和String。可以用点号，下划线和一个基于1的元素索引访问它

	val pair = (99, "Luftballons")  
	println(pair._1)  //点号，下划线和一个从1开始的索引访问它
	println(pair._2) 


#### 与Java的转换
*JavaConverters*Java和Scala的集合类之间进行转换.它给常用的Java集合提供了asScala方法，同时给常用的Scala集合提供了asJava方法。

`import scala.collection.JavaConverters._`

Scala.asJava
Java.asScala

####Option

Option:是一个表示有可能包含值的容器
Option本身是泛型的，并且有两个子类： Some[T] 或 None
>>scala推荐在可能返回空的方法使用Option[X]作为返回类型。如果有值就返回Some[x](Some也是Option的子类)，否则返回None

Null是所有AnyRef的子类，在scala的类型系统中，AnyRef是Any的子类，同时Any子类的还有AnyVal。对应java值类型的所有类型都是AnyVal的子类。所以Null可以赋值给所有的引用类型(AnyRef)，不能赋值给值类型，这个java的语义是相同的。 null是Null的唯一对象。 

Nothing是所有类型的子类，也是Null的子类。Nothing没有对象，但是可以用来定义类型。例如，如果一个方法抛出异常，则异常的返回值类型就是Nothing(虽然不会返回) 

Nil是一个空的List，定义为List[Nothing]，根据List的定义List[+A]，所有Nil是所有List[T]的子类

Either
Either 也是一个容器类型，但不同于 Try、Option，它需要两个类型参数： Either[A, B] 要么包含一个类型为 A 的实例，要么包含一个类型为 B 的实例。 这和 Tuple2[A, B] 不一样， Tuple2[A, B] 是两者都要包含。

Either 只有两个子类型： Left、 Right， 如果 Either[A, B] 对象包含的是 A 的实例，那它就是 Left 实例，否则就是 Right 实例。

在语义上，Either 并没有指定哪个子类型代表错误，哪个代表成功， 毕竟，它是一种通用的类型，适用于可能会出现两种结果的场景。 而异常处理只不过是其一种常见的使用场景而已， 不过，按照约定，处理异常时，Left 代表出错的情况，Right 代表成功的情况

Option: 解决null（空指针）问题
Either: 解决返回值不确定（返回两个值的其中一个）问题

###容器操作
foreach
map
zip
partition

###异常
一般来说，在 Scala 中，好的做法是通过从函数里返回一个合适的值来通知人们程序出错了
Try 有两个子类型：
Success[A]：代表成功的计算。
封装了 Throwable 的 Failure[A]：代表出了错的计算

##进阶

### 隐式转换(implicit conversion)

	 implicit def  addOne(i:String) = i.toInt + 1
	  def main(args:Array[String]){
	    val i:Int = "12";
	    println(i)
	  }
### 泛型
参数类型
类型变量界定
\[T <: X]
要求T是X的子类
\[T >: X ]
要求T  是X的 超类
视图界定
\[T <% X ]
要求T是或者能被隐式转换成 X 的子类
上下文界定
\[M:N]
要求提供N[M]类型的

class[+T]
协变


同一个类中或伴生对象中

###正则表达式
val pattern = "(S|s)cala".r
val str = "Scala is scalable and cool"


>> 匹配分组
	val p = "([a-z]+)([0-9]+).*".r
	val p(ss,num) = "abc123xyz"
		ss: String = abc
		num: String = 123

>>spark example logQuery
val apacheLogRegex =
      """^([\d.]+) (\S+) (\S+) \[([\w\d:/]+\s[+\-]\d{4})\] "(.+?)" (\d{3}) ([\d\-]+) "([^"]+)" "([^"]+)".*""".r

 apacheLogRegex.findFirstIn(line) match {
        case Some(apacheLogRegex(ip, _, user, dateTime, query, status, bytes, referer, ua)) =>
          new Stats(1, bytes.toInt)
        case _ => new Stats(1, 0)
      }


### apply

### 函数式风格


### 闭包 

如果你想在函数文本中包括超过一个语句，用大括号包住函数体，一行放一个语句，就组成了一
个代码块。与方法一样，当函数值被调用时，所有的语句将被执行，而函数的返回值就是最后一
行产生的那个表达式

1. 匿名函数 
	参数表 => 函数体　　 (s:String) => s.length
	
1.  回调

3. first-class function
2. require


def f(x: R)   call-by-value 
def f(x: => R)	  call-by-name (lazy parameters)


Curried functions
柯里化指的是将原来接受两个参数的函数变成新的接受一个参数的函数的过程。新的函数返回一个以原有第二个参数作为参数的函数。
def mulOneAtATime(x: Int) = (y: Int) => x * y
// 计算两个数的乘积
mulOneAtATime(6)(7)
// 多参数的写法
def mul(x: Int, y: Int) = x * y

mulOneAtATime(6)返回的是函数(y: Int)=>6*y，再将这个函数应用到7，最终得到结果。

任何多个参数的函数都可以被curry 

def add(a:Int,b:Int) = a+b 
val k = (add _).curried
val add5 = k(5)(_)
add(10)


函数即对象
apply

### pattern matching
####Case Class是也是普通类，但其导出构造参数，并通过模式匹配提供递归分解机制
For every case class the Scala compiler generates an equals method which implements structural equality and a toString method. 


<http://docs.scala-lang.org/tutorials/tour/case-classes.html>
http://nerd-is.in/2013-09/scala-learning-pattern-matching-and-case-classes/



4. tail recursive 会被自动优化：Scala 编译器检测到尾递归就用新值更新函数参数，然后把它替换成一个回到函数开头的跳转，递归经常是比基于循环的更优美和简明的方案。
如果方案是尾递归，就无须付出任何运行期开销 *TODO* 看看Java会不会自动优化




### 操作符和方法
1. 1 + 2与(1).+(2)其实是一回事
>> Scala里的操作符不是特殊的语言语法：任何方法都可以是操作符。使用方法的方式使它成为操作符。如果写成s.indexOf('o')，indexOf就不是操作符。不过如果写成，s indexOf 'o'，那么indexOf就是操作符了，因为你以操作符标注方式使用它。

2. ==  :如果左侧不是null再调用equals比较 
3. 不能用++i和i++，要在Scala里自增必须写成要么i = i + 1，或者i += 1


### 包管理
1. import XXX._  类似java中的静态导入


----------------------------------------------------------


### 高阶函数，闭包

>> f1(p1:type1)
f2(p2 => T2)    ---自定义DSL





### 其它

1. Some
2. Option
Option

Option是一个包含或者不包含某些事物的容器。

Option的基本接口类似于：

1
2
3
4
5
trait Option[T] {
  def isDefined: Boolean
  def get: T
  def getOrElse(t: T): T
}
Option本身是泛型的，它有两个子类：Some[T]和None

我们来看一个Option的示例： Map.get使用Option来作为它的返回类型。Option的作用是告诉你这个方法可能不会返回你请求的值。

1
2
3
4
5
6
7
8
scala> val numbers = Map(1 -> "one", 2 -> "two")
numbers: scala.collection.immutable.Map[Int,String] = Map((1,one), (2,two))
 
scala> numbers.get(2)
res0: Option[java.lang.String] = Some(two)
 
scala> numbers.get(3)
res1: Option[java.lang.String] = None
现在，我们要的数据存在于这个Option里。那么我们该怎么处理它呢？

一个比较直观的方法就是根据isDefined方法的返回结果作出不同的处理。

1
2
3
4
5
6
7
//如果这个值存在的话，那么我们把它乘以2，否则返回0。
 
val result = if (res1.isDefined) {
  res1.get * 2
} else {
  0
}
 

不过，我们更加建议你使用getOrElse或者模式匹配来处理这个结构。

getOrElse让你可以很方便地定义一个默认值。

1
val result = res1.getOrElse(0) * 2
模式匹配可以很好地和Option进行配合使用。

val result = res1 match { case Some(n) => n * 2 case None => 0 }

参考 《Effective Scala》中关于 Options的内容。

Scala的赋值语句中永远产生unit值，不同于Java

mixin 赞一个

Scalacheat
http://docs.scala-lang.org/cheatsheets/

Scala 指南
http://zh.scala-tour.com/

effective scala
http://twitter.github.io/effectivescala/index-cn.html

http://docs.scala-lang.org/zh-cn/overviews/collections/conversions-between-java-and-scala-collections





