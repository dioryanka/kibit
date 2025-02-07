package com.kibit.paymentapi.model;

import java.util.HashMap;
import java.util.Map;

public enum Status {
    EXECUTING("EXECUTING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String value;

    private static final Map<String, Status> LOOKUP = new HashMap<>();

    static {
        for (Status status : Status.values()) {
            LOOKUP.put(status.value, status);
        }
    }

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Status fromValue(String value) {
        return LOOKUP.get(value);
    }
}

