package com.tuneit.bash;

import com.tuneit.GitBashService;
import com.tuneit.TaskService;
import com.tuneit.gen.TaskServiceDefault;
import com.tuneit.gen.Variant;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.regex.Pattern;

@Slf4j
public class GitBashServiceDefault implements GitBashService {
    private TaskService taskService = new TaskServiceDefault();

    @Override
    public String poem(String poem, Variant variant) {
        log.debug("poem ...");
        try (BufferedWriter bufferedReader = new BufferedWriter(new FileWriter(new File(variant.getStudDirName() + "/poem")))) {
            bufferedReader.write(poem);
            return "Файл успешно изменён";
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при попытке открытия файла";
        }
    }

    @Override
    public String executeCommand(String line, Variant variant) {
        if (variant.getDay() == 1 && !new File(variant.getStudDirName()).exists()) {
            taskService.generateTask(variant);
        }
        try {
            if (line == null) {
                return "Empty line";
            }
            line = line.trim();
            if (!line.contains(" ")) {
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
                String resultCommit = command(line, variant);
                Boolean checkerResult = taskService.checkTask(variant);
                return resultCommit + (checkerResult ? "\nУспешно" : "Ошибка в ответе");
            } else if (Pattern.matches("add (poem|\\.)", command)) {
                return command(line, variant);
            } else {
                return easyCommand(command, variant);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
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
        log.debug(command);
        Process status = new ProcessBuilder().command("bash", "-c", command).directory(new File(variant.getStudDirName())).start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(status.getInputStream()));
        StringBuilder result = new StringBuilder();
        String resultLine = reader.readLine();
        if (resultLine != null) {
            while (resultLine != null) {
                result.append(resultLine).append("\n");
                resultLine = reader.readLine();
            }
        }

        reader = new BufferedReader(new InputStreamReader(status.getErrorStream()));
        resultLine = reader.readLine();
        if (resultLine != null) {
            while (resultLine != null) {
                result.append(resultLine);
                resultLine = reader.readLine();
                if (resultLine != null) {
                    result.append("\n");
                }
            }
        }

        return result.toString();
    }
}
