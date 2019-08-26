package com.tuneit.gen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Variant {
    private Integer day; // from 1 to 7
    private String username;
    private Integer variant;

    public Random getRandom() {
        return new Random(hashCode());
    }
}
