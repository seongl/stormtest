package trident;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.builtin.Count;
import org.apache.storm.trident.operation.builtin.Debug;
import org.apache.storm.tuple.Fields;
import wordcount.WordReader;

/**
 * Created by slee8 on 11/6/17.
 */
public class HelloWorldTrident {
    public static void main(String[] args) throws Exception {
        TridentTopology topology = new TridentTopology();

        topology.newStream("lines", new WordReader())
                .each(new Fields("word"),
                        new SplitFunction(),
                        new Fields("word_split"))
                .groupBy(new Fields("word_split"))
                .aggregate(new Count(), new Fields("count"));

// 22222222222222222222
//                .aggregate(new Count(), new Fields("count"))
//                .each(new Fields("count"), new Debug());

// 1111111111111111111111
//                .each(new Fields("word_split"), new Debug());

        Config conf = new Config();
        conf.setDebug(true);
        conf.put("fileToRead", "/Users/slee8/smaple.txt");

        if(args.length != 0 && args[0].equals("remote")) {
            StormSubmitter.submitTopology("Trident-Topology", conf, topology.build());
        } else {
            LocalCluster cluster = new LocalCluster();
            try {
                cluster.submitTopology("Trident-Topology", conf, topology.build());
                Thread.sleep(10000);
            } finally {
                cluster.shutdown();
            }
        }

    }
}
