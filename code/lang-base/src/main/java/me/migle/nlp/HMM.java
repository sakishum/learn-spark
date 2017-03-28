package me.migle.nlp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by migle on 2017/3/20.
 */
public class MSegment {
    private List<Character> words = new ArrayList<Character>(); // 观察值序列


    private char[] hiddenStatus = new char[]{'B', 'E', 'M', 'S'};  //    S：隐藏状态集合；每个状态代表该字在词语中的位置,开始、结束、中间、单独成词
    //    N：观察状态集合；  单个字的状态

    //dim1:bems,dim2:bems
    private double[][] transMatrix = new double[][]{
            {-3.14e+100, -0.510825623765990, -0.916290731874155, -3.14e+100},
            {-0.5897149736854513, -3.14e+100, -3.14e+100, -0.8085250474669937},
            {-3.14e+100, -0.33344856811948514, -1.2603623820268226, -3.14e+100},
            {-0.7211965654669841, -3.14e+100, -3.14e+100, -0.6658631448798212}
    }; //    A：隐藏状态间的转移概率矩阵；


    private double[] InitPro = new double[]{0.7, -3.14e+100, -3.14e+100, 0.3}; //B,E,M,S    //    PI：初始概率分布（隐藏状态的初始概率分布）；
//    B：输出矩阵（即隐藏状态到输出状态的概率）；

    private void hmm(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
        }
    }


    public static void main(String[] args) {
        String s = "小明硕士毕业于中国科学院计算所";
        MSegment ws = new MSegment();
        ws.hmm(s);
    }

}
