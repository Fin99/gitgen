package com.tuneit.gen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Task {
    @NonNull
    private Boolean result;
    private String taskText;
}
