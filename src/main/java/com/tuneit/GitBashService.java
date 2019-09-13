package com.tuneit;

import com.tuneit.data.Variant;

public interface GitBashService {
    String executeCommand(String command, Variant variant);

    String poem(String poem, Variant variant);

    String getTask(Variant variant);
}
