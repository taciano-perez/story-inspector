package com.o3.storyinspector.gui.core;

import java.util.ArrayList;
import java.util.List;

public class ReportManager {

    static List<ReportEventListener> listeners = new ArrayList<>();

    public static void registerEventListener(final ReportEventListener listener) {
        listeners.add(listener);
    }

    public static void fireReportEvent(final ReportEvent event) {
        listeners.stream().forEach(l -> l.handleReportEvent(event));
    }
}
