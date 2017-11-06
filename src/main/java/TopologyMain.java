import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by slee8 on 11/3/17.
 */
public class TopologyMain {
    public static void main(String[] args) throws InterruptedException {
        // Build Topology
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("Yahoo-Finance-Spout", new YfSpout());
        builder.setBolt("Yahoo-Finance-Bolt", new YfBolt(), 3)
                .shuffleGrouping("Yahoo-Finance-Spout");

        StormTopology topology = builder.createTopology();

        // Configuration
        Config config = new Config();
        config.setDebug(true);
        config.put("filetoWrite", "/Users/slee8/Downloads/output.txt");

        // Submit Topology to cluster
         LocalCluster cluster = new LocalCluster();
        try {
//            cluster.submitTopology("Stock-Tracker-Topology", config, topology);
            StormSubmitter.submitTopology("Parallelized-Topology", config, topology);
//            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cluster.shutdown();
        }

    }
}
