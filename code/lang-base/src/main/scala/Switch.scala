import com.asiainfo.Conf

/**
 * Created by migle on 2016/9/9.
 */
class Switch {
  def main(args: Array[String]) {

  }

  def rule(line:Map[String,String])={
    line.get("s_topic").get match {
      case Conf.consume_topic_netpay => {
        r.getEventid.equalsIgnoreCase(Conf.eventNetpay)
      }
      case Conf.consume_topic_usim => {
        r.getEventid.equalsIgnoreCase(Conf.eventUSIMChange)
      }
      case Conf.consume_topic_order => {
        r.getEventid.equalsIgnoreCase(Conf.eventBusiOrder)
      }
      case _ => false
    }
  }
}
