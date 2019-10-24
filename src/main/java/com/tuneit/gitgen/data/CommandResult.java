package com.tuneit.gitgen.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommandResult {
    @NonNull
    private String commandResult;
    private String poem;
}
