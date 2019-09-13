package com.tuneit.gen;

import com.tuneit.TaskService;
import com.tuneit.data.Variant;
import com.tuneit.gen.day.*;

public class TaskServiceDefault implements TaskService {

    private Day getDay(Integer day) {
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
    public void generateTask(Variant variant) {
        getDay(variant.getDay()).generateTask(variant);
    }

    @Override
    public Boolean checkTask(Variant variant) {
        return getDay(variant.getDay()).checkTask(variant);
    }

    @Override
    public String getTaskText(Variant variant) {
        return getDay(variant.getDay()).getTaskText();
    }
}
