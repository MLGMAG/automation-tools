package com.mlgmag.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseContext {
    private boolean isSuccessful;
    private String message;
}
