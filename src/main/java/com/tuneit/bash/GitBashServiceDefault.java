package com.tuneit.bash;

import com.tuneit.GitBashService;
import com.tuneit.TaskService;
import com.tuneit.gen.Task;
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
    public Integer getDay(Variant variant) {
        if (!new File(variant.getStudDirName()).exists()) {
            return 0;
        } else if (taskService.checkTask(new Variant(4, variant.getUsername(), variant.getVariant())).getResult()) {
            return 5;
        } else if (taskService.checkTask(new Variant(3, variant.getUsername(), variant.getVariant())).getResult()) {
            return 4;
        } else if (taskService.checkTask(new Variant(2, variant.getUsername(), variant.getVariant())).getResult()) {
            return 3;
        } else if (taskService.checkTask(new Variant(1, variant.getUsername(), variant.getVariant())).getResult()) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public Task init(Variant variant) {
        if (variant.getDay() == 0 && !new File(variant.getStudDirName()).exists()) {
            variant.setDay(1);
            return taskService.generateTask(variant);
        }
        return null;
    }

    @Override
    public CommandResult executeCommand(String line, Variant variant) {
        if (variant.getDay() == 1 && !new File(variant.getStudDirName()).exists()) {
            taskService.generateTask(variant);
        }
        try {
            if (line == null) {
                return new CommandResult("Empty line");
            }
            line = line.trim();
            if (!line.contains(" ")) {
                return new CommandResult("Empty line");
            }

            if (line.equals("cat poem")) {
                return new CommandResult(command("cat poem", variant));
            }
            if (!line.substring(0, line.indexOf(" ")).equals("git")) {
                return new CommandResult("Command not found");
            }

            String command = line.substring(line.indexOf(" ") + 1);

            if (Pattern.matches("checkout (master|dev|quatrain[123])", command)) {
                return new CommandResult(command(line, variant));
            } else if (Pattern.matches("merge (master|dev|quatrain[123])", command)) {
                return new CommandResult(command(line, variant));
            } else if (Pattern.matches("commit -m \"[A-z0-9 ]*?\"", command)) {
                String resultCommand = command(line, variant);
                Task checkerResult = taskService.checkTask(variant);
                if (variant.getDay() != 5 && checkerResult.getResult()) {
                    Task generatorResult = taskService.generateTask(variant.nextDay());
                    return new CommandResult(resultCommand, generatorResult.getTaskText());
                } else {
                    return new CommandResult(resultCommand, checkerResult.getTaskText());
                }
            } else if (Pattern.matches("add (poem|\\.)", command)) {
                return new CommandResult(command(line, variant));
            } else {
                return new CommandResult(easyCommand(command, variant));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new CommandResult("Ошибка при обработке комманды");
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
        log.info(command);
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
