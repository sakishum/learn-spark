import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.exceptions.YarnException;

import java.io.IOException;

/**
 * Created by migle on 2016/6/14.
 */
public class SimpleYarnApp {

    public static void main(String[] args) throws IOException, YarnException {
        YarnClient yarnClient = YarnClient.createYarnClient();
        Configuration conf = new Configuration();
       // conf.set("fs.defaultFS","hdfs://vm-centos-01:9999");
        conf.set("yarn.resourcemanager.address", "vm-centos-01:8032");
        yarnClient.init(conf);
        yarnClient.start();
        YarnClientApplication app = yarnClient.createApplication();

        GetNewApplicationResponse appResponse = app.getNewApplicationResponse();
        System.out.println(appResponse.getApplicationId());

        //yarn node -list
        System.out.println(yarnClient.getYarnClusterMetrics().getNumNodeManagers());

    }
}
