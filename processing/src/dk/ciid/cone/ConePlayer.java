package dk.ciid.cone;

import controlP5.*;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.*;
import processing.serial.Serial;
import processing.sound.*;
import java.io.File;
import java.util.ArrayList;

public class ConePlayer extends PApplet {

    private static int knobRangeMin = 0;
    private static int knobRangeMax = 255;
    private int lastTimePlayed = 0;
    private String[] fileNames;
    private SoundFile currentPlaying;
    private int currentPlayingIndex = 0;
    private Serial serialPort;
    private static int LF = 10;
    OscP5 oscP5;
    public static int SERVER_DEFAULT_SERVER_LISTENING_PORT = 32000;






    public void settings() {
        size(700,400);
        oscP5 = new OscP5(this,SERVER_DEFAULT_SERVER_LISTENING_PORT);

    }

    public void setup() {
        smooth();
        noStroke();

        //String path = "/Users/kelvyn/Projects/2017_03 Physical Computing Connected/Production/Code/processing/src/data/music/"; // <--- CHANGE THIS.
        String path = "/Users/vytas/workspace/the_cone/processing/src/data/music/";
        fileNames = listFileNames(path);
        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = path + fileNames[i];
        }

        String portName = Serial.list()[3];
        print(Serial.list());
        serialPort = new Serial(this, portName, 9600);
    }

    public void draw() {
        if ( serialPort.available() > 0) {  // If data is available,
            try {
                String stringValue = serialPort.readStringUntil(LF);
                if (stringValue != null) {
                    String[] values = stringValue.split(";");
                    int[] intValues = new int[3];
                    intValues[0] = Integer.parseInt(values[0]);
                    intValues[1] = Integer.parseInt(values[1]);
                    intValues[2] = Integer.parseInt(values[2].replaceAll("[\\D]", ""));

                    playFileFromValueArray(intValues, -1);
                }

            } catch (Exception ex) {
                println("error");
                System.out.println(ex);

            }
        }

    }

    private String[] listFileNames(String dir) {
        File file = new File(dir);
        if (file.isDirectory()) {
            String names[] = file.list();
            return names;
        } else {
            // If it's not a directory
            return null;
        }
    }

    private void playFileFromValueArray(int[] valueArray, int cue) { //-1 cue is random
        if (fileNames == null) {
            return;
        }
        if (millis() - lastTimePlayed < 200) {
            return;
        }
        lastTimePlayed = millis();

//        int value1 = (int)map(valueArray[0], 0, 360, 0,30);
//        int value2 = (int)map(valueArray[1], 0, 360, 0,30);
//        int value3 = (int)map(valueArray[2], 0, 360, 0,30);
        int value1 = (int)map(valueArray[0], 0, 180, 0,30);
        int value2 = (int)map(valueArray[1], 0, 180, 0,30);
        int value3 = (int)map(valueArray[2], 0, 180, 0,30);

        int index = (value1 ^ value2) ^ value3;

        if (currentPlayingIndex == index) {
            return;
        }
        currentPlayingIndex = index;

        String fileName = fileNames[index];
        println(fileName);

        if (currentPlaying != null) {
            currentPlaying.stop();
        }
        currentPlaying = new SoundFile(this, fileName);
        int myCue = 0;
        if (cue == -1) {
            myCue = (int)random(currentPlaying.duration());
        } else {
            myCue = cue;
        }
        currentPlaying.cue(myCue);
        currentPlaying.loop();
    }

    public void oscEvent(OscMessage theOscMessage) {
        print("### received an osc message.");
        print(" addrpattern: "+theOscMessage.addrPattern());
        println(" typetag: "+theOscMessage.typetag());
        println(" typetag: "+theOscMessage.arguments()[0]);
        // accelX = (float) theOscMessage.arguments()[0];

        switch (theOscMessage.addrPattern()){
            case "/newTrack":

                String[] strValues = ((String)theOscMessage.arguments()[0]).split(";");
                int[] intValues  = new int[strValues.length];
                for(int i = 0; i < strValues.length; i++){
                    intValues[i] = Integer.parseInt(strValues[i]);
                }
                playFileFromValueArray(intValues, -1);

            break;


        }
    }

    public static void main(String[] args) {
        PApplet.main(ConePlayer.class.getName());
    }
}
