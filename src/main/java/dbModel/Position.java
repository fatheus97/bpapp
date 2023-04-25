package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;

@Embeddable
public class Position {
    private long x;
    private long y;

    @JsonProperty("x")
    public long getX() {
        return x;
    }

    @JsonProperty("x")
    public void setX(long value) {
        this.x = value;
    }

    @JsonProperty("y")
    public long getY() {
        return y;
    }

    @JsonProperty("y")
    public void setY(long value) {
        this.y = value;
    }
}
