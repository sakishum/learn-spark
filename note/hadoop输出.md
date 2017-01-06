## OutputFormat
1. getRecordWriter返回真正输出执行者RecordWriter
2. checkOutputSpecs作一些检查工作

## FileOutputFormat
抽象类，继承自*OutputFormat*实现了检查文件是否已存在或者是否有相应权限
设置压缩类，输出目录，工作目录等功能

1. org.apache.hadoop.mapreduce.lib.output.FileOutputFormat为新版的实现，目前Spark还使用的是旧API
2. OutputCommitter是任务提交器主要是用来对任务(包括map任何和rduce任务)的输出进行管理，包括提交、终止等  FileOutputCommitter：任务处理过程中的输出结果存储在工作目录(在临时目录下)下，最后成功处理完之后才把结果移动到最终目录(临时目录是其子目录)。作业最终的输出目录可以通过配置文件或

## TextOutputFormat
文本输出格式实现类，继承自FileOutputFormat
静态内部类LineRecordWriter中一定义了具体的输出格式：“key分隔符value” 其中分隔符取自mapreduce.output.textoutputformat.separator，默认为"\t"

默认输出文件名为“part-xxxx”如果想自定义输出文件名可以实现自己的TextOutputFormat，修改getRecordWriter方法中的name即可

