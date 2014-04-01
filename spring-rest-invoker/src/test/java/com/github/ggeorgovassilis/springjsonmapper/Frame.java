package com.github.ggeorgovassilis.springjsonmapper;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Frame of an animation
 * @author george georgovassilis
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Frame implements Serializable {

    int duration;
    String text;

    public int getDuration() {
	return duration;
    }

    public void setDuration(int duration) {
	this.duration = duration;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }
}
