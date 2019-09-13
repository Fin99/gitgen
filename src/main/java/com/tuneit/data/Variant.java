package com.tuneit.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Random;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Variant {
    private Integer day; // from 1 to 7
    @NonNull
    private String username;
    @NonNull
    private Integer variant;

    public Random getRandom() {
        return new Random(hashCode());
    }

    public String getStudDirName() {
        return username + variant; // TODO escape character
    }

    public String getOriginDirName() {
        return username + variant + "origin"; // TODO escape character
    }

    public Variant nextDay() {
        return new Variant(day + 1, username, variant);
    }
}
