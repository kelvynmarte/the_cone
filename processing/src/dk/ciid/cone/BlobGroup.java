package dk.ciid.cone;

import java.util.ArrayList;

/**
 * Created by kelvyn on 17/03/2017.
 */
public class BlobGroup {

    ArrayList<Blob> lastFrameBlobs = new ArrayList<>();
    ArrayList<Blob> blobs = new ArrayList<>();
    long timestamp;

    public BlobGroup(ArrayList<Blob> lastFrameBlobs, ArrayList<Blob> blobs, long timestamp) {
        this.lastFrameBlobs = lastFrameBlobs;
        this.blobs = blobs;
        this.timestamp = timestamp;
    }

    public ArrayList<Blob> getLastFrameBlobs() {
        return lastFrameBlobs;
    }

    public ArrayList<Blob> getBlobs() {
        return blobs;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int compareToBlobGroup(BlobGroup blogGroupB){
        int difference = 0;

        int blobCount = Math.min(this.getBlobs().size(), blogGroupB.getBlobs().size());
        for(int i = 0; i < blobCount; i++ ){
            difference += Math.abs(this.getBlobs().get(i).getAngleToCenter() - blogGroupB.getBlobs().get(i).getAngleToCenter());
        }
        return difference;
    }

    public int getMaxDistanceToCenter(){
        int maxDistance = 0;
        for(Blob blob : blobs){
            if(blob.getDistanceToCenter() > maxDistance){
                maxDistance = blob.getDistanceToCenter();
            }
        }
        return maxDistance;
    }
}
