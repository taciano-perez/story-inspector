package com.o3.storyinspector.annotation.readability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FleschKincaidReadabilityInspectorTest {

    @Test
    void inspectFKGradeLevel() {
        // given
        final String CONTENT = "A rich man's wife became sick, and when she felt that her end was drawing near, " +
                        "she called her only daughter to her bedside and said, \"Dear child, remain pious " +
                        "and good, and then our dear God will always protect you, and I will look down on " +
                        "you from heaven and be near you.\" With this she closed her eyes and died. " +
                        "The girl went out to her mother's grave every day and wept, and she remained pious " +
                        "and good. When winter came the snow spread a white cloth over the grave, and when " +
                        "the spring sun had removed it again, the man took himself another wife. This wife " +
                        "brought two daughters into the house with her. They were beautiful, with fair faces, " +
                        "but evil and dark hearts. Times soon grew very bad for the poor stepchild.";

        // when
        double fkGradeLevel = FleschKincaidReadabilityInspector.inspectFKGradeLevel(CONTENT);

        // then
        assertEquals(6.943587069864442, fkGradeLevel);
    }
}