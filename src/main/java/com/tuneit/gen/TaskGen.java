package com.tuneit.gen;

import com.tuneit.bash.CommandResult;

public interface TaskGen {
    CommandResult generateTask(Variant variant);
}
