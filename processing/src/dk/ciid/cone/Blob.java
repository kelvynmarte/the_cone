package dk.ciid.cone;

import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by kelvyn on 16/03/2017.
 */
public class Blob {
    public static int MAX_DISTANCE_TO_START_POINT = 28;

    ArrayList<PVector> points = new ArrayList<>();
    PVector startPositin;
    PVector centerPoint;
    BlobType blobType;



    public Blob(PVector startPositin, BlobType blobType) {
        this.startPositin = startPositin;
        this.points.add(startPositin);
        this.blobType = blobType;
    }

    public boolean isPartOfBlob(PVector pos){
        return Math.abs(startPositin.x - pos.x) < MAX_DISTANCE_TO_START_POINT && Math.abs(startPositin.x - pos.x) < MAX_DISTANCE_TO_START_POINT;
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
            x++;
            y++;
        }
        return new PVector(x/points.size(),y/points.size());
    }
}
