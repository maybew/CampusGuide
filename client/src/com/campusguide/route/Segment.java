package com.campusguide.route;
//by Haseem Saheed

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class Segment {
	/** Points in this segment. **/
    private LatLng start;
    /** Turn instruction to reach next segment. **/
    private String instruction;
    /** Length of segment. **/
    private int length;
    /** Distance covered. **/
    private double distance;
    
    private String distanceText;
    private String timeText;
    private List<LatLng> points;

    /**
     * Create an empty segment.
     */

    public Segment() {
    }


    /**
     * Set the turn instruction.
     * @param turn Turn instruction string.
     */

    public void setInstruction(final String turn) {
            this.instruction = turn;
    }

    /**
     * Get the turn instruction to reach next segment.
     * @return a String of the turn instruction.
     */

    public String getInstruction() {
            return instruction;
    }

    /**
     * Add a point to this segment.
     * @param point GeoPoint to add.
     */

    public void setPoint(final LatLng point) {
            start = point;
    }

    /** Get the starting point of this 
     * segment.
     * @return a GeoPoint
     */

    public LatLng startPoint() {
            return start;
    }

    /** Creates a segment which is a copy of this one.
     * @return a Segment that is a copy of this one.
     */

    public Segment copy() {
            final Segment copy = new Segment();
            copy.start = start;
            copy.instruction = instruction;
            copy.length = length;
            copy.distance = distance;
            copy.distanceText = distanceText;
            copy.timeText = timeText;
            copy.points = points;
            return copy;
    }

    /**
     * @param length the length to set
     */
    public void setLength(final int length) {
            this.length = length;
    }

    /**
     * @return the length
     */
    public int getLength() {
            return length;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
            this.distance = distance;
    }

    /**
     * @return the distance
     */
    public double getDistance() {
            return distance;
    }
    
    public void setDistanceText(String text) {
    	this.distanceText = text;
    }
    
    public String getDistanceText() {
    	return distanceText;
    }
    
    public void setTimeText(String text) {
    	this.timeText = text;
    }
    
    public String getTimeText() {
    	return timeText;
    }
    
    public void setPoints(List<LatLng> points) {
    	this.points = points;
    }
    
    public List<LatLng> getPoints() {
    	return points;
    }

}