package org.mpad.host_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO representing the decrypted JSON payload received over UDP.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandPayload {

    @JsonProperty("type")
    private String type;

    @JsonProperty("x")
    private float x;

    @JsonProperty("y")
    private float y;

    @JsonProperty("text")
    private String text;

    @JsonProperty("time")
    private long time;

    // --- FIX: Added the 'data' field that CommandProcessorService is looking for ---
    @JsonProperty("data")
    private String data;

    public CommandPayload() {}

    // --- Getters and Setters ---

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

    // --- FIX: Added the missing methods so command.getData() will compile! ---
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}