package com.github.ssullivan;

import com.amazonaws.services.transcribe.AmazonTranscribeClient;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobRequest;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobResult;
import io.dropwizard.lifecycle.Managed;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranscriptionService implements Managed {
    private ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ITranscriptionJobQueue queue;
    private AmazonTranscribeClient transcribeClient;


    public TranscriptionService(final ITranscriptionJobQueue queue, final AmazonTranscribeClient client) {
        this.queue = queue;
        // probably could jsut be their async client
        this.transcribeClient = client;

        int availbleProcessors = Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < availbleProcessors; ++i) {
            // could use guice factory here..
            executors.submit(new TranscriptionJobWorker(this.queue, transcribeClient));
        }
    }

    public CompletableFuture<TranscriptionJobState> transcribe() {
        StartTranscriptionJobRequest request = new StartTranscriptionJobRequest();

        StartTranscriptionJobResult result =  transcribeClient.startTranscriptionJob(request);
        TranscriptionJobState transcriptionJobState = new TranscriptionJobState(result.getTranscriptionJob());

        this.queue.offer(transcriptionJobState);

        return CompletableFuture.completedFuture(transcriptionJobState);
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        this.executors.shutdown();
    }
}
