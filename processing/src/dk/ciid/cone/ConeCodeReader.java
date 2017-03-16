package dk.ciid.cone;

import processing.core.PVector;
import processing.video.*;

import processing.core.PApplet;

import java.util.ArrayList;

public class ConeCodeReader extends PApplet {

    Capture cam;
    public static int READING_RESOLUTION = 20;


    public void settings() {
        size(640, 480);
    }

    public void setup() {
        size(640, 480);

        String[] cameras = Capture.list();

        if (cameras.length == 0) {
            println("There are no cameras available for capture.");
            exit();
        } else {
            println("Available cameras:");
            for (int i = 0; i < cameras.length; i++) {
                println(cameras[i]);
            }

            // The camera can be initialized directly using an
            // element from the array returned by list():
            cam = new Capture(this, cameras[0]);

            cam.start();
        }

    }

    public void draw() {
        background(255);
        ArrayList<PVector> blackDots = new ArrayList();

        if (cam.available() == true) {
            // image(cam, 0, 0);
            cam.read();
            for (int x = 0; x< width/READING_RESOLUTION; x++) {
                for (int y = 0; y< height/READING_RESOLUTION; y++) {
                    int c = cam.get(x*READING_RESOLUTION, y*READING_RESOLUTION);
                    fill(c);
                    if(red(c)< 200 && green(c) < 200 && blue(c) > 180){
                        println("blue dot at: " + x*READING_RESOLUTION + " / " + y*READING_RESOLUTION);
                    }
                    if(red(c)< 200 && green(c) > 180 && blue(c) < 200){
                        println("green dot at: " + x*READING_RESOLUTION + " / " + y*READING_RESOLUTION);
                    }

                    if(red(c)< 40 && green(c) < 40 && blue(c) < 40){
                        println("black dot at: " + x*READING_RESOLUTION + " / " + y*READING_RESOLUTION);
                        blackDots.add(new PVector(x*READING_RESOLUTION, y*READING_RESOLUTION));
                        ellipse(x*READING_RESOLUTION, y*READING_RESOLUTION, READING_RESOLUTION, READING_RESOLUTION);

                    }


                }

            }

        }
        /*
        for(PVector blackDot: blackDots){
            ellipse(blackDot.x, blackDot.x, 8, 8);
        }*/

        ellipse(40, 40, 8, 8);
        ellipse(width-40, 40, 8, 8);
        ellipse(40, height-40, 8, 8);
        ellipse(width-40, height-40, 8, 8);



        delay(20);
        // The following does the same, and is faster when just drawing the image
        // without any additional resizing, transformations, or tint.
        //set(0, 0, cam);


    }

    public static void main(String[] args) {
        PApplet.main(ConeCodeReader.class.getName());
    }
}