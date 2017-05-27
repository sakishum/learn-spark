/**
  * Created by migle on 2017/4/28.
  */

object ClosuresTest {
  def inr(): () => Int ={
    var i = 0;
    return ()=>{i=i+1;i}
  }

def call(f:()=>Int):Int={
  println(f())
  println(f())
  println(f())
  f()
}

  def main(args: Array[String]): Unit = {
    val x = inr()
    println(x()) //1
    println(x()) //2
    println(x()) //3

    val x2 = ()=>{
      var i = 0
      i=i+1
      i
    }

  call(x2)


  }
}
