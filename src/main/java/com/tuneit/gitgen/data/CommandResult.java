package com.tuneit.gitgen.data;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommandResult {
    @NonNull
    private String commandResult;
    private String poem;
    private boolean changeDay = false;
    private boolean commitCommand = false;

    public CommandResult(@NonNull String commandResult, String poem) {
        this.commandResult = commandResult;
        this.poem = poem;
    }
}
