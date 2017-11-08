
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.kafka.spout.KafkaSpoutTuplesBuilder;
import org.apache.storm.kafka.spout.KafkaSpoutTuplesBuilderNamedTopics;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.utils.Utils;
//import io.ebay.rheos.client.schema.meta.SchemaColMeta;
//import io.ebay.rheos.client.schema.store.local.ColumnMetaStore;
import io.ebay.rheos.schema.avro.GenericRecordDomainDataDecoder;
import io.ebay.rheos.schema.avro.RheosEventDeserializer;
import io.ebay.rheos.schema.avro.RheosEventSerializer;
import io.ebay.rheos.schema.avro.SchemaRegistryAwareAvroDeserializerHelper;
import io.ebay.rheos.schema.avro.SchemaRegistryAwareAvroSerializerHelper;
import io.ebay.rheos.schema.event.RheosEvent;
//import io.ebay.rheos.tuplebuilder.ChangeDataTupleBuilder;

/**
 * Created by slee8 on 11/7/17.
 */
public class LocalSpoutTest {
    private static String RHEOS_DB_HEADER = "rheosDbHeader";
    private static String CURRENT_RECORD = "currentRecord";
    private static int SCHEMA_ID = 2346;

    public static class TestSpout extends BaseRichSpout {

        private Schema ebayItemSchema;
        private SpoutOutputCollector collector;
        private RheosEventSerializer serialize;
        private KafkaSpoutTuplesBuilder tupleBuilder = null;

        @Override
        public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
            this.collector = collector;
            this.tupleBuilder = new KafkaSpoutTuplesBuilderNamedTopics.Builder<>(new ChangeDataTupleBuilder<String, String>("topic")).build();
            try {
                Map<String, Object> config = new HashMap<>();
                config.put("rheos.services.urls", "http://rheos-mgmt.qa.ebay.com");
                SchemaRegistryAwareAvroSerializerHelper<GenericRecord> serializerHelper = new SchemaRegistryAwareAvroSerializerHelper<>(config, GenericRecord.class);
                ebayItemSchema = serializerHelper.getSchema(SCHEMA_ID);

                serialize = new RheosEventSerializer();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void nextTuple() {
            Utils.sleep(3000);

            collector.emit(tupleBuilder.buildTuple(DbRecord()));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("topic", "partition","offset","key", "value"));
        }

        private ConsumerRecord<byte[], RheosEvent> DbRecord() {

            RheosEvent rheosEvent = new RheosEvent(ebayItemSchema);

            rheosEvent.setEventId("100");
            rheosEvent.setEventCreateTimestamp(System.currentTimeMillis());
            rheosEvent.setEventSentTimestamp(System.currentTimeMillis());
            rheosEvent.setProducerId("your-producer-id");
            rheosEvent.setSchemaId(SCHEMA_ID);

            //db-streaming header info
            GenericRecord rheosDbHeader = getRheosDbHeader();
            rheosEvent.put(RHEOS_DB_HEADER, rheosDbHeader);

            //db-streaming record info
            GenericRecord record = new GenericData.Record(ebayItemSchema.getField(CURRENT_RECORD).schema());
            record.put("marketplace", "mpt");
            record.put("id", "100");
            rheosEvent.put(CURRENT_RECORD, record);

            return new ConsumerRecord("topic", 1, 1, null, serialize.serialize(null, rheosEvent));
        }

        private GenericRecord getRheosDbHeader(){
            GenericRecord rheosDbHeader = new GenericData.Record(ebayItemSchema.getField(RHEOS_DB_HEADER).schema());

            rheosDbHeader.put("schema", "staging_cg10_user");
            rheosDbHeader.put("table", "ebay_items");
            rheosDbHeader.put("opType", "UPDATE");
            rheosDbHeader.put("opTs", "2016-01-19 12:21:00.000385");
            rheosDbHeader.put("recordRowid", "AACO46ALGAAEzY2AAB");
            rheosDbHeader.put("tranScn", "3704597526828");

            rheosDbHeader.put("trailSeqno", 797L);
            rheosDbHeader.put("trailRba", 54247573L);
            rheosDbHeader.put("logPosition", 185581256L);
            rheosDbHeader.put("logRba", 174431L);
            rheosDbHeader.put("dbName", "S8I2");

            rheosDbHeader.put("ggHostName", "qadb208");

            rheosDbHeader.put("numColumns", 10);

            rheosDbHeader.put("updColumns", null);
            rheosDbHeader.put("nullColumns", null);
            rheosDbHeader.put("missingColumns", null);
            rheosDbHeader.put("beforeNullColumns", null);

            rheosDbHeader.put("updCompressed", Boolean.TRUE);
            rheosDbHeader.put("schemaType", "avro");
            rheosDbHeader.put("SchemaName", "ebay_items");

            Map latencyTsMap = new HashMap();
            latencyTsMap.put("latency", System.currentTimeMillis());
            rheosDbHeader.put("latencyTsMap", latencyTsMap);

            Map timestamps = new HashMap();
            timestamps.put("latency", System.currentTimeMillis());
            rheosDbHeader.put("timestamps", timestamps);

            return rheosDbHeader;
        }

    }

    public static class TestBolt extends BaseRichBolt{

        private Schema schema;
        private RheosEventDeserializer deserialize;
        private GenericRecordDomainDataDecoder decoder;
        private SchemaRegistryAwareAvroDeserializerHelper deserializerHelper;

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            Map<String, Object> config = new HashMap<>();
            config.put("rheos.services.urls", "http://rheos-mgmt.qa.ebay.com");
            decoder = new GenericRecordDomainDataDecoder(config);

            deserialize = new RheosEventDeserializer();
            deserializerHelper = new SchemaRegistryAwareAvroDeserializerHelper<>(config, GenericRecord.class);
        }

        @Override
        public void execute(Tuple input) {
            RheosEvent event = deserialize.deserialize(null, (byte[])input.getValueByField("value"));
            System.out.println("event.eventId=" + event.getEventId());
            GenericRecord eventRecord = decoder.decode(event);

            //System.out.println("rheosDbHeader==>" + (GenericRecord)eventRecord.get("rheosDbHeader"));
            //System.out.println("currentRecord==>" + (GenericRecord)eventRecord.get("currentRecord"));

            schema = deserializerHelper.getSchema(event.getSchemaId());
            Schema tableSchema = schema.getField(CURRENT_RECORD).schema();

            System.out.println("AAA");
//            ColumnMetaStore colMetaStore = new ColumnMetaStore();
//            SchemaColMeta colMeta = colMetaStore.get(tableSchema);
//
//            for (String pkCol : colMeta.getPrimaryKeyColumns()) {
//                System.out.println("key column name => " + pkCol);
//            }

        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {

        }
    }


    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("testSpout", new TestSpout(), 1);
        builder.setBolt("testBolt", new TestBolt(), 1).shuffleGrouping("testSpout");

        Config config = new Config();
        //config.setDebug(true);

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("mytopology", config, builder.createTopology());
    }
}
