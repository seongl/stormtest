//import java.io.IOException;
//
//import org.apache.avro.io.DatumReader;
//import org.apache.avro.io.Decoder;
//import org.apache.avro.io.DecoderFactory;
//import org.apache.avro.specific.SpecificDatumReader;
//import org.apache.avro.specific.SpecificRecord;
//
//import com.ebay.sesna.avro.models.GenericEvent;
//import com.ebay.sesna.avro.models.NativeAppBeacon;
//import com.ebay.sesna.avro.models.NativeBeacon;

public class AvroDeserializer {
    public static void main(String[] args) {
        System.out.println("AAA");
    }

//    public GenericEvent deserializeToGenericEvent(byte[] value) throws IOException {
//        DatumReader<GenericEvent> reader = new SpecificDatumReader<GenericEvent>(GenericEvent.class);
//        Decoder decoder = DecoderFactory.get().binaryDecoder(value, null);
//        GenericEvent result = reader.read(null, decoder);
//        return result;
//    }
//
//    public NativeAppBeacon deserializeToNativeAppBeacon(byte[] value) throws IOException {
//        DatumReader<NativeAppBeacon> reader = new SpecificDatumReader<NativeAppBeacon>(NativeAppBeacon.class);
//        Decoder decoder = DecoderFactory.get().binaryDecoder(value, null);
//        NativeAppBeacon result = reader.read(null, decoder);
//        return result;
//    }
//
//    public NativeBeacon deserializeToNativeBeacon(byte[] value) throws IOException {
//        DatumReader<NativeBeacon> reader = new SpecificDatumReader<NativeBeacon>(NativeBeacon.class);
//        Decoder decoder = DecoderFactory.get().binaryDecoder(value, null);
//        NativeBeacon result = reader.read(null, decoder);
//        return result;
//    }
//
//    public <T extends SpecificRecord> T deserialize(byte[] data, Class<T> readerClass) throws IOException {
//        SpecificDatumReader<T> reader = new SpecificDatumReader<T>(readerClass);
//        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
//        return reader.read(null, decoder);
//    }
}
