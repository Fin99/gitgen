package com.tuneit;

import com.tuneit.bash.GitBashServiceDefault;
import com.tuneit.data.Variant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Variant variant = new Variant(null, "alexandr", 1);
    private static GitBashService bashService = new GitBashServiceDefault();

    public static void main(String[] args) throws IOException {
        String commandResult;
        while (true) {
            String command = reader.readLine();
            if (command.equals("p")) {
                commandResult = bashService.poem(readPoem(), variant);
            } else {
                commandResult = bashService.executeCommand(command, variant);
            }

            System.out.println("//////////");
            System.out.println(bashService.getTask(variant));
            System.out.println("----------");
            System.out.println(commandResult);
            System.out.println("//////////");
        }
    }

    private static String readPoem() throws IOException {
        StringBuilder buf = new StringBuilder();
        String line = reader.readLine();
        while (!line.equals("\ne")) {
            buf.append(line);
            line = "\n" + reader.readLine();
        }
        return buf.toString();
    }
}
