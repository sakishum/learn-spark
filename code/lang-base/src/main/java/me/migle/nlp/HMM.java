package me.migle.nlp;

import java.io.*;
import java.util.*;

/**
 * Created by migle on 2017/3/20.
 */
public class HMM {
    private List<Character> words = new ArrayList<Character>(); // 观察值序列

    private char[] hiddenStatus = new char[]{'B', 'M', 'E', 'S'};  //S：隐藏状态集合；每个状态代表该字在词语中的位置,开始、结束、中间、单独成词
    //    N：观察状态集合；  单个字的状态
    //dim1:bems,dim2:bems
    private double[][] transMatrix; //    A：隐藏状态间的转移概率矩阵；  4*4
    private double[] InitPro = {0, 0, 0, 0};   //    PI：初始概率分布（隐藏状态的初始概率分布）；


    //    B： 观测状态转移概率矩阵 B（即隐藏状态到输出状态的概率，）；观察值到隐藏状态的概率，即每个汉字处理于词首、尾、中间，单独成词的概率
    //根据学习语料库的增多，此矩阵趋于正常状态，分词时根据这个状态来对输入分词
    Map<Character, int[]> matrixB = new HashMap<>();

    public HMM() {

    }

    private void viterbi(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
        }
    }

    public void train(String traindata) {
        //得出 PI,B,A
        try (BufferedReader br = new BufferedReader(new FileReader(traindata))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                addTrain(line);
            }
            matrixB.forEach((k, v) -> System.out.print("\n" + k + ":" + Arrays.toString(v)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTrain(String line) {
        String[] words = line.split(" ");
        //统计每个字在B\E\M|S位置的次数
        for (String word : words) {
            if (word.length() < 1) continue;
            if (word.length() == 1) {
                //S
                matrixBTotal(word.charAt(0), 3);
            } else {
                //B
                matrixBTotal(word.charAt(0), 0);

                for (int i = 1; i < (word.length() - 1); i++) {
                    //M
                    matrixBTotal(word.charAt(i), 1);

                }
                //E
                matrixBTotal(word.charAt(word.length() - 1), 2);
            }
        }
    }

    private void getB() {
        matrixB.forEach((k,v)->{

        });

    }

    private void matrixBTotal(Character key, int state) {
        if (matrixB.containsKey(key)) {
            matrixB.get(key)[state]++;
        } else {
            int[] states = new int[hiddenStatus.length];
            states[state] = 1;
            matrixB.put(key, states);
        }
    }

    public static void main(String[] args) {
        //String s = "小明硕士毕业于中国科学院计算所";
        HMM ws = new HMM();
        //ws.viterbi(s);
        System.out.println((new File(".")).getAbsoluteFile());
        ws.train("E:\\workspace\\learn-spark\\code\\lang-base\\src\\main\\resources\\data/icwb2-data/training/pku_training.utf8");

    }

}
