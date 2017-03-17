package dk.ciid.cone.test;


import dk.ciid.cone.Blob;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import java.util.ArrayList;

public class OSCTestSketch extends PApplet {

    NetAddress myRemoteLocation;
    ArrayList<String> oscMessages = new ArrayList();
    int messageArrayPosition = 0;


    public void settings() {

        size(640, 480);
        myRemoteLocation = new NetAddress("127.0.0.1", 32000);
        oscMessages.add("123;12;80");
        oscMessages.add("34;90;34");
        oscMessages.add("123;66;134");
    }

    public void setup() {

    }

    public void draw() {

        background(255);

        if(mousePressed){
            background(0);
            OscMessage myOscMessage = new OscMessage("/newTrack");
            /* add a value (an integer) to the OscMessage */

            myOscMessage.add(oscMessages.get(messageArrayPosition++));
            /* send the OscMessage to a remote location specified in myNetAddress */
            // oscP5.send(myOscMessage, myBroadcastLocation);
            OscP5.flush(myOscMessage, myRemoteLocation);

            if(messageArrayPosition >= oscMessages.size()){
                messageArrayPosition = 0;
            }
            delay(800);
        }


    }

    public static void main(String[] args) {
        PApplet.main(OSCTestSketch.class.getName());
    }
}