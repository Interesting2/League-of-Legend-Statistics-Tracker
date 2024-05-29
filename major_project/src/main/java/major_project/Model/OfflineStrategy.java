package major_project.Model;

public class OfflineStrategy implements StatusStrategy{
    
    public Summoner searchSummoner(String input_api_key, String name, RequestModel requestModel, JsonModel jsonModel) {
        Summoner summoner = jsonModel.offlineParseSummoner();
        return summoner;
    }
    
    public League[] getLeagues(String input_api_key, String summonerId, RequestModel requestModel, JsonModel jsonModel) {
        League[] leagues = jsonModel.offlineParseLeagues();
        return leagues;
    }

    public void sendMessage(String data, String sid, String key, RequestModel requestModel) {
        return;
    }

    public RedditToken loginRequest(String data, RequestModel requestModel, JsonModel jsonModel) {
        return null;
    }

    public void postRequest(String postText, String accessToken, RequestModel requestModel, JsonModel jsonModel) {

    }
    
}
