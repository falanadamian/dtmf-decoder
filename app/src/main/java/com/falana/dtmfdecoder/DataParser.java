package com.falana.dtmfdecoder;

import android.os.AsyncTask;
import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class DataParser extends AsyncTask<Void, Object, Void>{

    private MainActivity mainActivity;
    private BlockingQueue<ProcessedData> recordedData;
    private static ArrayList<Character> decodedList;
    private int round;
    private boolean hasWaited;
    private long decodedTime;
    private long lastDecodedTime;
    private long nullTime;
    private boolean firstTime;


    DataParser(MainActivity mainActivity, BlockingQueue<ProcessedData> blockingQueue){
        this.mainActivity = mainActivity;
        this.recordedData = blockingQueue;
        decodedList = new ArrayList<>();
        round=0;
        hasWaited=false;
        firstTime= true;
    }

    @Override
    protected Void doInBackground(Void... params){

        int minBufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, minBufferSize);

        float[] buffer = new float[1024];
        audioRecord.startRecording();

        try {
            while (mainActivity.isRunning()) {

                int bufferSize = audioRecord.read(buffer, 0, 1024, AudioRecord.READ_NON_BLOCKING);
                ProcessedData dataToProcess = new ProcessedData(buffer, bufferSize);

                recordedData.put(dataToProcess);

                ProcessedData recorded = recordedData.take();

                Decoder decoder = recorded.FFT();

                decoder.normalize();

                Character decodedTone = decodeTone(decoder.decodeTone());

                publishProgress(decodedTone, decoder);
            }
        }catch (InterruptedException ignored) {
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Object... progress){
        Character decodedTone = (Character) progress[0];
        mainActivity.setTone(decodedTone);
        Decoder decoder = (Decoder) progress[1];
        mainActivity.drawGraph(decoder);
    }

    private char decodeTone(char decodedTone) {

        if (decodedTone != ' ') {

            if (round == 0) {
                decodedList.add(decodedTone);
                lastDecodedTime= System.currentTimeMillis();
                round=1;
            } else {
                decodedList.add(decodedTone);

                if (decodedList.get(1) == decodedList.get(0)) {
                    decodedTime = System.currentTimeMillis();
                }

                decodedList.remove(0);

                char decoded;

                if ( (decodedTime - lastDecodedTime > 1000 || firstTime) || hasWaited) {
                    decoded = decodedList.get(0);
                    lastDecodedTime = System.currentTimeMillis();
                    firstTime= false;
                    return decoded;
                }
                return ' ';
            }
        }

        nullTime = System.currentTimeMillis();

        if(nullTime - decodedTime > 100){
            hasWaited=true;
        }else
            hasWaited=false;

        return ' ';
    }
}

class ProcessedData{
    private double[] data;

    ProcessedData(float[] buffer, int bufferSize){
        data = new double[1024];

        for (int i = 0; i < bufferSize; i++){
            data[i] = (double) buffer[i];
        }
    }

    public Decoder FFT(){
        return new Decoder(FFT.magnitude(data));
    }
}