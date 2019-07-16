package com.falana.dtmfdecoder;

import java.util.HashMap;
import java.util.Map;

public class Decoder {

    private double[] spectrum;
    private int length;
    private static final Map<Character, int[]> dualTones= new HashMap<>();
    static{
        dualTones.put('1', new int[]{45, 77});
        dualTones.put('2', new int[]{45, 86});
        dualTones.put('3', new int[]{45, 95});
        dualTones.put('4', new int[]{49, 77});
        dualTones.put('5', new int[]{49, 86});
        dualTones.put('6', new int[]{49, 95});
        dualTones.put('7', new int[]{55, 77});
        dualTones.put('8', new int[]{55, 86});
        dualTones.put('9', new int[]{55, 95});
        dualTones.put('0', new int[]{60, 86});
        dualTones.put('*', new int[]{60, 77});
        dualTones.put('#', new int[]{60, 95});
    }

    Decoder(double[] spectrum){
        this.spectrum = spectrum;
        this.length = spectrum.length;
    }

    public void normalize(){
        double max = 0;

        for(int i=0; i<length; i++)
            if(max < spectrum[i])
                max = spectrum[i];

        for(int i=0; i<length; i++)
            spectrum[i] /= max;
    }

    public double get(int index){
        return spectrum[index];
    }

    public int length(){
        return length;
    }

    int getMaxIndex(int start, int stop){
        int maxIndex = 0;
        double maxValue = 0;

        for(int i = start; i <= stop; ++i)
            if(maxValue < spectrum[i]){
                maxValue = spectrum[i];
                maxIndex = i;
            }
        return maxIndex;
    }

    double getAverage(int start, int stop){
        double sum = 0;

        for(int i = start; i <= stop; ++i)
            sum+= spectrum[i];

        return sum/(stop - start);
    }

    private static boolean isBetween(int value, int min, int max){
        return((value > min) && (value < max));
    }

    public char decodeTone(){
        int lowFreqMax = getMaxIndex(40,75);
        int highFreqMax = getMaxIndex(75,100);

        if(getAverage(0,200)<=0.1) {
            if(getAverage(0, 40)<=0.4 && getAverage(100, 150) <= 0.4 ) {
                for (Map.Entry<Character, int[]> entry : dualTones.entrySet()) {
                    if ( isBetween((lowFreqMax - entry.getValue()[0]), -1, 1) && isBetween((highFreqMax - entry.getValue()[1]), -1, 1)) {
                        if(spectrum[entry.getValue()[0]] > 2* getAverage(0,200) && spectrum[entry.getValue()[1]] > 2* getAverage(0,200) ) {
                            return entry.getKey();
                        }
                    }
                }
            }
            return ' ';
        }
        else
            return ' ';
    }
}