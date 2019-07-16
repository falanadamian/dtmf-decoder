package com.falana.dtmfdecoder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class MainActivity extends AppCompatActivity {

    private ToggleButton decodeButton;
    private EditText decodedText;
    private char lastTone;
    private String decodedString;
    private boolean running;
    private DataParser dataParser;
    BlockingQueue<ProcessedData> blockingQueue;
    private ImageView imageView;
    private Canvas canvas;
    private Paint paint;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        running= false;
        lastTone= ' ';
        decodedString=" ";

        decodeButton = findViewById(R.id.decodeButton);
        decodeButton.setText(R.string.toggleOff);
        decodeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onOff();
                    decodeButton.setTextOn("Zatrzymaj");
                } else {
                    onOff();
                    decodeButton.setTextOff("Dekoduj");
                }
            }
        });

        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clearTones();
            }
        });

        decodedText = findViewById(R.id.decodedText);
        decodedText.setFocusable(false);
        decodedText.setEnabled(false);

        Bitmap bitmap = Bitmap.createBitmap(1024, 512, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        imageView = findViewById(R.id.graph);
        imageView.setImageBitmap(bitmap);

        lastTone=' ';
    }

    public void drawGraph(Decoder decoder) {
        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.WHITE);

        for (int i = 0; i < 20; i++){
            int mag = (int) (512 - ((decoder.get(i)- decoder.getAverage(0,200)) * 512));
            canvas.drawLine(i, mag, i, 512, paint);
        }

        for (int i = 20; i < decoder.length(); i++){
            int mag = (int) (512 - (decoder.get(i) * 512));
            canvas.drawLine(i, mag, i, 512, paint);
        }

        if(decoder.decodeTone() != ' ') {
            int indexLow = decoder.getMaxIndex(40, 75);
            int indexHigh = decoder.getMaxIndex(75, 100);
            paint.setColor(Color.RED);

            for (int i = -1; i < 1; i++) {
                int mag = (int) (512 - (decoder.get(indexLow+i) * 512));
                canvas.drawLine(indexLow+i, mag, indexLow+i, 512, paint);
                canvas.drawLine(1024- indexLow+i, mag, 1024-indexLow+i, 512, paint);

                mag = (int) (512 - (decoder.get(indexHigh) * 512));
                canvas.drawLine(indexHigh+i, mag, indexHigh+i, 512, paint);
                canvas.drawLine(1024- indexHigh+i, mag, 1024- indexHigh+i, 512, paint);
            }
        }

        imageView.invalidate();
    }

    public void clearTones(){
        decodedString = "";
        decodedText.setText(decodedString);
    }

    public void addTone(Character decodedTone){
        decodedString += decodedTone;
        decodedText.setText(decodedString);
    }

    public void setTone(char decodedTone){
        if(decodedTone != ' ') {
            if (lastTone != decodedTone)
                addTone(decodedTone);
        }
        lastTone = decodedTone;
    }

    public void onOff(){
        if (!running){
            running = true;
            lastTone = ' ';
            blockingQueue = new LinkedBlockingQueue<>();
            dataParser = new DataParser(this,blockingQueue);
            dataParser.execute();
        } else{
            running = false;
            dataParser.cancel(true);
        }
        decodedText.setEnabled(running);
    }

    public boolean isRunning(){
        return running;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
