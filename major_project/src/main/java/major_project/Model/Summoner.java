package major_project.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Summoner {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("accountId")
    @Expose
    private String accountId;
    @SerializedName("puuid")
    @Expose
    private String puuid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profileIconId")
    @Expose
    private Integer profileIconId;
    @SerializedName("revisionDate")
    @Expose
    private Long revisionDate;
    @SerializedName("summonerLevel")
    @Expose
    private Integer summonerLevel;
    @SerializedName("status")
    @Expose
    private Status status;

    public Status getStatus() {
    return status;
    }

    public String getId() {
    return id;
    }

    public void setId(String id) {
    this.id = id;
    }

    public String getAccountId() {
    return accountId;
    }

    public void setAccountId(String accountId) {
    this.accountId = accountId;
    }

    public String getPuuid() {
    return puuid;
    }

    public void setPuuid(String puuid) {
    this.puuid = puuid;
    }

    public String getName() {
    return name;
    }

    public void setName(String name) {
    this.name = name;
    }

    public Integer getProfileIconId() {
    return profileIconId;
    }

    public void setProfileIconId(Integer profileIconId) {
    this.profileIconId = profileIconId;
    }

    public Long getRevisionDate() {
    return revisionDate;
    }

    public void setRevisionDate(Long revisionDate) {
    this.revisionDate = revisionDate;
    }

    public Integer getSummonerLevel() {
    return summonerLevel;
    }

    public void setSummonerLevel(Integer summonerLevel) {
    this.summonerLevel = summonerLevel;
    }

    @Override
    public String toString() {
    return "Summoner{" +
    "id='" + id + '\'' +
    ", accountId='" + accountId + '\'' +
    ", puuid='" + puuid + '\'' +
    ", name='" + name + '\'' +
    ", profileIconId=" + profileIconId +
    ", revisionDate=" + revisionDate +
    ", summonerLevel=" + summonerLevel +
    '}';
    }

}