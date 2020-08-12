package com.evgenltd.hnhtool.analyzer.serializer;

import com.evgenltd.hnhtool.analyzer.model.MessageColumn;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MessageColumnMarshaller {

    private static final Logger log = LogManager.getLogger(MessageColumnMarshaller.class);

    public static final class Serializer extends JsonSerializer<MessageColumn> {
        @Override
        public void serialize(
                final MessageColumn messageColumn,
                final JsonGenerator jsonGenerator,
                final SerializerProvider serializerProvider
        ) throws IOException {
            jsonGenerator.writeBooleanField("enabled", messageColumn.isEnabled());
            jsonGenerator.writeStringField("name", messageColumn.getName());
            jsonGenerator.writeStringField("path", messageColumn.getPath().toString());
            jsonGenerator.writeNumberField("width", messageColumn.getWidth());
        }
    }

    public static final class Deserializer extends JsonDeserializer<MessageColumn> {
        @Override
        public MessageColumn deserialize(
                final JsonParser jsonParser, final DeserializationContext deserializationContext
        ) throws IOException {
            final MessageColumn messageColumn = new MessageColumn();
            final JsonNode root = jsonParser.getCodec().readTree(jsonParser);
            messageColumn.setEnabled(root.get("enabled").asBoolean());
            messageColumn.setName(root.get("name").asText());
            final JsonNode pathNode = root.get("path");
            if (pathNode != null) {
                final String path = pathNode.asText();
                try {
                    messageColumn.setPath(JsonPointer.compile(path));
                } catch (final IllegalArgumentException e) {
                    log.error(e);
                }
            }
            messageColumn.setWidth(root.get("width").asDouble());
            return messageColumn;
        }
    }

}
