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

    public static int BLOB_QUE_SIZE = 10;
    public static int BLOB_QUE_TOLLERANCE_PER_BLOB = 10;


    public static int SOURDUNDING_PIXEL_RESOLUTION = 4;
    public static int CAMERA_WIDTH = 640;
    public static int CAMERA_HEIGHT = 480;

    ControlP5 cp5;
    ArrayList<Blob> centerBlobs = new ArrayList<>();
    ArrayList<Blob> lastFrameBlobs = new ArrayList<>();
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
            cam = new Capture(this, cameras[0]);

            cam.start();
        }

        cp5 = new ControlP5(this);
        cp5.addColorWheel("colorCenter", 3 * width / 4 - 200, 200, 200).setRGB(color(30, 30, 30));
        cp5.addColorWheel("colorDots", 3 * width / 4 - 200, 580, 200).setRGB(color(128, 20, 20));

        myRemoteLocation = new NetAddress("127.0.0.1", 32000);

    }

    public void draw() {

        // println(cp5.get(ColorWheel.class,"colorCenter").getRGB());

        // Background
        background(255);
        noStroke();
        fill(45);
        rect(width / 2, 0, width / 2, height);

        lastFrameBlobs =  blobs;
        blobs = new ArrayList<>(); // reset blobs
        centerBlobs = new ArrayList<>(); // reset blobs


        ArrayList<PVector> blackDots = new ArrayList();

        if (cam.available() == true) {
            image(cam, 0, height / 2);
            cam.read();
            for (int x = 0; x < CAMERA_WIDTH / READING_RESOLUTION; x++) {
                for (int y = 0; y < CAMERA_HEIGHT / READING_RESOLUTION; y++) {
                    int c = cam.get(x * READING_RESOLUTION, y * READING_RESOLUTION);
                    fill(c);
                    if (red(c) < 200 && green(c) < 200 && blue(c) > 180) {
                        // println("blue dot at: " + x*READING_RESOLUTION + " / " + y*READING_RESOLUTION);
                    }
                    if (red(c) > 90 && red(c) > green(c) + 30) {
                        // println("red dot at: " + x*READING_RESOLUTION + " / " + y*READING_RESOLUTION);
                        addToBlobs(new PVector(x * READING_RESOLUTION, y * READING_RESOLUTION), BlobType.CENTER);
                    }

                    if (red(c) < 40 && green(c) < 40 && blue(c) < 40) {
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

        // println("blob count: " + blobs.size());

        if (blobs.size() == 3 && centerBlobs.size() == 1) {
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

            // TODO only update if there are changes since lase frame


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

/*

        float angle = (float) Math.toDegrees(Math.atan2(target.y - y, target.x - x));

        if(angle < 0){
            angle += 360;
        }
*/
        /*
        for(PVector blackDot: blackDots){
            ellipse(blackDot.x, blackDot.x, 8, 8);
        }

        ellipse(40, 40, 8, 8);
        ellipse(width-40, 40, 8, 8);
        ellipse(40, height-40, 8, 8);
        ellipse(width-40, height-40, 8, 8);*/


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


    }

    public static void main(String[] args) {
        PApplet.main(ConeCodeReader.class.getName());
    }
}