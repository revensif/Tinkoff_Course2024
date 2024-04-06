package edu.java.bot.configuration.serializer;

import edu.java.bot.dto.request.LinkUpdateRequest;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;

public class GeneralUpdateSerializer implements Serializer<Object> {

    private final Serializer<LinkUpdateRequest> requestSerializer = new LinkUpdateRequestSerializer();
    private final ByteArraySerializer byteArraySerializer = new ByteArraySerializer();

    @Override
    public byte[] serialize(String topic, Object data) {
        if (data instanceof LinkUpdateRequest) {
            return requestSerializer.serialize(topic, (LinkUpdateRequest) data);
        } else {
            return byteArraySerializer.serialize(topic, (byte[]) data);
        }
    }
}
