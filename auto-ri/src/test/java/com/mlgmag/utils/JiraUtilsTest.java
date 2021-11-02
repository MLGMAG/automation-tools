package com.mlgmag.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class JiraUtilsTest {

    @ParameterizedTest
    @MethodSource("shouldRemoveExtraNewLinesData")
    void shouldRemoveExtraNewLines(String input, String expected) {
        String actual = JiraUtils.removeExtraNewLines(input);

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> shouldRemoveExtraNewLinesData() {
        return Stream.of(
                Arguments.of("\n\n\n", "\n\n"),
                Arguments.of("\n\n", "\n\n"),
                Arguments.of("\n", "\n"),
                Arguments.of("\n\n\n\n\nMessage\n\n\nAnother Message\ntext\n\n", "\n\nMessage\n\nAnother Message\ntext\n\n")
        );
    }
}