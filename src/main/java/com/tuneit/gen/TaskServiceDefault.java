package com.tuneit.gen;

import com.tuneit.TaskService;
import com.tuneit.gen.day.*;

public class TaskServiceDefault implements TaskService {

    private TaskChecker getChecker(Integer day) {
        switch (day) {
            case 1:
                return new Monday();
            case 2:
                return new Tuesday();
            case 3:
                return new Wednesday();
            case 4:
                return new Thursday();
            case 5:
                return new Friday();
            default:
                throw new IllegalStateException("Unexpected value: " + day);
        }
    }

    private TaskGen getGenerator(Integer day) {
        switch (day) {
            case 1:
                return new Monday();
            case 2:
                return new Tuesday();
            case 3:
                return new Wednesday();
            case 4:
                return new Thursday();
            case 5:
                return new Friday();
            default:
                throw new IllegalStateException("Unexpected value: " + day);
        }
    }

    @Override
    public Task generateTask(Variant variant) {
        return getGenerator(variant.getDay()).generateTask(variant);
    }

    @Override
    public Task checkTask(Variant variant) {
        return getChecker(variant.getDay()).checkTask(variant);
    }
}
