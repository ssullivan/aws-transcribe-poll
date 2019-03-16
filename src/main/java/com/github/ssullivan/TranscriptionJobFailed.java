package com.github.ssullivan;

import com.amazonaws.services.transcribe.model.TranscriptionJob;

public class TranscriptionJobFailed extends RuntimeException {
    private final TranscriptionJob transcriptionJob;

    public TranscriptionJobFailed(TranscriptionJob transcriptionJob) {
        super("The transcription job failed");
        this.transcriptionJob = transcriptionJob;
    }

    public TranscriptionJob getTranscriptionJob() {
        return transcriptionJob;
    }
}
