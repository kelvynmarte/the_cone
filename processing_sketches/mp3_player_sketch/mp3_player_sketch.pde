import controlP5.*;
import processing.sound.*;

ControlP5 cp5;
static int knobRangeMin = 0;
static int knobRangeMax = 255;
Knob myKnob1;
Knob myKnob2;
Knob myKnob3;
int lastTimePlayed = 0;
String[] fileNames;
SoundFile currentPlaying;
int currentKnobValue;

void setup() {
  size(700,400);
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
               
  String path = "/Users/vytas/workspace/mp3_player_sketch/music/";
  fileNames = listFileNames(path);
  for (int i = 0; i < fileNames.length; i++) {
    fileNames[i] = path + fileNames[i];
  }
}

String[] listFileNames(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    String names[] = file.list();
    return names;
  } else {
    // If it's not a directory
    return null;
  }
}

void draw() {
  
}

void knob1(int theValue) {
  println("a knob event. Value: "+theValue);
  playFileFromKnobValue(theValue, -1);
}

void playFileFromKnobValue(int knobValue, int cue) { //-1 cue is random
  if (fileNames == null) {
    return;
  }
  if (millis() - lastTimePlayed < 200) {
    return;
  }
  currentKnobValue = knobValue;
  lastTimePlayed = millis();
  int index = (int)map(knobValue, knobRangeMin, knobRangeMax, 0, fileNames.length - 1); //<>//
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