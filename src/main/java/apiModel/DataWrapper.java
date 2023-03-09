package apiModel;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.annotations.SerializedName;
import dbModel.Data;
import dbModel.Metadata;

public class DataWrapper {
    private Metadata metadata;
    @SerializedName("info")
    private Data data;

    @JsonProperty("metadata")
    public Metadata getMetadata() { return metadata; }
    @JsonProperty("metadata")
    public void setMetadata(Metadata value) { this.metadata = value; }

    @JsonProperty("info")
    public Data getData() { return data; }
    @JsonProperty("info")
    public void setData(Data value) { this.data = value; }
}
