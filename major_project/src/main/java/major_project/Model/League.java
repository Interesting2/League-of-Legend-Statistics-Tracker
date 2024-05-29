package major_project.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class League {

    @SerializedName("queueType")
    @Expose
    private String queueType;
    @SerializedName("summonerId")
    @Expose
    private String summonerId;
    @SerializedName("summonerName")
    @Expose
    private String summonerName;
    @SerializedName("leaguePoints")
    @Expose
    private Integer leaguePoints;
    @SerializedName("wins")
    @Expose
    private Integer wins;
    @SerializedName("losses")
    @Expose
    private Integer losses;
    @SerializedName("veteran")
    @Expose
    private Boolean veteran;
    @SerializedName("inactive")
    @Expose
    private Boolean inactive;
    @SerializedName("freshBlood")
    @Expose
    private Boolean freshBlood;
    @SerializedName("hotStreak")
    @Expose
    private Boolean hotStreak;
    @SerializedName("leagueId")
    @Expose
    private String leagueId;
    @SerializedName("tier")
    @Expose
    private String tier;
    @SerializedName("rank")
    @Expose
    private String rank;
    @SerializedName("miniSeries")
    @Expose
    private MiniSeries miniSeries;

    @SerializedName("status")
    @Expose
    private Status status;

    public Status getStatus() {
    return status;
    }

    public String getQueueType() {
    return queueType;
    }

    public void setQueueType(String queueType) {
    this.queueType = queueType;
    }

    public String getSummonerId() {
    return summonerId;
    }

    public void setSummonerId(String summonerId) {
    this.summonerId = summonerId;
    }

    public String getSummonerName() {
    return summonerName;
    }

    public void setSummonerName(String summonerName) {
    this.summonerName = summonerName;
    }

    public Integer getLeaguePoints() {
    return leaguePoints;
    }

    public void setLeaguePoints(Integer leaguePoints) {
    this.leaguePoints = leaguePoints;
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

    public Boolean getVeteran() {
    return veteran;
    }

    public void setVeteran(Boolean veteran) {
    this.veteran = veteran;
    }

    public Boolean getInactive() {
    return inactive;
    }

    public void setInactive(Boolean inactive) {
    this.inactive = inactive;
    }

    public Boolean getFreshBlood() {
    return freshBlood;
    }

    public void setFreshBlood(Boolean freshBlood) {
    this.freshBlood = freshBlood;
    }

    public Boolean getHotStreak() {
    return hotStreak;
    }

    public void setHotStreak(Boolean hotStreak) {
    this.hotStreak = hotStreak;
    }

    public String getLeagueId() {
    return leagueId;
    }

    public void setLeagueId(String leagueId) {
    this.leagueId = leagueId;
    }

    public String getTier() {
    return tier;
    }

    public void setTier(String tier) {
    this.tier = tier;
    }

    public String getRank() {
    return rank;
    }

    public void setRank(String rank) {
    this.rank = rank;
    }

    public MiniSeries getMiniSeries() {
    return miniSeries;
    }

    public void setMiniSeries(MiniSeries miniSeries) {
    this.miniSeries = miniSeries;
    }

    @Override
    public String toString() {
    return "League{" +
    "queueType='" + queueType + '\'' +
    ", summonerId='" + summonerId + '\'' +
    ", summonerName='" + summonerName + '\'' +
    ", leaguePoints=" + leaguePoints +
    ", wins=" + wins +
    ", losses=" + losses +
    ", veteran=" + veteran +
    ", inactive=" + inactive +
    ", freshBlood=" + freshBlood +
    ", hotStreak=" + hotStreak +
    ", leagueId='" + leagueId + '\'' +
    ", tier='" + tier + '\'' +
    ", rank='" + rank + '\'' +
    ", miniSeries=" + miniSeries +
    '}';
    }

}