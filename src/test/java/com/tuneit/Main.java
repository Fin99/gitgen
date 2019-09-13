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
        Variant variant = new Variant(null, username, variantTask);
        while (true) {
            GitBashService bashService = new GitBashServiceDefault();
            task = bashService.getTask(variant);
            switch (reader.readLine()) {
                case "c":
                    CommandResult command = bashService.executeCommand(reader.readLine(), variant);
                    commandResult = command.getCommandResult();
                    task = command.getTask() == null ? task : task + "\n" + command.getTask();
                    break;
                case "p":
                    String line = reader.readLine();
                    while (!line.equals("\ne")) {
                        buf += line;
                        line = "\n" + reader.readLine();
                    }
                    commandResult = bashService.poem(buf, variant);
                    buf = "";
                    break;
            }

            System.out.println(task.trim());
            System.out.println("----------");
            System.out.println(commandResult.trim());
            System.out.println();
        }
    }
}
