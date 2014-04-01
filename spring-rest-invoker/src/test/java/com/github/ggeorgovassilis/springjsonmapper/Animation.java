package com.github.ggeorgovassilis.springjsonmapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Animation consisting of metadata and frames
 * 
 * @author george georgovassilis
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Animation implements Serializable {

    String id;
    String comment;
    int frameSize;
    Date lastUpdated;
    String status;
    String title;
    List<Frame> frames = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    public int getFrameSize() {
	return frameSize;
    }

    public void setFrameSize(int frameSize) {
	this.frameSize = frameSize;
    }

    public Date getLastUpdated() {
	return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
	this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public List<Frame> getFrames() {
	return frames;
    }

    public void setFrames(List<Frame> frames) {
	this.frames = frames;
    }
}
