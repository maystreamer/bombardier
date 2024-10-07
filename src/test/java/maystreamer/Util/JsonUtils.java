package maystreamer.Util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;

public class JsonUtils {
    public final static Gson GSON = new Gson();
    static JsonParser JSON_PARSER = new JsonParser();

    public static JsonObject fromJson(final String resourceFileName) throws IOException {
        try (FileReader reader = new FileReader(String.valueOf(FileUtils.getResourcePath(resourceFileName)))) {
            // Parse the JSON file to a JsonObject
            return JSON_PARSER.parse(reader).getAsJsonObject();
        } catch (IOException e) {
            throw e;
        }
    }

    public static JsonObject toJson(final String jsonString) {
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        }
        return new JsonObject();
    }

    // Method with defaultVal parameter
    public static String value(final JsonObject payload, final String key, final String defaultVal) {
        if (null != payload && !payload.isJsonNull()) {
            JsonElement element = payload.get(key);
            if (null != element && !element.isJsonNull()) {
                return element.getAsString();
            }
        }
        return defaultVal;
    }

    // Overloaded method without defaultVal
    public static String value(final JsonObject payload, final String key) {
        return value(payload, key, null);
    }
}