package com.mlgmag.service;

import com.mlgmag.exception.NoArgsException;
import org.springframework.stereotype.Service;

@Service
public class ArgsService {
    private static final String ERROR_MESSAGE = "Please specify release!";

    public void validate(String... args) {
        if (args.length == 0) {
            throw new NoArgsException(ERROR_MESSAGE);
        }
    }

    public String getFixVersion(String... args) {
        return args[0];
    }
}
