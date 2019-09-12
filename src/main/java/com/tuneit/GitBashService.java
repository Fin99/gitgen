package com.tuneit;

import com.tuneit.bash.CommandResult;
import com.tuneit.gen.Task;
import com.tuneit.gen.Variant;

public interface GitBashService {
    CommandResult executeCommand(String command, Variant variant);

    String poem(String poem, Variant variant);

    Integer getDay(Variant variant);

    Task init(Variant variant);
}
