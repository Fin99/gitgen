package com.tuneit;

import com.tuneit.gen.Variant;

public interface GitBashService {
    String executeCommand(String command, Variant variant);

    String poem(String poem, Variant variant);
}
