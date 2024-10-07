package maystreamer.config;


import com.google.gson.JsonObject;
import maystreamer.Util.JsonUtils;

import java.io.IOException;

public class Configuration {

    public static JsonObject load(final String resourceName) throws IOException {
        return JsonUtils.fromJson(resourceName);
    }
}