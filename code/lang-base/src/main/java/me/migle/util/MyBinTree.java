package me.migle.util;

/**
 * Created by migle on 2017/4/26.
 * 二叉查找树，二叉平衡树，红黑树
=======
 */


public class MyBinTree<V extends Comparable> {
    private Node root;
    private class Node implements Comparable<Node>{
        private Node left;
        private Node right;
        private V value;
        private int level=0;
        private int height=0;
        public Node(V value) {
            this.value = value;
        }

        public void print(String x){
            System.out.println(x+" " + this.value);
            if(this.left != null){
                this.left.print("L:" +level + " "+ x+ " ");
            }
            if(this.right != null){
                this.right.print("R:"+level + " "+ x+ " ");
        public Node(V value) {
            this.value = value;
        }
        public void print(String x){
            System.out.println(x+" " + this.value);
            if(this.left != null){
                this.left.print("L:" + x+ " ");
            }
            if(this.right != null){
                this.right.print("R:"+ x+ " ");
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "left=" + left +
                    ", right=" + right +
                    ", value=" + value +
                    ", level=" + level +
                    ", height=" + height +
                    '}';
        }

        @Override
        public int compareTo(Node o) {
            return value.compareTo(o.value);
        }
    }

    public MyBinTree(V root){
        this.root = new Node(root);
    }

    //二叉查找树可以表示动态的数据集合，对于给定的数据集合，在建立一颗二叉查找树时，二叉查找树的结构形态与关键字的插入顺序有关。如果全部或者部分地按照关键字的递增或者递减顺序插入二叉查找树的结点，则所建立的二叉查找树全部或者在局部形成退化的单分支结构。在最坏的情况下，二叉查找树可能完全偏斜，高度为n，其平均与最坏的情况下查找时间都是O(n)；而最好的情况下，二叉查找树的结点尽可能靠近根结点，其平均与最好情况的查找时间都是O(logn)。
    // 因此，我们希望最理想的状态下是使二叉查找树始终处于良好的结构形态。
    public  void addNode(V val){
        Node n = new Node(val);
        if (this.root == null) {
            this.root = n;
            this.root.height++;
        }else{
            addNode(this.root,n);
         }
    }
    public Node findNode(V val){
        Node n = new Node(val);
      return findNode(this.root,n);
    }

    public void deleteNode(V val){
        //TODO
    }

    public void toAVL(){
        //转换成平衡二叉树
    }
    public Node findNode(Node s,Node n){
        if(s.compareTo(n) == 0){
            return s;
        }
        if(s.compareTo(n) > 0 && s.left != null){
            return findNode(s.left,n);
        }
        if(s.compareTo(n) < 0 && s.right != null){
            return findNode(s.right,n);
        }
        return null;
    }


    public void addNode(Node pNode ,Node cNode){
        cNode.level++;
        if(pNode.compareTo(cNode)>0){
            if(pNode.left == null) {
                pNode.left = cNode;
            }else{
                addNode(pNode.left,cNode);
            }
        }else{
            if(pNode.right == null) {
                pNode.right = cNode;
            }else{
                addNode(pNode.right,cNode);
            }
        }
    }


    @Override
    public String toString() {
        return "MyBinTree{" +
                 root +
                '}';
    }

    public void printNode(){
        this.root.print(" ");
    }

    public static void main(String[] args) {
        MyBinTree<String> mbt = new MyBinTree<>("M");
        mbt.addNode("B");
        mbt.addNode("A");

        mbt.addNode("C");
        mbt.addNode("D");
        mbt.addNode("X");
        mbt.addNode("Y");
        mbt.addNode("Q");
        mbt.addNode("Z");
        mbt.printNode();

        System.out.println("Found it:"+ mbt.findNode("M").height);
        System.out.println("Found it:"+ mbt.findNode("X").height);
        System.out.println("Found it:"+ mbt.findNode("Y").height);
        System.out.println("Found it:"+ mbt.findNode("Z"));
        System.out.println("Found it:"+ mbt.findNode("Y"));
        //System.out.println(mbt);
    }
}
