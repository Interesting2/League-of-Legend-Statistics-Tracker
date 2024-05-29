package major_project.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MiniSeries {

    @SerializedName("target")
    @Expose
    private Integer target;
    @SerializedName("wins")
    @Expose
    private Integer wins;
    @SerializedName("losses")
    @Expose
    private Integer losses;
    @SerializedName("progress")
    @Expose
    private String progress;

    public Integer getTarget() {
    return target;
    }

    public void setTarget(Integer target) {
    this.target = target;
    }

    public Integer getWins() {
    return wins;
    }

    public void setWins(Integer wins) {
    this.wins = wins;
    }

    public Integer getLosses() {
    return losses;
    }

    public void setLosses(Integer losses) {
    this.losses = losses;
    }

    public String getProgress() {
    return progress;
    }

    public void setProgress(String progress) {
    this.progress = progress;
    }

    @Override
    public String toString() {
    return "MiniSeries{" +
    "target=" + target +
    ", wins=" + wins +
    ", losses=" + losses +
    ", progress=" + progress +
    '}';
    }

}
