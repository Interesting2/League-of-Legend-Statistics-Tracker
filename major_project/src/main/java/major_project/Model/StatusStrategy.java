package major_project.Model;

/** 
 * This interface is for the offline/online strategy 
 */
public interface StatusStrategy {

    /**
     * This method is for searching summoner
     * @param input_api_key
     * @param name
     * @param requestModel
     * @param jsonModel
     * @return the Returned Summoner object
     */
    public Summoner searchSummoner(String input_api_key, String name, RequestModel requestModel, JsonModel jsonModel);

    /**
     * This method is for getting the leagues
     * @param input_api_key
     * @param summonerId
     * @param requestModel
     * @param jsonModel
     * @return the Returned League array
     */
    public League[] getLeagues(String input_api_key, String summonerId, RequestModel requestModel, JsonModel jsonModel);

    /**
     * This method is for sending message through twilio
     * @param data
     * @param sid
     * @param key
     * @param requestModel
     */
    public void sendMessage(String data, String sid,  String key, RequestModel requestModel);

    /**
     * This method is for logging in to reddit
     * @param data
     * @param requestModel
     * @param jsonModel
     * @return the Returned RedditToken object
     */
    public RedditToken loginRequest(String data, RequestModel requestModel, JsonModel jsonModel);

    /**
     * This method is for posting to reddit
     * @param postText
     * @param accessToken
     * @param requestModel
     * @param jsonModel
     */
    public void postRequest(String postText, String accessToken, RequestModel requestModel, JsonModel jsonModel);
}