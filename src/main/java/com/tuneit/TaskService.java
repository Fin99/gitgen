package com.tuneit;

import com.tuneit.data.Variant;
import com.tuneit.gen.Task;

public interface TaskService {
    Task generateTask(Variant variant);

    Task checkTask(Variant variant);

    String getTaskText(Variant variant);
}
