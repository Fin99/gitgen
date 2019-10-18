package com.tuneit.gitgen.bash;

import com.tuneit.gitgen.GitBashService;
import com.tuneit.gitgen.TaskService;
import com.tuneit.gitgen.data.Variant;
import com.tuneit.gitgen.gen.TaskServiceDefault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.regex.Pattern;

@Slf4j
@Component
public class GitBashServiceDefault implements GitBashService {
    private TaskService taskService = new TaskServiceDefault();

    @Override
    public String poem(String poem, Variant variant) {
        log.info("poem ...");
        try (BufferedWriter bufferedReader = new BufferedWriter(new FileWriter(new File(variant.getStudDirName() + "/poem")))) {
            bufferedReader.write(poem);
            return "Файл успешно изменён";
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при попытке открытия файла";
        }
    }

    @Override
    public String getTask(Variant variant) {
        updateDay(variant);
        if (variant.getDay() == 0 && !new File(variant.getStudDirName()).exists()) {
            variant.setDay(1);
            taskService.generateTask(variant);
        }
        return taskService.getTaskText(variant);
    }

    @Override
    public String executeCommand(String line, Variant variant) {
        updateDay(variant);
        if (variant.getDay() == 0 && !new File(variant.getStudDirName()).exists()) {
            variant.setDay(1);
            taskService.generateTask(variant);
        }
        try {
            if (line == null) {
                return "Empty line";
            }
            line = line.trim();
            if (line.equals("")) {
                return "Empty line";
            }

            if (line.equals("cat poem")) {
                return command("cat poem", variant);
            }
            if (!line.substring(0, line.indexOf(" ")).equals("git")) {
                return "Command not found";
            }

            String command = line.substring(line.indexOf(" ") + 1);

            if (Pattern.matches("checkout (master|dev|quatrain[123])", command)) {
                return command(line, variant);
            } else if (Pattern.matches("merge (master|dev|quatrain[123])", command)) {
                return command(line, variant);
            } else if (Pattern.matches("commit -m \"[A-z0-9 ]*?\"", command)) {
                String resultCommand = command(line, variant);
                Boolean checkerResult = taskService.checkTask(variant);
                if (variant.getDay() != 5 && checkerResult) {
                    taskService.generateTask(variant.nextDay());
                    return resultCommand;
                } else {
                    return resultCommand;
                }
            } else if (Pattern.matches("add (poem|\\.)", command)) {
                return command(line, variant);
            } else {
                return easyCommand(command, variant);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при обработке комманды";
        }
    }

    private void updateDay(Variant variant) {
        if (!new File(variant.getStudDirName()).exists()) {
            variant.setDay(0);
        } else if (taskService.checkTask(new Variant(4, variant.getUsername(), variant.getVariant()))) {
            variant.setDay(5);
        } else if (taskService.checkTask(new Variant(3, variant.getUsername(), variant.getVariant()))) {
            variant.setDay(4);
        } else if (taskService.checkTask(new Variant(2, variant.getUsername(), variant.getVariant()))) {
            variant.setDay(3);
        } else if (taskService.checkTask(new Variant(1, variant.getUsername(), variant.getVariant()))) {
            variant.setDay(2);
        } else {
            variant.setDay(1);
        }
    }

    private String easyCommand(String command, Variant variant) throws IOException {
        switch (command) {
            case "status":
                return command("git status", variant);
            case "log":
                return command("git --no-pager log", variant);
            case "log --graph":
                return command("git --no-pager log --graph", variant);
            case "diff":
                return command("git --no-pager diff", variant);
            case "reset --hard HEAD":
                return command("git reset --hard HEAD", variant);
            case "branch":
                return command("git branch", variant);
            default:
                return "Command not found";
        }
    }

    private String command(String command, Variant variant) throws IOException {
        log.info("Username: " + variant.getUsername() + ". Day: " + variant.getDay() + ". Command: " + command);
        Process status = new ProcessBuilder().command("bash", "-c", command).directory(new File(variant.getStudDirName())).start();


        String result = readCommandResult(status.getInputStream());
        if (result != null) {
            return result;
        }

        result = readCommandResult(status.getErrorStream());
        if (result != null) {
            return result;
        }

        return "";
    }

    private String readCommandResult(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String resultLine = reader.readLine();
            if (resultLine != null) {
                StringBuilder result = new StringBuilder();
                result.append(resultLine);
                resultLine = reader.readLine();
                while (resultLine != null) {
                    result.append("\n");
                    result.append(resultLine);
                    resultLine = reader.readLine();
                }
                return result.toString();
            } else {
                return null;
            }
        }
    }
}
