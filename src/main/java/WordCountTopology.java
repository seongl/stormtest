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

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
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

        // 1. Set Kafka Spout
        String spoutComponentName = getComponentName("apls_traffic", spoutPrefix);
        KafkaSpoutStreams stream = getKafkaSpoutStreams(spoutComponentName + streamSuffix, "misc.ha.apls_traffic_group.apls_traffic");
        KafkaSpoutConfig<String, String> kafkaSpoutConfig = getKafkaSpoutConfig(stream, "misc.ha.apls_traffic_group.apls_traffic");
//        kafkaSpoutConfig.getKafkaSpoutStreams().
        builder.setSpout(spoutComponentName, new KafkaSpout(kafkaSpoutConfig), 1);

        // 2. Set Bolt
        builder.setBolt("printer", new PrinterBolt() );

//        builder.setSpout("spout", new RheosSpout(), 1);
//        builder.setBolt("split", new SplitSentence(), 8).shuffleGrouping("spout");
//        builder.setBolt("count", new WordCount(), 12).fieldsGrouping("split", new Fields("word"));

        Config conf = new Config();
        conf.put("level", "info");
        conf.setDebug(true);
        conf.setMessageTimeoutSecs(500);
//        conf.registerSerialization(Env.class);
//        conf.registerSerialization(GenericEvent.class);
        conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS,
                "-Xms3g -Xmx3g -XX:NewSize=2g -XX:MaxNewSize=2g -XX:SurvivorRatio=6 -XX:+UseParallelGC -XX:+UseParallelOldGC");

        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);

            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        }
        else {
            conf.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());

            Thread.sleep(10000);

//            cluster.shutdown();
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

    public static KafkaSpoutConfig<String, String> getKafkaSpoutConfig(KafkaSpoutStreams kafkaSpoutStreams, String topics) {
        return new KafkaSpoutConfig.Builder(getKafkaConsumerProps(), getKafkaSpoutStreams(), getTuplesBuilder(), getRetryService())
//                .setKeyDeserializer(new org.apache.kafka.common.serialization.ByteArrayDeserializer())
//                .setValueDeserializer(new io.ebay.rheos.schema.avro.RheosEventDeserializer())
        .setOffsetCommitPeriodMs(10_000)
        // please only use either UNCOMMITTED_EARLIEST or UNCOMMITTED_LATEST
        .setFirstPollOffsetStrategy(UNCOMMITTED_EARLIEST)
        // default value is 2 seconds
        .setPollTimeoutMs(2_000)
        // by default it is 200000, please do not put a very small value
        /*
         * Default msg timeout is 20secs. Make sure that the max uncommitted configuration that is set is sufficient.
         * What can happen (and actually happened) is that the Spout can keep emitting until it reaches the max
         * uncommitted limit but the first one that it emitted actually didn't respond back. If the msg timeout
         * configuration is big enough so that in that time the max uncommitted is reached, then Spout cannot even retry
         * the failed one and so it will stop further processing
         */
        .setMaxUncommittedOffsets(4_000_000)
        .setKeepOrdering(false)
        .setMsgTimeOutMs(30_1000).build();
    }

    protected static KafkaSpoutStreams getKafkaSpoutStreams() {
        final Fields outputFields = new Fields("key", "value", "partitionAndOffset", "rheosTimestamp",
                "spoutTimestamp");
        return new KafkaSpoutStreamsNamedTopics.Builder(outputFields, "default",
                new String[] { "misc.ha.apls_traffic_group.apls_traffic" }).build();
    }


    private static KafkaSpoutRetryService getRetryService() {
		/*return new KafkaSpoutRetryExponentialBackoff(getTimeInterval(500, TimeUnit.MICROSECONDS),
				TimeInterval.milliSeconds(2), Integer.MAX_VALUE, TimeInterval.seconds(10));*/
        return new KafkaSpoutRetryImmediate();
    }


//    public static KafkaSpoutTuplesBuilder<String, String> getTuplesBuilder(String topics) {
////        return (KafkaSpoutTuplesBuilder<String, String>) new KafkaSpoutTuplesBuilderNamedTopics.Builder();
//
//        return new KafkaSpoutTuplesBuilderNamedTopics.Builder<>(
//                new ChangeDataTupleBuilder<String, String>(topics)).build();
//    }


    protected static KafkaSpoutTuplesBuilder<String, String> getTuplesBuilder() {
        return new KafkaSpoutTuplesBuilderNamedTopics.Builder<String, String>(
                new KafkaSpoutTupleBuilder("misc.ha.apls_traffic_group.apls_traffic")).build();
    }




    public static Map<String,Object> getKafkaConsumerProps() {
        Map<String, Object> props = new HashMap<>();
//        props.put(KafkaSpoutConfig.Consumer.RHEOS_SERVICES_URLS, "https://rheos-services.qa.ebay.com");
//        props.put(KafkaSpoutConfig.Consumer.CONSUMER_CLIENT_ID, "1b151f4f-1c54-4198-ba3a-80d14d6b4ada");
//        props.put(KafkaSpoutConfig.Consumer.BOOTSTRAP_SERVERS, "rheos-kafka-proxy-1.phx02.dev.ebayc3.com:9092,rheos-kafka-proxy-2.phx02.dev.ebayc3.com:9092,rheos-kafka-proxy-3.phx02.dev.ebayc3.com:9092,rheos-kafka-proxy-1.lvs02.dev.ebayc3.com:9092,rheos-kafka-proxy-2.lvs02.dev.ebayc3.com:9092,rheos-kafka-proxy-3.lvs02.dev.ebayc3.com:9092");
//        props.put(KafkaSpoutConfig.Consumer.GROUP_ID, "TestAPLSRheosConsumer01");
//        props.put(KafkaSpoutConfig.Consumer.VALUE_DESERIALIZER, "io.ebay.rheos.schema.avro.RheosEventDeserializer");
//        props.put(KafkaSpoutConfig.Consumer.KEY_DESERIALIZER, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
////        props.put(KafkaSpoutConfig.Consumer.KAFKA_CLUSTER_REGION, config.getChangeDataMergeConfig().getRegion());
//        props.put(KafkaSpoutConfig.Consumer.CLUSTER_TYPE, "local");


        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "rheos-kafka-proxy-1.phx02.dev.ebayc3.com:9092,rheos-kafka-proxy-2.phx02.dev.ebayc3.com:9092,rheos-kafka-proxy-3.phx02.dev.ebayc3.com:9092,rheos-kafka-proxy-1.lvs02.dev.ebayc3.com:9092,rheos-kafka-proxy-2.lvs02.dev.ebayc3.com:9092,rheos-kafka-proxy-3.lvs02.dev.ebayc3.com:9092");
        // This is the consumer name that is registered in Rheos
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "TestAPLSRheosConsumer01");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        // This is client id registered in Rheos. This much match what is stored in Rheos or no data can be consumed
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "1b151f4f-1c54-4198-ba3a-80d14d6b4ada");

        /*
         * below configs are up to users to define. Full config list can be found at
         * http://kafka.apache.org/documentation.html#newconsumerconfigs
         */
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // At least once configuration. For 'at-most-once config, set ‘enable.auto.commit’ to true.
        // props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 64 * 1024);
        // props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 16 * 1024 * 1024);

        // The following help to catch up with the producer. Sometimes the producer produces at very high rates > 300k/s
        // and the following configuration will help to fetch more data in one shot (~ 4250 in one poll with 16 MB of
        // max_partition_fetch_bytes_config)
        // TODO : check with producer config. If it is less volume from producer, then try with this config:  64 * 1024
        props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 8 * 1024 * 1024);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 16 * 1024 * 1024); // TODO: 16 * 1024
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());

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
