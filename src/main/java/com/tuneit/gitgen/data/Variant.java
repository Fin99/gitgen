package com.tuneit.gitgen.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Random;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Variant {
    private Integer day; // from 1 to 5
    @NonNull
    private String username;
    @NonNull
    private Integer variant;

    public Random getRandom() {
        return new Random(username.hashCode());
    }

    public String getStudDirName() {
        return System.getProperty("user.home") + "/gitgen/repo/" + username + variant;
    }

    public String getStudPoem() {
        return System.getProperty("user.home") + "/gitgen/repo/" + username + variant + "/poem";
    }

    public String getOriginDirName() {
        return System.getProperty("user.home") + "/gitgen/repo/" + username + variant + "origin";
    }

    public String getOriginPoem() {
        return System.getProperty("user.home") + "/gitgen/repo/" + username + variant + "origin" + "/poem";
    }

    public Variant nextDay() {
        return new Variant(day + 1, username, variant);
    }
}
