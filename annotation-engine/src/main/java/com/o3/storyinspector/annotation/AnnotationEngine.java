package com.o3.storyinspector.annotation;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AnnotationEngine {

    public static void main(String[] args) throws IOException {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer
                .tokenize("\n" +
                        "The address at Seestrasse 228 was a mansion, indeed; the sunny landscape wasn't the only reason Küsnacht was called Zürich's 'Goldcoast.'A wooden gate separated the street from the front garden, where a straight path led to the main door, surrounded by an orderly row of dark-green box trees. I opened the fence, and a tall man in gardener's attire emerged from the bushes, with a thick leather apron and matching gloves.\n" +
                        "\"Good morning. I'm inspector Hektor Teufel, from the Cantonal Police. Someone called the station informing a body was found in the property.\"\n" +
                        "\"Teufel--devil, huh? That's an interesting surname. Quite a coincidence, indeed. Perhaps it takes a devil to catch a devil.\"\n" +
                        "\"What do you mean?\"\n" +
                        "\"Well, this hardly seems the work of God, as you will see for yourself.\"\n" +
                        "\"Forgive my abruptness, but this is a serious matter. Could you take me to the owner of the property? I want him to show me the crime scene.\"\n" +
                        "\"Yes, of course. Please follow me.\"\n" +
                        "He led me through a side path around the house. It was a solid, stern building composed of straight angles, exception made to a cylindric tower on the facade where the front door was located. \n" +
                        "We walked past a small dock where a lone sailboat swayed in the breeze and through a back garden. Near the lake there was a box containing sand and miniatures of houses and people. This household has children, I noted.\n" +
                        "On top of a stone slab in the middle of the garden, sat a statue picturing an odd oriental figure with a crown and four arms, dancing inside a circle of flame.\n" +
                        "There, a grim scene awaited.\n" +
                        "At the feet of the statue, laid a burnt body. Its shape seemed to belong to a middle-aged man, but it was hard to be sure; it was a confused mass of gray flesh, with only the silhouette indicating it once belonged to a person. His features were completely defaced by the fire, making it impossible to tell where the burnt clothing ended and where the charred, crackled skin began. A faint smell of barbecued meat hung in the air, almost making me gag.\n" +
                        "It was a gruesome sight. I had seen worse, still an involuntary whistle escaped me.\n" +
                        "\"What in blazes happened here?\"\n" +
                        "\"I told you it was a devilish scene. After breakfast, I went outside to ... work in the back garden, and found this. We immediately rang the police station, and here you are.\"\n" +
                        "\"This is useful information, but I must insist in speaking to the master of the house. There are questions I must ask him.\"\n" +
                        "\"Yes, understandably,\" the tall man interjected, extending his hand. \"Pleased to make your acquaintance. I am Dr. Carl Gustav Jung, the owner of this property. What else would you like to know?\"\n" +
                        "I felt embarrassed. His attire, strong build, and large callused hands were misleading. On a second glance, it was impossible to miss the quality of his clothes, the perfectly trimmed mustache, the fine golden rim eyeglasses, the costly wedding band.\n" +
                        "Trying to recover my stride, I continued the interrogation.\n" +
                        "\"Do you know who is the deceased?\"\n" +
                        "\"I don't think so. But even if it were someone I knew, it would be impossible to tell, in this disfigured state.\"\n" +
                        "\"Have you noticed anything unusual during the night? Seen something, heard something?\"\n" +
                        "\"Not at all.\"\n" +
                        "\"Who else lives in the house?\"\n" +
                        "\"My wife and our five children.\"\n" +
                        "\"Did any of them notice anything strange? During the night?\"\n" +
                        "\"Franz, my boy, woke in the middle of the night claiming he heard monsters lurking outside. It seems he was right in a certain way, wouldn't you say?\"\n" +
                        "\"Can I talk to them?\"\n" +
                        "He took me inside the house. Above the front door, a stone lintel bore an inscription, 'vocatus atque non vocatus, Deus aderit.'\n" +
                        "\"What does that mean?\" I asked, pointing at the inscription.\n" +
                        "\"That we should keep our door open to the unknown visitor,\" was the enigmatic answer. What else would you expect, Mr. Teufel?\n" +
                        "The front parlor was an elegant room with spare but tasteful furniture. Sunlight filtering through the curtains shone delicately on the fishbone-pattern floor. The house smelled of varnish and wealth.\n" +
                        "Emma Rauschenbach-Jung was a solemn, no-nonsense woman. She answered my questions matching her husband's previous statements, and then excused herself. Their children--three girls and a boy, all between seven and thirteen years old, plus a toddler girl-- followed her.\n" +
                        "When the two of us were alone again, the doctor escorted me to the door. However, I wasn't finished with the questions.\n" +
                        "\"So, you are a doctor. Do you work in a hospital?\"\n" +
                        "\"Although I am by education a medical doctor, my specialty is psychology--a new branch of psychiatry. I have worked many years at the Burghölzli asylum, but currently hold a private practice, seeing my patients on this very house.\"\n" +
                        "\"I see. So you treat crazy people.\"\n" +
                        "He smiled. \"You may say that. But aren't we all a little insane?\"\n" +
                        "\"Would it be possible that this was done by one of your patients? A lunatic, I mean.\"\n" +
                        "He shook his head. \"Currently, I don't treat clinically insane patients. My clients are people like you and I, civil persons who live in society, not maniacs who go around killing or burning.\"\n" +
                        "\"But if they are normal people, why do they see a mind doctor?\"\n" +
                        "\"You would be surprised, inspector, with how much emotional suffering your average 'normal person' holds within his soul.\"\n" +
                        "\"Oh, now I get it! You're one of those divan-and-chair doctors that are fashionable between the rich folk these days.\"\n" +
                        "He shrugged, annoyed. I took a moment to choose carefully the words of my next question.\n" +
                        "\"Dr. Jung, do you have any enemies?\"\n" +
                        "\"Who doesn't? But no--not the kind who would do something like this. I hardly think so.\"\n" +
                        "\"I see. Well, do you have anything to add that could throw more light to this case?\"\n" +
                        "The doctor stopped and turned to look at me. His light brown eyes fixed mine in a focused gaze, as if he was searching inside of me; searching and finding things I did not know were there. I don't know why it felt like that. It was disconcerting.\n" +
                        "\"Look, inspector,\" he said after a moment, \"I know what you're thinking. I have nothing to do with this crime. I was just unlucky it happened on my property--you can be assured.\"\n" +
                        "Then he turned away and disappeared into the house. I stood there, expecting the forensic officers to take the body away.\n");

        InputStream inputStreamNameFinder = AnnotationEngine.class.getClass()
                .getResourceAsStream("/models/en-ner-person.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(
                inputStreamNameFinder);
        NameFinderME nameFinderME = new NameFinderME(model);
        List<Span> spans = Arrays.asList(nameFinderME.find(tokens));

        for (Span s : spans) {
            System.out.println(s.toString() + "  " + tokens[s.getStart()]);
        }
    }
}
