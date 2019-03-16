package com.github.ssullivan;

import com.amazonaws.services.transcribe.model.TranscriptionJob;
import com.google.inject.ImplementedBy;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ImplementedBy(TranscriptionJobQueue.class)
public interface ITranscriptionJobQueue {

    Optional<TranscriptionJobState> poll(final long duration, final TimeUnit unit) throws InterruptedException;

    void offer(final TranscriptionJobState transcriptionJob);
}
