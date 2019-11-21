package com.tuneit.gitgen.gen;

import com.tuneit.gitgen.TaskService;
import com.tuneit.gitgen.data.Variant;
import com.tuneit.gitgen.gen.day.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskServiceDefault implements TaskService {
    @Override
    public Boolean checkTask(Variant variant) {
        int correctDay = getDay(variant);
        switch (correctDay) {
            case 1:
                new Monday().fix(variant, false);
            case 2:
                new Tuesday().fix(variant, false);
            case 3:
                new Wednesday().fix(variant, false);
            case 4:
                new Thursday().fix(variant, false);
            case 5:
                new Friday().fix(variant, false);
        }
        return correctDay > variant.getDay();
    }


    @Override
    public String getTaskText(Variant variant) {
        switch (getDay(variant)) {
            case 1:
                return "Задание первое: исправь ошибки в третьем абзаце. (Некто перепутал строки)";
            case 2:
                return "Задание второе: исправь ошибки в первом абзаце. (Кот проходил около программиста и решил ему помочь...)";
            case 3:
                return "Задание третье: соедините первый и третий абзац в ветку dev. " +
                        "Абзацы должны разделяться пустой строкой, а первый абзац стихотворения уже добавили в ветку dev.";
            case 4:
                return "Задание четвертое: исправь ошибки во втором абзаце. (У программиста залипает 'Back Space', и ему не хватило нервов дописать второй абзац)";
            case 5:
                return "Задание пятое: присоедините второй абзац к ветке dev. Абзацы должны разделяться пустой строкой.";
            case 6:
                return "Готово!";
            default:
                log.error("getDay return incorrect day");
                throw new IllegalStateException("Incorrect day");
        }
    }

    @Override
    public int getDay(Variant variant) {
        int day = new Monday().check(variant);
        if (day == 0) {
            init(variant);
            day++;
        }
        return day;
    }

    private void init(Variant variant) {
        new Monday().fix(variant, false);
    }
}
