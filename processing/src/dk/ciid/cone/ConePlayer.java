package dk.ciid.cone;

import controlP5.*;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.*;
import processing.serial.Serial;
import processing.sound.*;
import java.io.File;

public class ConePlayer extends PApplet {

    private ControlP5 cp5;
    private static int knobRangeMin = 0;
    private static int knobRangeMax = 255;
    private Knob myKnob1;
    private Knob myKnob2;
    private Knob myKnob3;
    private int lastTimePlayed = 0;
    private String[] fileNames;
    private SoundFile currentPlaying;
    private int currentKnobValue;
    Serial serialPort;
    private static int LF = 10;



    public void settings() {
        size(700,400);
    }

    public void setup() {
        smooth();
        noStroke();

        cp5 = new ControlP5(this);

        myKnob1 = cp5.addKnob("knob1")
                .setRange(knobRangeMin, knobRangeMax)
                .setValue(50)
                .setPosition(100, 70)
                .setRadius(50)
                .setDragDirection(Knob.VERTICAL)
        ;

        String path = "/Users/vytas/workspace/the_cone/music/";
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
                    print(stringValue);

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

    private void knob1(int theValue) {
        println("a knob event. Value: "+theValue);
        playFileFromKnobValue(theValue, -1);
    }

    private void playFileFromKnobValue(int knobValue, int cue) { //-1 cue is random
        if (fileNames == null) {
            return;
        }
        if (millis() - lastTimePlayed < 200) {
            return;
        }
        currentKnobValue = knobValue;
        lastTimePlayed = millis();
        int index = (int)map(knobValue, knobRangeMin, knobRangeMax, 0, fileNames.length - 1);
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

    public static void main(String[] args) {
        PApplet.main(ConePlayer.class.getName());
    }
}
