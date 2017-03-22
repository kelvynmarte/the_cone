package dk.ciid.cone;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PVector;
import processing.video.*;
import controlP5.*;

import processing.core.PApplet;

import java.util.*;

public class ConeCodeReader extends PApplet {

    Capture cam;
    public static int READING_RESOLUTION = 4;
    public static int MIN_NUMBER_OF_POINTS_PER_BLOB = 8;

    public static int BLOB_QUE_SIZE = 2;
    public static int BLOB_QUE_TOLLERANCE_PER_BLOB = 10;


    public static int SOURDUNDING_PIXEL_RESOLUTION = 4;
    public static int CAMERA_WIDTH = 640;
    public static int CAMERA_HEIGHT = 480;
    public int CAMERA_X_PADDING = 100;
    public int CAMERA_Y_PADDING = 50;
    public static int MAX_DISTANCE_TO_CENTER = 150;
    // public int CAMERA_CENTER_BLOB_DETECTION_DIAMETER = 50; TODO

    ControlP5 cp5;
    ArrayList<Blob> centerBlobs = new ArrayList<>();
    ArrayList<Blob> calibrationBlobs = new ArrayList<>();
    ArrayList<Blob> blobs = new ArrayList<>();

    PriorityQueue<BlobGroup> blobGroupQue;
    BlobGroup lastSentBlobGroup;


    NetAddress myRemoteLocation;

    public void settings() {

        size(640 * 2, 480 * 2);
        blobGroupQue = new PriorityQueue<BlobGroup>(10, new Comparator<BlobGroup>()
        {
            @Override
            public int compare(BlobGroup blobGroupA, BlobGroup blobGroupB)
            {
                return (int)(blobGroupA.getTimestamp() - blobGroupB.getTimestamp());
            }
        });
    }

    public void setup() {

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
            // Capture(parent, requestWidth, requestHeight, cameraName, frameRate)
            cam = new Capture(this, CAMERA_WIDTH, CAMERA_HEIGHT, cameras[0]);

            cam.start();
        }

        cp5 = new ControlP5(this);
        cp5.addColorWheel("colorCenter", 3 * width / 4 - 200, 300, 200).setRGB(color(30, 30, 30));
        cp5.addColorWheel("colorDots", 3 * width / 4 - 200, 580, 200).setRGB(color(128, 20, 20));


        cp5.addSlider("CAMERA_X_PADDING")
                .setPosition(3 * width / 4,50)
                .setRange(0,CAMERA_WIDTH/3);

        cp5.addSlider("CAMERA_Y_PADDING")
                .setPosition(3 * width / 4,100)
                .setRange(0,CAMERA_HEIGHT/3);

        myRemoteLocation = new NetAddress("127.0.0.1", 32000);

    }

    public void draw() {

        // println(cp5.get(ColorWheel.class,"colorCenter").getRGB());

        // Background
        background(255);
        noStroke();
        fill(45);
        rect(width / 2, 0, width / 2, height);

        fill(220, 220,220);
        rect(CAMERA_X_PADDING, CAMERA_Y_PADDING, CAMERA_WIDTH - CAMERA_X_PADDING * 2, CAMERA_HEIGHT - CAMERA_Y_PADDING * 2);

        blobs = new ArrayList<>(); // reset blobs
        centerBlobs = new ArrayList<>(); // reset blobs
        calibrationBlobs = new ArrayList<>();

        ArrayList<PVector> blackDots = new ArrayList();

        if (cam.available() == true) {
            image(cam, 0, height / 2);
            cam.read();


            for (int x = CAMERA_X_PADDING / READING_RESOLUTION; x < (CAMERA_WIDTH - CAMERA_X_PADDING )/ READING_RESOLUTION; x++) {
                for (int y = CAMERA_Y_PADDING / READING_RESOLUTION; y < (CAMERA_HEIGHT - CAMERA_Y_PADDING) / READING_RESOLUTION; y++) {
                    int c = cam.get(x * READING_RESOLUTION, y * READING_RESOLUTION);
                    fill(c);
                    /*
                    if (red(c) > 90 && green(c) > 90 && blue(c) < 180 && red(c) + green(c) - 120 > blue(c)*2) {
                        addToBlobs(new PVector(x * READING_RESOLUTION, y * READING_RESOLUTION), BlobType.CALIBRATION);
                    }*/
                    if (red(c) > 80 && red(c) > green(c) + 80 && red(c) > blue(c) + 80) { // TODO Identify center blob by position
                        // println("red dot at: " + x*READING_RESOLUTION + " / " + y*READING_RESOLUTION);
                        addToBlobs(new PVector(x * READING_RESOLUTION, y * READING_RESOLUTION), BlobType.CENTER);
                    }

                    if (red(c) < 18 && green(c) < 18 && blue(c) < 18) {
                        // println("black dot at: " + x*READING_RESOLUTION + " / " + y*READING_RESOLUTION);
                        blackDots.add(new PVector(x * READING_RESOLUTION, y * READING_RESOLUTION));
                        ellipse(x * READING_RESOLUTION, y * READING_RESOLUTION, READING_RESOLUTION, READING_RESOLUTION);
                        addToBlobs(new PVector(x * READING_RESOLUTION, y * READING_RESOLUTION), BlobType.DOT);

                    }

                }

            }

        }

        // Remove to small blobs
        // println("blob count before filter: " + blobs.size());

        ListIterator<Blob> blobIterator = blobs.listIterator();
        while (blobIterator.hasNext()) {
            Blob blob = blobIterator.next();
            if (blob.getNumberOfPoints() < MIN_NUMBER_OF_POINTS_PER_BLOB) blobIterator.remove();
        }


        ListIterator<Blob> centerBlobsIterator = centerBlobs.listIterator();
        while (centerBlobsIterator.hasNext()) {
            Blob blob = centerBlobsIterator.next();
            if (blob.getNumberOfPoints() < MIN_NUMBER_OF_POINTS_PER_BLOB) centerBlobsIterator.remove();
        }

        ListIterator<Blob> calibrationBlobsIterator = calibrationBlobs.listIterator();
        while (calibrationBlobsIterator.hasNext()) {
            Blob blob = calibrationBlobsIterator.next();
            if (blob.getNumberOfPoints() < MIN_NUMBER_OF_POINTS_PER_BLOB) calibrationBlobsIterator.remove();
        }

        // println("blob count: " + blobs.size());

        // println(calibrationBlobs.size());

        // TODO Identify 3 closest blobs to center blob
        // TODO Maybe additionaly filter out to large blobs (Combine close blobs)

        // println(blobs.size());

        for(Blob centerBlob : centerBlobs){
            fill(255, 0, 0);
            ellipse(centerBlob.getCenterPoint().x, centerBlob.getCenterPoint().y, 40, 40);
        }
/*
        for(Blob blob : blobs){
            fill(100, 100, 100);
            ellipse(blob.startPositin.x, blob.startPositin.y, 40, 40);
        }
*/


        blobs.subList(0, Math.max(blobs.size()-3, 0)).clear();
        // println(":: " + blobs.size());

        for(Blob blob : blobs){
            fill(0, 0, 0);
            ellipse(blob.startPositin.x, blob.startPositin.y, 40, 40);
        }

        if (centerBlobs.size() == 1 && blobs.size() == 3)  {
            PVector centerPoint = centerBlobs.get(0).getCenterPoint();
            // Collections.sort(blobs);


            for (Blob blob : blobs) {
                fill(255, 255, 0);
                ellipse(blob.getCenterPoint().x, blob.getCenterPoint().y, 40, 40);

                // Calculate angle and distance
                int angle = (int) Math.toDegrees(Math.atan2(blob.getCenterPoint().y - centerPoint.y, blob.getCenterPoint().x - centerPoint.x));

                if (angle < 0) {
                    angle += 360;
                }

                // round angle
                // angle = (int)map(Math.round(map(angle, 0 ,360, 0, 12)), 0, 12, 0, 360);


                blob.setAngleToCenter(angle);
                int distance = new Double(Math.sqrt(Math.pow((blob.getCenterPoint().x - centerPoint.x), 2) + Math.pow((blob.getCenterPoint().y - centerPoint.y), 2))).intValue();
                blob.setDistanceToCenter(distance);

            }

            // Draw reading area


            // Draw center point
            fill(255, 0, 0);
            ellipse(centerPoint.x, centerPoint.y, 40, 40);

            // Sort blobs
            blobs.sort(Blob::compareTo);

            BlobGroup currentBlobGroup = new BlobGroup(centerBlobs, blobs, new Date().getTime());


            int totalBlobGroupDifference = 0;

            for (BlobGroup blobGroupB : blobGroupQue) {
                int diff = currentBlobGroup.compareToBlobGroup(blobGroupB);
                totalBlobGroupDifference += diff;
            }

            blobGroupQue.add(currentBlobGroup);

            if (blobGroupQue.size() > BLOB_QUE_SIZE)
                blobGroupQue.poll();

            // only update if there are changes since lase 10 frames

            if(currentBlobGroup.getMaxDistanceToCenter() <=  MAX_DISTANCE_TO_CENTER ){
                if(totalBlobGroupDifference <= blobGroupQue.size() * BLOB_QUE_TOLLERANCE_PER_BLOB &&
                        (lastSentBlobGroup == null || currentBlobGroup.compareToBlobGroup(lastSentBlobGroup) > BLOB_QUE_TOLLERANCE_PER_BLOB)){
                    for (Blob blob : blobs) {
                        println("distance: " + blob.getDistanceToCenter() + "  angle: " + blob.getAngleToCenter() + "°");
                    }
                    OscMessage myOscMessage = new OscMessage("/newTrack");
                /* add a value (an integer) to the OscMessage */
                    String blobAngleMsg = "";
                    for (Blob blob : blobs) {
                        blobAngleMsg = blobAngleMsg + blob.getAngleToCenter() + ";";
                    }

                    myOscMessage.add(blobAngleMsg);
                /* send the OscMessage to a remote location specified in myNetAddress */
                    // oscP5.send(myOscMessage, myBroadcastLocation);
                    OscP5.flush(myOscMessage, myRemoteLocation);

                    lastSentBlobGroup = currentBlobGroup; // set last sent blob group
                }

            }



        }





        delay(40);
        // The following does the same, and is faster when just drawing the image
        // without any additional resizing, transformations, or tint.
        //set(0, 0, cam);


    }

    public void addToBlobs(PVector point, BlobType type) {
        boolean createNewBlob = true;


        if (type == BlobType.DOT) {
            for (Blob blob : blobs) {
                if (blob.getBlobType() == type && blob.isPartOfBlob(point)) {
                    blob.addPointToBLob(point);
                    createNewBlob = false;
                    return;
                }
            }
            if (createNewBlob) blobs.add(new Blob(point, type));
        }

        if (type == BlobType.CENTER) {
            for (Blob centerBlob : centerBlobs) {
                if (centerBlob.getBlobType() == type && centerBlob.isPartOfBlob(point)) {
                    centerBlob.addPointToBLob(point);
                    createNewBlob = false;
                    return;
                }
            }
            if (createNewBlob) centerBlobs.add(new Blob(point, type));
        }

        if (type == BlobType.CALIBRATION) {
            for (Blob calibrationBlob : calibrationBlobs) {
                if (calibrationBlob.getBlobType() == type && calibrationBlob.isPartOfBlob(point)) {
                    calibrationBlob.addPointToBLob(point);
                    createNewBlob = false;
                    return;
                }
            }
            if (createNewBlob) calibrationBlobs.add(new Blob(point, type));
        }


    }

    public static void main(String[] args) {
        PApplet.main(ConeCodeReader.class.getName());
    }
}