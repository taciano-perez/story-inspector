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

    @Test
    void inspectFKGradeLevelWithEllipses() {
        // given
        final String CONTENT = "Harry Potter rolled over inside his blankets without waking up. " +
                "One small hand closed on the letter beside him and he slept on, not knowing he was special, " +
                "not knowing he was famous, not knowing he would be woken in a few hours' time by Mrs. Dursley's scream " +
                "as she opened the front door to put out the milk bottles, nor that he would spend the next few weeks " +
                "being prodded and pinched by his cousin Dudley... " +
                "He couldn't know that at this very moment, people meeting in secret all over the country were " +
                "holding up their glasses and saying in hushed voices: \"To Harry Potter -- the boy who lived!";

        // when
        double fkGradeLevel = FleschKincaidReadabilityInspector.inspectFKGradeLevel(CONTENT);

        // then
        assertEquals(14.892252252252256, fkGradeLevel);
    }

}