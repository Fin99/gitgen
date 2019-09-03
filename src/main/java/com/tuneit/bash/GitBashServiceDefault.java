package com.tuneit.bash;

import com.tuneit.GitBashService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class GitBashServiceDefault implements GitBashService {
    @Override
    public String executeCommand(String line) {
        try {
            if (line == null) {
                return "Empty line";
            }
            line = line.trim();
            if (!line.contains(" ")) {
                return "Empty line";
            }

            if (line.equals("cat poem")) {
                return command("cat poem");
            }
            if (!line.substring(0, line.indexOf(" ")).equals("git")) {
                return "Command not found";
            }

            String command = line.substring(line.indexOf(" ") + 1);

            if (Pattern.matches("checkout( -b)? (master|dev|quatrain[123])", command)) {
                return command(line);
            } else if (Pattern.matches("merge (master|dev|quatrain[123])", command)) {
                return command(line);
            } else if (Pattern.matches("commit -m \"[A-z0-9 ]*?\"", command)) {
                return command(line);
            } else if (Pattern.matches("add (poem|\\.)", command)) {
                return command(line);
            } else {
                return easyCommand(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String easyCommand(String command) throws IOException {
        switch (command) {
            case "status":
                return command("git status");
            case "log":
                return command("git --no-pager log");
            case "log --graph":
                return command("git --no-pager log --graph");
            case "diff":
                return command("git --no-pager diff");
            case "reset --hard HEAD":
                return command("git reset --hard HEAD");
            case "branch":
                return command("git branch");
            default:
                return "Command not found";
        }
    }

    private String command(String command) throws IOException {
        Process status = Runtime.getRuntime().exec(command, new String[]{}, new File("test1"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(status.getInputStream()));
        StringBuilder result = new StringBuilder();
        String resultLine = reader.readLine();
        if (resultLine != null) {
            while (resultLine != null) {
                result.append(resultLine);
                resultLine = reader.readLine();
            }
        }

        reader = new BufferedReader(new InputStreamReader(status.getErrorStream()));
        resultLine = reader.readLine();
        if (resultLine != null) {
            while (resultLine != null) {
                result.append(resultLine);
                resultLine = reader.readLine();
            }
        }

        return result.toString();
    }
}
