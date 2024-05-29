package major_project.Model;

public class OnlineStrategy implements StatusStrategy{

    public Summoner searchSummoner(String input_api_key, String name, RequestModel requestModel, JsonModel jsonModel) {
        System.out.println("Handling requests");
        String response = requestModel.searchSummonerRequest(input_api_key, name, "online");
        System.out.println("Handled requests");
        Summoner summoner = jsonModel.parseSummoner(response);
        return summoner;
    }

    public League[] getLeagues(String input_api_key, String summonerId, RequestModel requestModel, JsonModel jsonModel) {
        String response = requestModel.searchLeaguesRequest(input_api_key, summonerId, "online");
        League[] leagues = jsonModel.parseLeagues(response);
        return leagues;
    }

    public void sendMessage(String data, String sid, String key, RequestModel requestModel) {
        requestModel.sendMessage(data, sid, key);
    }

    public RedditToken loginRequest(String data, RequestModel requestModel, JsonModel jsonModel) {
        String key = System.getenv("REDDIT_API_KEY");
        String secret = System.getenv("REDDIT_API_SECRET");
        try {
            String response = requestModel.loginRequest(data, key, secret);
            RedditToken redditToken = jsonModel.parseRedditToken(response);
            return redditToken;
        } catch (IllegalStateException e) {
            throw e;
        }
    }

    public void postRequest(String postText, String accessToken, RequestModel requestModel, JsonModel jsonModel) {
        requestModel.postRequest(postText, accessToken);
    }
}
