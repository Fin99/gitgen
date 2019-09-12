package com.tuneit;

import com.tuneit.bash.CommandResult;
import com.tuneit.bash.GitBashServiceDefault;
import com.tuneit.gen.Variant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        String username = "alexandr";
        int variantTask = 1;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String buf = "";
        String task = "";
        String commandResult = "";
        while (true) {
            GitBashService bashService = new GitBashServiceDefault();
            Variant variant = new Variant(null, username, variantTask);
            int day = bashService.getDay(variant);
            variant.setDay(day);
            System.out.println(day);
            if (day == 0) {
                task = bashService.init(variant).getTaskText();
                commandResult = "empty";
            } else {
                switch (reader.readLine()) {
                    case "c":
                        CommandResult command = bashService.executeCommand(reader.readLine(), variant);
                        commandResult = command.getCommandResult();
                        task = command.getTask() == null ? task : command.getTask();
                        break;
                    case "p":
                        buf += reader.readLine();
                        break;
                    case "e":
                        commandResult = bashService.poem(buf, variant);
                        buf = "";
                        break;
                }
            }
            System.out.println(task);
            System.out.println("----------");
            System.out.println(commandResult);
            System.out.println();
        }
    }
}
