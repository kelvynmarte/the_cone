package dk.ciid.cone;

import oscP5.OscP5;
import processing.core.PVector;

import java.util.ArrayList;

import static dk.ciid.cone.ConeCodeReader.MAX_DISTANCE_TO_START_POINT;

/**
 * Created by kelvyn on 16/03/2017.
 */
public class Blob implements Comparable<Blob> {

    ArrayList<PVector> points = new ArrayList<>();
    PVector startPositin;
    PVector centerPoint;
    BlobType blobType;
    int angleToCenter;
    int distanceToCenter;
    OscP5 oscP5;



    public Blob(PVector startPositin, BlobType blobType) {
        this.startPositin = startPositin;
        this.points.add(startPositin);
        this.blobType = blobType;
    }

    public boolean isPartOfBlob(PVector pos){
       return (new Double(Math.sqrt(Math.pow((startPositin.x - pos.x), 2) + Math.pow((startPositin.y - pos.y), 2))).intValue() < MAX_DISTANCE_TO_START_POINT);
        //return Math.abs(startPositin.x - pos.x) < MAX_DISTANCE_TO_START_POINT && Math.abs(startPositin.x - pos.x) < MAX_DISTANCE_TO_START_POINT;
    }

    public void addPointToBLob(PVector point){
        this.points.add(point);
    }

    public BlobType getBlobType() {
        return blobType;
    }

    public int getNumberOfPoints(){
        return points.size();
    }

    public PVector getCenterPoint(){
        int x = 0;
        int y = 0;
        for(PVector point : points){
            x+=point.x;
            y+=point.y;
        }
        return new PVector(x/points.size(),y/points.size());
    }

    public int getAngleToCenter() {
        return angleToCenter;
    }

    public void setAngleToCenter(int angleToCenter) {
        this.angleToCenter = angleToCenter;
    }

    public int getDistanceToCenter() {
        return distanceToCenter;
    }

    public void setDistanceToCenter(int distanceToCenter) {
        this.distanceToCenter = distanceToCenter;
    }

    @Override
    public int compareTo(Blob b) {
        return b.distanceToCenter - this.distanceToCenter;
    }

    public ArrayList<PVector> getPoints() {
        return points;
    }
}
