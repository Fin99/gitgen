package com.tuneit;

import com.tuneit.gen.Task;
import com.tuneit.gen.Variant;

public interface TaskService {
    Task generateTask(Variant variant);

    Boolean checkTask(Variant variant);
}
