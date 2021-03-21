package com.o3.storyinspector.annotation;

public interface BookProcessingStatusListener {

    void updateProcessingStatus(final double percentageCompleted, final int minutesLeft);

}
