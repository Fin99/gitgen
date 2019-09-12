package com.tuneit;

import com.tuneit.bash.CommandResult;
import com.tuneit.gen.Variant;

public interface TaskService {
    CommandResult generateTask(Variant variant);

    Boolean checkTask(Variant variant);
}
