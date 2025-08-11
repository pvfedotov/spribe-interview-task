package com.spribe.utils;

public class ConfigProvider {
    public static String getBaseUrl() {
        return ConfigReader.get("service.url");
    }

    public static String getSupervisorLogin() {
        return ConfigReader.get("supervisor.login");
    }

    public static String getSupervisorId() {
        return ConfigReader.get("supervisor.id");
    }

    public static String getAdminLogin() {
        return ConfigReader.get("admin.login");
    }

    public static String getAdminId() {
        return ConfigReader.get("admin.login");
    }
}
