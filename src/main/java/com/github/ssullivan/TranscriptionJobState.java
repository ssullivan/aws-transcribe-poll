package com.github.ssullivan;


import com.amazonaws.services.transcribe.model.TranscriptionJob;

import java.util.concurrent.CompletableFuture;

public class TranscriptionJobState {
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String FAILED = "FAILED";
    private static final String COMPLETED = "COMPLETED";


    private final Object transcriptionJobGuard = new Object();
    private TranscriptionJob transcriptionJob;
    private CompletableFuture<TranscriptionJob> jobFinishedFuture;

    public TranscriptionJobState(final TranscriptionJob transcriptionJob) {
        this.transcriptionJob = transcriptionJob;
        this.jobFinishedFuture = new CompletableFuture<>();
    }

    public TranscriptionJob getTranscriptionJob() {
        synchronized (transcriptionJobGuard) {
            return this.transcriptionJob;
        }
    }

    public TranscriptionJob setTranscriptionJob(final TranscriptionJob transcriptionJob) {
        synchronized (transcriptionJobGuard) {
            this.transcriptionJob = transcriptionJob;
        }
        return this.transcriptionJob;
    }

    public CompletableFuture<TranscriptionJob> getJobFinishedFuture() {
        return jobFinishedFuture;
    }

    public void onComplete(final TranscriptionJob transcriptionJob) {
        jobFinishedFuture.complete(transcriptionJob);
    }

    public void onFailure(final  TranscriptionJob transcriptionJob) {
        jobFinishedFuture.completeExceptionally(new TranscriptionJobFailed(transcriptionJob));
    }
}
