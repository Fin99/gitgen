package com.tuneit.gitgen;

import com.tuneit.gitgen.data.Variant;

public interface TaskService {
    void generateTask(Variant variant);

    Boolean checkTask(Variant variant);

    String getTaskText(Variant variant);
}
