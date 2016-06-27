import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

/**
 * Created by migle on 2016/6/23.
 */
public class JWordCount {
//    static {
//        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
//            System.setProperty("hadoop.home.dir", "E:\\spark\\hadoop-2.6.4\\hadoop-2.6");
//        }
//    }

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("wordcount by java")
                .setMaster("spark://vm-centos-00:7077")
                .set("hadoop.home.dir", "E:\\spark\\hadoop-2.6.4\\hadoop-2.6")
                .set("spark.executor.memory", "100m")
                .set("spark.testing", "10");

        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.addJar("E:\\workspace\\learn-spark\\code\\mspark\\target\\mspark-1.0-SNAPSHOT.jar");
        JavaRDD<String> jrdd = jsc.textFile("hdfs://vm-centos-01:9999/user/migle/HdfsRWTest/CHANGES.txt");


//        JavaRDD<String> words = jrdd.flatMap(new FlatMapFunction<String, String>() {
//            @Override
//            public Iterable<String> call(String s) throws Exception {
//                return Arrays.asList(s.split(" "));
//            }
//        });
//
//        JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {
//            @Override
//            public Tuple2<String, Integer> call(String s) {
//                return new Tuple2<String, Integer>(s, 1);
//            }
//        });
//
//        JavaPairRDD<String, Integer> counts = ones.reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer i1, Integer i2) {
//                return i1 + i2;
//            }
//        });
//
//        List<Tuple2<String, Integer>> output = counts.collect();
//        for (Tuple2<?,?> tuple : output) {
//            System.out.println(tuple._1() + ": " + tuple._2());
//    }

    //Java 8 style
    JavaPairRDD<String, Integer> ones = jrdd.flatMap((line) -> Arrays.asList(line.split(" ")))
            .mapToPair((x) -> new Tuple2<String, Integer>(x, 1));

    JavaPairRDD<String, Integer> counts = ones.reduceByKey((a, b) -> a + b);
    List<Tuple2<String, Integer>> r = counts.collect();
    r.forEach((t)->System.out.println(t.toString()));

    jsc.stop();

}
}
