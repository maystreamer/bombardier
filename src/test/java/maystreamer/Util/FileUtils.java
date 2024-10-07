package maystreamer.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static String read(final Path path) throws IOException {
        return Files.readString(path);
    }

    public static String readFromResource(final String resource) throws IOException {
        Path path = getResourcePath(resource);
        return read(path);
    }

    public static Path getResourcePath(String resourceName) {
        // Get the resource URL from the class loader
        var resourceUrl = FileUtils.class.getClassLoader().getResource(resourceName);
        if (resourceUrl != null) {
            // Convert the URL to a Path
            return Paths.get(resourceUrl.getPath());
        }
        return null;
    }
}