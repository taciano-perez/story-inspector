package com.o3.storyinspector.gui.utils;

import javafx.scene.Node;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

public class IconUtils {

    public static Node getIcon(FontAwesome.Glyph glyph) {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        final Node result = fontAwesome.create(glyph.getChar());
        return result;
    }
}
