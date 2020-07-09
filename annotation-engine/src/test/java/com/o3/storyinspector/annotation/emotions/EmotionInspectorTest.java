package com.o3.storyinspector.annotation.emotions;

import com.o3.storyinspector.storydom.constants.EmotionType;
import org.junit.jupiter.api.Test;

class EmotionInspectorTest {

    @Test
    void inspectAngerScore() {
        // given
        final String sampleText = "I hate you I love you\n" +
                "I hate that I love you\n" +
                "Don't want to, but I can't put\n" +
                "Nobody else above you\n" +
                "I hate you I love you\n" +
                "I hate that I want you\n" +
                "You want her, you need her\n" +
                "And I'll never be her\n" +
                "Feeling used, but I'm\n" +
                "Still missing you and I can't\n" +
                "See the end of this just wanna feel your kiss\n" +
                "Against my lips and now all this time\n" +
                "Is passing by, but I still can't seem to tell you why\n" +
                "It hurts me every time I see you\n" +
                "Realize how much I need you\n" +
                "I hate you I love you\n" +
                "I hate that I love you\n" +
                "Don't want to, but I can't put\n" +
                "Nobody else above you\n" +
                "I hate you I love you\n" +
                "I hate that I want you\n" +
                "You want her, you need her\n" +
                "And I'll never be her\n" +
                "Oh oh, keep it on the low\n" +
                "You're still in love with me but your friends don't know\n" +
                "If u wanted me you would just say so\n" +
                "And if I were you, I would never let me go\n" +
                "I don't mean no harm, I just miss you on my arm\n" +
                "Wedding bells were just alarms caution tape around my heart\n" +
                "I miss you when I can't sleep\n" +
                "Or right after coffee or right when I can't eat\n" +
                "I miss you in my front seat\n" +
                "Still got sand in my sweaters from nights we don't remember\n" +
                "Do you miss me like I miss you?\n" +
                "Fucked around and got attached to you\n" +
                "Friends can break your heart too\n" +
                "And I'm always tired but never of you\n" +
                "When love and trust are gone\n" +
                "I guess this is moving on\n" +
                "Everyone I do right does me wrong\n" +
                "So every lonely night, I sing this song\n" +
                "I hate you I love you\n" +
                "I hate that I love you\n" +
                "Don't want to, but I can't put\n" +
                "Nobody else above you\n" +
                "I hate you I love you\n" +
                "I hate that I want you\n" +
                "You want her, you need her\n" +
                "And I'll never be her\n" +
                "All alone I watch you watch her\n" +
                "Like she's the only girl you've ever seen\n" +
                "You don't care you never did\n" +
                "You don't give a damn about me\n" +
                "Yeah all alone I watch you watch her\n" +
                "She's the only thing you've ever seen\n" +
                "How is it you never notice\n" +
                "That you are slowly killing me\n" +
                "I hate you I love you\n" +
                "I hate that I love you\n" +
                "Don't want to, but I can't put\n" +
                "Nobody else above you\n" +
                "I hate you I love you\n" +
                "I hate that I want you\n" +
                "You want her, you need her\n" +
                "And I'll never be her";

        // when
        final double angerScore = EmotionInspector.inspectEmotionScore(EmotionType.ANGER, sampleText);

        // then
        assert (angerScore > 0.0);
    }
}