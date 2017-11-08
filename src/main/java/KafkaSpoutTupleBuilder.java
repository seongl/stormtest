
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import backtype.storm.tuple.Values;



public class KafkaSpoutTupleBuilder extends org.apache.storm.kafka.spout.KafkaSpoutTupleBuilder<String, String> {

    private static final long serialVersionUID = 1L;

    public KafkaSpoutTupleBuilder(String... topics) {
        super(topics);
    }

    @Override
    public List<Object> buildTuple(ConsumerRecord<String, String> rec) {
        return new Values(rec.key(), rec.value(), String.format("%d-%d", rec.partition(), rec.offset()),
                rec.timestamp(), System.currentTimeMillis());
    }
}
