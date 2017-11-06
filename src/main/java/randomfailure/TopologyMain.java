package randomfailure;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import reliable.ReliableWordReader;

/**
 * Created by slee8 on 11/6/17.
 */
public class TopologyMain {
    public static void main(String[] args) {
        // Topology definition
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("Reliable-Spout", new ReliableWordReader());
        builder.setBolt("Randome-Failure-Bolt",
                new RandomFailureBolt()).shuffleGrouping("Reliable-Spout");

        // Configuration
        Config conf = new Config();
        conf.setDebug(true);
        conf.put("fileToRead", "/Users/slee8/sample.txt");

        // Topology run
        LocalCluster cluster = new LocalCluster();
        try {
            cluster.submitTopology("Random-Fail-Topology", conf, builder.createTopology());
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cluster.shutdown();;
        }
    }
}
