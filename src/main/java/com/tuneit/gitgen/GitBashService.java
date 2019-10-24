package com.tuneit.gitgen;

import com.tuneit.gitgen.data.CommandResult;
import com.tuneit.gitgen.data.Variant;

public interface GitBashService {
    CommandResult executeCommand(String command, Variant variant);

    String updatePoem(String poem, Variant variant);

    String getTask(Variant variant);

    Integer getDay(Variant variant);
}
