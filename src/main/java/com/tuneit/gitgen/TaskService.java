package com.tuneit.gitgen;

import com.tuneit.gitgen.data.Variant;

public interface TaskService {

    Boolean checkTask(Variant variant);

    String getTaskText(Variant variant);

    int getDay(Variant variant);
}
