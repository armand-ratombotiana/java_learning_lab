package com.learning.quizzes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import org.junit.jupiter.api.Test;

class JavaBasicsQuizzesTest {

    @Test
    void shouldExposeAllQuizTopics() {
        List<String> topics = JavaBasicsQuizzes.getQuizTopics();

        assertThat(topics)
                .hasSize(18)
                .contains("Type Widening", "Bitwise Operations", "Local Variable Type Inference");
    }

    @Test
    void shouldRunAllQuizzesWithoutThrowing() {
        assertThatCode(JavaBasicsQuizzes::runAllQuizzes)
                .doesNotThrowAnyException();

        assertThat(JavaBasicsQuizzes.runAllQuizzes()).isEqualTo(18);
    }
}
