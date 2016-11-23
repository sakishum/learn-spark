package me.migle;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by migle on 2016/7/29.
 */
public class ClassPath {
    public static void main(String[] args) throws URISyntaxException {


        //调用的位置,而非文件所在的位置
        System.out.println("user.dir:  " + System.getProperty("user.dir"));


        //调用的位置,而非文件所在的位置
        System.out.println("new File(\"./\") :  " + (new File("./")).getAbsolutePath());




        System.out.println("ClassPath.class.getResource(\".\"):    "+ ClassPath.class.getResource("."));
        System.out.println("ClassPath.class.getResource(\"/\"):    "+ ClassPath.class.getResource("/"));
        System.out.println("Thread.currentThread().getContextClassLoader().getResource(\".\"):      " + Thread.currentThread().getContextClassLoader().getResource("."));

        System.out.println("---------------------------------");
    }
}
