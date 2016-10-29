
一些基础但不经常用，在此记录一下
System.getenv  #获取环境变量  linux中由export设置的值
System.getProperty\System.getPropertys #获取系统属性


  private val started = new AtomicBoolean(false)
  AtomicReference:
    针对 Object reference 的CAS(compare and set )操作  


  volatile类型：当某线程修改value的值时，其他线程看到的value值都是最新的value值


- [ ] HashMap WeakHashMap


## 数据类型

Integer的拆箱、装箱
在装箱的时候自动调用的是Integer的valueOf(int)方法。而在拆箱的时候自动调用的是Integer的intValue方法。

        int a = 10;
        Integer b = 10;
        Integer c = new Integer(10);
        Integer d = Integer.valueOf(10);
        Integer b1 = 10;

        //跟原生类型比较，Integer会自动拆箱,所以值相等的时候就会相等
        System.out.println(a == b);  //true
        System.out.println(a == b1); //true
        System.out.println(a == c);  //true
        System.out.println(a == d);  //true


        //自动装箱时有缓存策略(java.lang.Integer.IntegerCache),没有缓存时才会用new Integer新创建对象,默认会缓存[-128,127]的值(最大值可以通过jvm参数调整-XX:AutoBoxCacheMax)
        // -XX:AutoBoxCacheMax=1024
        System.out.println(b == b1);  //缓存能命中时为true否则为false，默认[-128,127]
        System.out.println(b == c);   //false
        System.out.println(b == d);   //缓存能命中时为true否则为false 默认[-128,127]
        System.out.println(c == d);   //false



##多线程
//TODO
Semaphore和Lock的不同


http://www.hollischuang.com/archives/489?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io

http://www.cnblogs.com/xdp-gacl/p/3777987.html


finally返回值 

String的substring()方法内部是如何实现的？
!!!!http://www.codeceo.com/article/20-java-interview-questions-from-investment-banks.html
http://www.codeceo.com/article/133-java-interview-5-years.html
        


##SecurityManager
http://www.importnew.com/9751.html

##jdk自带工具
###jar
创建可执行jar包  
`jar cvfm x.jar MANIFEST.MF .`
*MANIFEST.MF*为清单文件内容类似以下：

```
Manifest-Version: 1.0
Main-Class: ClassPath

```

执行 `java -jar x.jar`  



java题纲
http://www.codeceo.com/article/201-java-interview-qa.html


重点：
http://www.importnew.com/14630.html





import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.FileInputStream;
import java.io.IOException;

public class FTPUploader {
   //static String host="10.95.66.80";
   //static String username="ocdc";
  // static String pwd="llixsx15@)";
    static String host="192.168.99.130";
    static String username="migle";
    static char[] pwd="iammigle".toCharArray();

    public static void main(String[] args) throws IOException, FtpProtocolException {
        FtpClient ftp = FtpClient.create(host);
        ftp.login(username,pwd);
        ftp.setBinaryType();
        ftp.putFile("/home/migle/test/xxxxx.txt",new FileInputStream("e:/bb.txt"));
        ftp.close();
    }
}





java 
-cp 通配符 
java -cp /app/lib/*  Main   
而不是  java -cp /app/lib/*.jar

低版本
java -cp $(echo /app/lib/*.jar | tr ' ' ':') Main


### 查找class所在的jar包
String name = SparkConf.class.getName();
System.out.println(name);
URL uri = SparkConf.class.getResource("/"+name.replace(".","/")+ ".class");
System.out.println(uri.toString());