package com.tuneit;

import com.tuneit.gen.Task;
import com.tuneit.gen.Variant;

public interface TaskService {
    Task generateTask(Variant variant);

    Task checkTask(Variant variant);

    String getTaskText(Variant variant);
}
