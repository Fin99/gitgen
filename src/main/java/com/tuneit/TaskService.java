package com.tuneit;

import com.tuneit.data.Variant;

public interface TaskService {
    void generateTask(Variant variant);

    Boolean checkTask(Variant variant);

    String getTaskText(Variant variant);
}
