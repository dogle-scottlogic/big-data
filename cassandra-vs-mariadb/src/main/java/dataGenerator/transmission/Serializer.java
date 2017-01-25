package dataGenerator.transmission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

/**
 * Created by lcollingwood on 30/11/2016.
 */
public class Serializer {
    private final static Logger LOG = Logger.getLogger(Serializer.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String Serialize(Object o) {
        String serialized = null;
        try {
            serialized = Serializer.objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to serialize", e);
        }
        return serialized;
    }
}
