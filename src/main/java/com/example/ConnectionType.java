package com.example;

public enum ConnectionType {
    ANTENNA,
    INPUT_FILE;

    public static ConnectionType from(String value) {
        return ConnectionType.valueOf(value.toUpperCase());
    }
}
