package com.evgenltd.hnhtools.common;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * <p>Class uses for loading text resources from classpath.</p>
 * <p></p>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 01:55</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Resources {

    public static String load(@NotNull final InputStream stream) {
        return load(stream, "UTF-8");
    }

    public static String load(@NotNull final InputStream stream, @NotNull final String charset) {
        Assert.valueRequireNonEmpty(stream, "Stream");
        Assert.valueRequireNonEmpty(charset, "Charset");

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (final IOException exception) {
            throw new ApplicationException(exception);
        }
    }

    public static String load(@NotNull final Class<?> classPath, @NotNull final String name) {
        Assert.valueRequireNonEmpty(classPath, "ClassPath");
        Assert.valueRequireNonEmpty(name, "Name");

        try (final InputStream stream = classPath.getResourceAsStream(name)) {
            return load(stream);
        } catch (final IOException exception) {
            throw new ApplicationException(exception);
        }

    }

    public static String load(@NotNull final URL url) {
        Assert.valueRequireNonEmpty(url, "Url");

        try (final InputStream stream = url.openStream()) {
            return load(stream);
        } catch (final IOException exception) {
            throw new ApplicationException(exception);
        }
    }

    public static String load(@NotNull final String absolutePath) {
        Assert.valueRequireNonEmpty(absolutePath, "AbsolutePath");

        final URL url = Resources.class.getClassLoader().getResource(absolutePath);
        Assert.requireNonEmpty(url, "Resource with name [%s] does not exists", absolutePath);

        //noinspection ConstantConditions
        return load(url);
    }

}
