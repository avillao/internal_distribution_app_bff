package com.dev_crazy.internal_distribution_app.admin_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum Branch {
    @JsonProperty("qa")
    QA("qa"),

    @JsonProperty("prod")
    PROD("prd");

    private final String branch;

    private Branch(String branch){
        this.branch = branch;
    }

    public String getBranch(){
        return this.branch;
    }

    @Override
    public String toString() {
        return this.branch;
    }

    public static Branch valueOfString(String branch){
        Enum<Branch>[] enumConstants = Branch.class.getEnumConstants();
        Branch platformEnum = (Branch) Arrays.stream(enumConstants).filter(
                e -> ((Branch) e).getBranch().equals(branch)).findAny().get();
        return platformEnum;
    }
}
