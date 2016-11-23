1. brach-1.3在idea编译成功后运行Master时候会报错

解决：

1. 将guava-14.0.1.jar添加到classpath下比如：jre\lib\ext下    
2. 将guava依赖配置的删掉这一行<scope>provided</scope>
