package com.tuneit.gitgen;

import com.tuneit.gitgen.data.Variant;

public interface GitBashService {
    String executeCommand(String command, Variant variant);

    String poem(String poem, Variant variant);

    String getTask(Variant variant);
}
