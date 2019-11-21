package com.tuneit.gitgen.gen.day;

import com.tuneit.gitgen.data.Variant;

public abstract class Day {
    public abstract int check(Variant variant);

    public abstract int check(Variant variant, Integer day);

    public abstract void fix(Variant variant, boolean doTask);
}
