package test;/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */


import static org.apache.storm.kafka.spout.KafkaSpoutConfig.FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.kafka.spout.KafkaSpoutRetryService;
import org.apache.storm.kafka.spout.KafkaSpoutStreams;
import org.apache.storm.kafka.spout.KafkaSpoutStreamsNamedTopics;
import org.apache.storm.kafka.spout.KafkaSpoutTuplesBuilder;
import org.apache.storm.kafka.spout.KafkaSpoutTuplesBuilderNamedTopics;
//import org.apache.storm.kafka.spout.test.test.TopicTest2TupleBuilder;
//import org.apache.storm.kafka.spout.test.test.TopicsTest0Test1TupleBuilder;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class KafkaSpoutTopologyMainNamedTopics {
    private static final String[] STREAMS = new String[]{"test_stream","test1_stream","test2_stream"};
    private static final String[] TOPICS = new String[]{"test","test1","test2"};


    public static void main(String[] args) throws Exception {
        new KafkaSpoutTopologyMainNamedTopics().runMain(args);
    }

    protected void runMain(String[] args) throws Exception {
        if (args.length == 0) {
            submitTopologyLocalCluster(getTopolgyKafkaSpout(), getConfig());
        } else {
            submitTopologyRemoteCluster(args[0], getTopolgyKafkaSpout(), getConfig());
        }

    }

    protected void submitTopologyLocalCluster(StormTopology topology, Config config) throws InterruptedException {
//        LocalCluster cluster = new LocalCluster();
//        cluster.submitTopology("test", config, topology);
//        stopWaitingForInput();
    }

    protected void submitTopologyRemoteCluster(String arg, StormTopology topology, Config config) throws Exception {
        StormSubmitter.submitTopology(arg, config, topology);
    }

    protected void stopWaitingForInput() {
        try {
            System.out.println("PRESS ENTER TO STOP");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Config getConfig() {
        Config config = new Config();
        config.setDebug(true);
        return config;
    }

    protected StormTopology getTopolgyKafkaSpout() {
        final TopologyBuilder tp = new TopologyBuilder();
        tp.setSpout("kafka_spout", new KafkaSpout<>(getKafkaSpoutConfig(getKafkaSpoutStreams())), 1);
        tp.setBolt("kafka_bolt", new KafkaSpoutTestBolt()).shuffleGrouping("kafka_spout", STREAMS[0]);
        tp.setBolt("kafka_bolt_1", new KafkaSpoutTestBolt()).shuffleGrouping("kafka_spout", STREAMS[2]);
        return tp.createTopology();
    }

    protected KafkaSpoutConfig<String,String> getKafkaSpoutConfig(KafkaSpoutStreams kafkaSpoutStreams) {
        return new KafkaSpoutConfig.Builder<String, String>(getKafkaConsumerProps(), kafkaSpoutStreams, getTuplesBuilder(), getRetryService())
                .setOffsetCommitPeriodMs(10_000)
                .setFirstPollOffsetStrategy(UNCOMMITTED_EARLIEST)//please only use either UNCOMMITTED_EARLIEST or UNCOMMITTED_LATEST
                .setPollTimeoutMs(2000)//default value is 2 seconds
                .setMaxUncommittedOffsets(200000)//by default it is 200000, please do not put a very small value
//                .setOutOfRangeCheckPeriodMs(2 * 60 * 1000) //for testing can put as 2 minutes, for production, can remove this line to use the default value which is 5 minutes
//                .setOutOfRangeAbandonPeriodMs(5 * 60 * 1000)//for testing can put as 5 minutes, for production, can remove this line to use the default value which is 30 minutes
                .build();
    }

    protected KafkaSpoutRetryService getRetryService() {
        return new KafkaSpoutRetryExponentialBackoff(getTimeInterval(500, TimeUnit.MICROSECONDS),
                TimeInterval.milliSeconds(2), Integer.MAX_VALUE, TimeInterval.seconds(10));
    }

    protected TimeInterval getTimeInterval(long delay, TimeUnit timeUnit) {
        return new TimeInterval(delay, timeUnit);
    }

    protected Map<String,Object> getKafkaConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        //if(noNeedSecurity)
        {
            setConsumerPropsWithoutSecurity(props);
        }
        //else if(needSecurity)
        {
            setConsumerPropsWithSecurity(props);
        }
        return props;
    }

    private void setConsumerPropsWithoutSecurity(Map<String, Object> props) {
        // This is the proxy addresses
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "rheos-kafka-proxy-1.lvs02.dev.ebayc3.com:9092,rheos-kafka-proxy-2.lvs02.dev.ebayc3.com:9092,rheos-kafka-proxy-3.lvs02.dev.ebayc3.com:9092,rheos-kafka-proxy-1.phx02.dev.ebayc3.com:9092,rheos-kafka-proxy-2.phx02.dev.ebayc3.com:9092,rheos-kafka-proxy-3.phx02.dev.ebayc3.com:9092");
        // This is the consumer name that is registered in Rheos
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-name-registered-in-Rheos");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        // This is client id registered in Rheos. This much match what is stored in Rheos or no data can be consumed
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "client-id-registered-in-Rheos");

        /* below configs are up to users to define.
        Full config list can be found at http://kafka.apache.org/documentation.html#newconsumerconfigs
        */
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 64 * 1024);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 16 * 1024);
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());
    }

    private void setConsumerPropsWithSecurity(Map<String, Object> props) {
        // This is the proxy addresses
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "rheos-kafka-proxy-1.lvs02.dev.ebayc3.com:9093,rheos-kafka-proxy-2.lvs02.dev.ebayc3.com:9093,rheos-kafka-proxy-3.lvs02.dev.ebayc3.com:9093,rheos-kafka-proxy-1.phx02.dev.ebayc3.com:9093,rheos-kafka-proxy-2.phx02.dev.ebayc3.com:9093,rheos-kafka-proxy-3.phx02.dev.ebayc3.com:9093");
        // This is the consumer name that is registered in Rheos
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-name-registered-in-Rheos");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        // This is client id registered in Rheos. This much match what is stored in Rheos or no data can be consumed
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "client-id-registered-in-Rheos");

        /* Security properties
        See here https://github.corp.ebay.com/streaming-contrib/rheos-api/wiki/Rheos-0.0.4-SNAPSHOT-Auth&Auth#configure-iaf-in-kafka-client
        for how to import the required dependency.
        */
        props.put("sasl.mechanism", "IAF");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.login.class", "io.ebay.rheos.kafka.security.iaf.IAFLogin");
        props.put("sasl.callback.handler.class", "io.ebay.rheos.kafka.security.iaf.IAFCallbackHandler");

        /* below configs are up to users to define.
        Full config list can be found at http://kafka.apache.org/documentation.html#newconsumerconfigs
        */
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 64 * 1024);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 16 * 1024);
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());
    }

    protected KafkaSpoutTuplesBuilder<String, String> getTuplesBuilder() {
        return new KafkaSpoutTuplesBuilderNamedTopics.Builder<>(
                new TopicsTest0Test1TupleBuilder<String, String>(TOPICS[0], TOPICS[1]),
                new TopicTest2TupleBuilder<String, String>(TOPICS[2]))
                .build();
    }

    protected KafkaSpoutStreams getKafkaSpoutStreams() {
        final Fields outputFields = new Fields("topic", "partition", "offset", "key", "value");
        final Fields outputFields1 = new Fields("topic", "partition", "offset");
        return new KafkaSpoutStreamsNamedTopics.Builder(outputFields, STREAMS[0], new String[]{TOPICS[0], TOPICS[1]})  // contents of topics test, test1, sent to test_stream
                .addStream(outputFields, STREAMS[0], new String[]{TOPICS[2]})  // contents of topic test2 sent to test_stream
                .addStream(outputFields1, STREAMS[2], new String[]{TOPICS[2]})  // contents of topic test2 sent to test2_stream
                .build();
    }
}