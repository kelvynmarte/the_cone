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
}
