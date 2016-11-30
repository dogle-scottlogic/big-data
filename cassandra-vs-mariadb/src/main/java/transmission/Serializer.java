package transmission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by lcollingwood on 30/11/2016.
 */
public class Serializer {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String Serialize(Object o) {
        String serialized = null;
        try {
            serialized = Serializer.objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return serialized;
    }
}
