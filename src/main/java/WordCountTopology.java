import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.ShellBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.storm.kafka.spout.KafkaSpoutConfig.FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST;

//import io.ebay.rheos.*;


import org.apache.storm.kafka.spout.*;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;

/**
 * Created by slee8 on 11/6/17.
 */
public class WordCountTopology {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordCountTopology.class);

    private static final String spoutPrefix = "lcr-";
    private static final String boltPrefix = "fcr-";
    private static final String streamSuffix = "-st";//stream

//    private static Configuration config;
//
    public static class SplitSentence extends ShellBolt implements IRichBolt {
        public SplitSentence() {
            super("python", "splitsentence.py");
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word"));
        }

        @Override
        public Map<String, Object> getComponentConfiguration() {
            return null;
        }
    }

    public static class WordCount extends BaseBasicBolt {
        Map<String, Integer> counts = new HashMap<String, Integer>();

        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {
            String word = tuple.getString(0);
            Integer count = counts.get(word);
            if (count == null)
                count = 0;
            count++;
            counts.put(word, count);
            collector.emit(new Values(word, count));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word", "count"));
        }
    }

    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();

//        TopicInfo tInfo = new TopicInfo("misc.ha.apls_traffic_group.apls_traffic");
//        LOGGER.info("spout for topic:" + tInfo.getTopicName());

        String spoutComponentName = getComponentName(tInfo.getShortName(), spoutPrefix);
        KafkaSpoutStreams stream = getKafkaSpoutStreams(spoutComponentName + streamSuffix, "misc.ha.apls_traffic_group.apls_traffic");
        KafkaSpoutConfig<String,String> kafkaSpoutConfig = getKafkaSpoutConfig(stream, tInfo.getTopicName());
        builder.setSpout(spoutComponentName, new KafkaSpout(kafkaSpoutConfig), 1);




        builder.setSpout("spout", new RandomSentenceSpout(), 5);
        builder.setBolt("split", new SplitSentence(), 8).shuffleGrouping("spout");
        builder.setBolt("count", new WordCount(), 12).fieldsGrouping("split", new Fields("word"));

        Config conf = new Config();
        conf.setDebug(true);

        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);

            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        }
        else {
            conf.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());

            Thread.sleep(10000);

            cluster.shutdown();
        }
    }



    private static String getComponentName(String topicName, String prefix) {
        String[] topicArray;
        String spoutName;
        topicArray = topicName.split("\\.");
        if (topicArray.length >= 3) {
            spoutName = prefix + topicArray[3];
        } else {
            spoutName = prefix + topicName;
        }
        return spoutName;
    }


    public static KafkaSpoutStreams getKafkaSpoutStreams(String streams, String topics) {
        final Fields outputFields = new Fields("topic", "partition", "offset", "key", "value");
        return new KafkaSpoutStreamsNamedTopics.Builder(outputFields, streams, new String[]{topics}).build();
    }

    public static KafkaSpoutConfig<String,String> getKafkaSpoutConfig(KafkaSpoutStreams kafkaSpoutStreams, String topics) {
        return new KafkaSpoutConfig.Builder(getKafkaConsumerProps(), kafkaSpoutStreams, getTuplesBuilder(topics), getRetryService())
                .setOffsetCommitPeriodMs(1_000)
                .setFirstPollOffsetStrategy(UNCOMMITTED_EARLIEST)
                .setMaxUncommittedOffsets(6_000)
                .setPollTimeoutMs(10)
                .setMaxRetries(3)
//                .setOutOfRangeAbandonPeriodMs(5 * 60 * 1000)
//                .setMaxRetryFailCount(10000)
//                .setMaxRetryFailTimeWindow(10 * 60 * 1000)
                .setMsgTimeOutMs(30 * 1000)
                .setAutoOffsetResetStrategy(KafkaSpoutConfig.AutoOffsetResetStrategy.EARLIEST)
                .build();
    }


    private static KafkaSpoutRetryService getRetryService() {
		/*return new KafkaSpoutRetryExponentialBackoff(getTimeInterval(500, TimeUnit.MICROSECONDS),
				TimeInterval.milliSeconds(2), Integer.MAX_VALUE, TimeInterval.seconds(10));*/
        return new KafkaSpoutRetryImmediate();
    }


    public static KafkaSpoutTuplesBuilder<String, String> getTuplesBuilder(String topics) {
        return (KafkaSpoutTuplesBuilder<String, String>) new KafkaSpoutTuplesBuilderNamedTopics.Builder();

//        return new KafkaSpoutTuplesBuilderNamedTopics.Builder<>(
//                new ChangeDataTupleBuilder<String, String>(topics)).build();
    }

    public static Map<String,Object> getKafkaConsumerProps() {
        Map<String, Object> props = new HashMap<>();
//        props.put(KafkaSpoutConfig.Consumer.RHEOS_SERVICES_URLS, config.getChangeDataMergeConfig().getMetadataRegistryUrls());
//        props.put(KafkaSpoutConfig.Consumer.CONSUMER_GROUP_ID, config.getChangeDataMergeConfig().getLcrConsumerGroupId());
//        props.put(KafkaSpoutConfig.Consumer.KAFKA_CLUSTER_REGION, config.getChangeDataMergeConfig().getRegion());
        props.put(KafkaSpoutConfig.Consumer.CLUSTER_TYPE, "local");
        return props;
    }

    //oradb.core4-lcr.caty.ebay-checkout-trans:1:cg-ck-trans
    private static class TopicInfo {
        private String topicName;
        private int numInstances = 0;
        private String shortName;

        public TopicInfo(String topicNameFromLst) {
            String[] topicArray;
            topicArray = topicNameFromLst.split(":");
            if (topicArray.length >= 2) {

                this.topicName = topicArray[0];
                this.numInstances = Integer.parseInt(topicArray[1]);
                this.shortName = topicArray[2];
            } else {
                this.topicName = topicNameFromLst;
            }
        }

        public String getTopicName() {
            return topicName;
        }

        public int getNumInstances() {
            return numInstances;
        }

        public String getShortName(){
            return shortName;
        }
    }

}
