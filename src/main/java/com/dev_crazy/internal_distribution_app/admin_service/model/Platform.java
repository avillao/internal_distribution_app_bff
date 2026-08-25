package com.dev_crazy.internal_distribution_app.admin_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum Platform {
    @JsonProperty("android")
    ANDROID("android"),

    @JsonProperty("ios")
    IOS("ios");

    private final String platform;

    private Platform(String platform){
        this.platform = platform;
    }

    public String getPlatform(){
        return this.platform;
    }

    @Override
    public String toString() {
        return this.platform;
    }

    public static Platform valueOfString(String platform){
        Enum<Platform>[] enumConstants = Platform.class.getEnumConstants();
        Platform platformEnum = (Platform) Arrays.stream(enumConstants).filter(
                e -> ((Platform) e).getPlatform().equals(platform)).findAny().get();
        return platformEnum;
    }
}
