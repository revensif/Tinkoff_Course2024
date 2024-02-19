package edu.java.bot.utils;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static edu.java.bot.utils.LinkUtils.isCorrectLink;
import static org.assertj.core.api.Assertions.assertThat;

public class LinkUtilsTest {

    @ParameterizedTest
    @DisplayName("Link validation test : Correct Links")
    @CsvSource({
        "https://github.com/revensif/Tinkoff_Course2024",
        "https://stackoverflow.com/"
    })
    public void shouldReturnTrueToLinks(URI url) {
        assertThat(isCorrectLink(url)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("Link validation test : Incorrect Links")
    @CsvSource({
        "https://mail.ru/",
        "https://stackoverflo.com/"
    })
    public void shouldReturnFalseToLinks(URI url) {
        assertThat(isCorrectLink(url)).isFalse();
    }
}
