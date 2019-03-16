package com.github.ssullivan;

import com.amazonaws.services.transcribe.AmazonTranscribeClient;
import com.amazonaws.services.transcribe.model.GetTranscriptionJobRequest;
import com.amazonaws.services.transcribe.model.GetTranscriptionJobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TranscriptionJobWorker implements Runnable {
    private static final Logger Log = LoggerFactory.getLogger(TranscriptionJobWorker.class);

    private ITranscriptionJobQueue queue;
    private AmazonTranscribeClient transcribeClient;
    private volatile boolean keepWorking = true;


    public TranscriptionJobWorker(final ITranscriptionJobQueue queue, final AmazonTranscribeClient transcribeClient) {
        this.queue = queue;
        this.transcribeClient = transcribeClient;
    }

    @Override
    public void run() {
        while(keepWorking) {
            try {
                Optional<TranscriptionJobState> job = queue.poll(1, TimeUnit.MINUTES);
                job.ifPresent(transcriptionJobState -> {
                    final GetTranscriptionJobRequest request = new GetTranscriptionJobRequest()
                            .withTranscriptionJobName(transcriptionJobState.getTranscriptionJob().getTranscriptionJobName());

                    final GetTranscriptionJobResult result= transcribeClient.getTranscriptionJob(request);
                    switch (result.getTranscriptionJob().getTranscriptionJobStatus()) {
                        case "FAILED":
                            transcriptionJobState.setTranscriptionJob(result.getTranscriptionJob());
                            transcriptionJobState.onFailure(result.getTranscriptionJob());
                            break;
                        case "COMPLETED":
                            transcriptionJobState.setTranscriptionJob(result.getTranscriptionJob());
                            transcriptionJobState.onComplete(result.getTranscriptionJob());
                            break;
                        case "IN_PROGRESS":
                        default:
                            transcriptionJobState.setTranscriptionJob(result.getTranscriptionJob());
                            this.queue.offer(transcriptionJobState);
                            break;
                    }
                });
            } catch (InterruptedException e) {
                Log.error("Interrupted while waiting for transcription job", e);
                keepWorking = false;
                Thread.currentThread().interrupt();
            }
        }
    }
}
