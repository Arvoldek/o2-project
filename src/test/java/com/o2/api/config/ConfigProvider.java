package com.o2.api.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigProvider {

    private static final String DEFAULT_BASE_URL = "https://jsonplaceholder.typicode.com";

    private ConfigProvider() {
        // utility class
    }

    public static String getBaseUrl() {
        try {
            String environment = System.getProperty("environment", "default");
            Config config = ConfigFactory.load("serenity.conf");

            String envPath = "environments." + environment + ".base.url";
            if (config.hasPath(envPath)) {
                return config.getString(envPath);
            }
            if (config.hasPath("environments.default.base.url")) {
                return config.getString("environments.default.base.url");
            }
        } catch (Exception e) {
            // fall back to default if serenity.conf cannot be loaded
        }
        return DEFAULT_BASE_URL;
    }
}