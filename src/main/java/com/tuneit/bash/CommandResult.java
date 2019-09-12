package com.tuneit.bash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class CommandResult {
    @NonNull
    private String commandResult;
    private String task;

}
