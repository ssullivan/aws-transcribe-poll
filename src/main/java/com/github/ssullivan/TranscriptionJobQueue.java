package com.github.ssullivan;

import com.amazonaws.services.transcribe.model.TranscriptionJob;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TranscriptionJobQueue implements ITranscriptionJobQueue {
    private LinkedBlockingQueue<TranscriptionJobState> queue;

    public TranscriptionJobQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    public TranscriptionJobQueue(int maxCapacity) {
        queue = new LinkedBlockingQueue<>(maxCapacity);
    }

    @Override
    public Optional<TranscriptionJobState> poll(long duration, TimeUnit unit) throws InterruptedException {
        return Optional.ofNullable(this.queue.poll(duration, unit));
    }

    @Override
    public void offer(final TranscriptionJobState transcriptionJob) {
        this.queue.offer(transcriptionJob);
    }
}
