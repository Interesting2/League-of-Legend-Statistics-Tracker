package major_project.Presenter;

import javafx.application.Platform;
import major_project.Model.GameModel;
import major_project.View.GameView;
import major_project.View.RedditView;

public class RedditPresenter {

    private GameView view;
    private GameModel model;
    private RedditView redditView;

    public RedditPresenter(GameModel model, GameView view) {
        this.view = view;
        this.model = model;

        this.redditView = new RedditView(this);
    }

    public void login() {
        redditView.showLoginPage(view);
    }

    /**
     * This method is to control the reddit login request
     * @param username  the username user provides
     * @param password the password user provides
     */
    public void loginRequest(String username, String password) {
        view.displayProgressIndicator(true);
        Runnable redditLogin = () -> {
            try {
                String accessToken = model.login(username, password);
                Platform.runLater(() -> {
                    view.displayProgressIndicator(false);
                    if (accessToken != null) {
                        redditView.displaySuccess();
                    } else redditView.displayFail();
                });
            } catch (IllegalStateException e) {
                Platform.runLater(() -> {
                    view.displayProgressIndicator(false);
                    view.displayError(e.getMessage());
                });
            }

        };
        Thread redditLoginThread = new Thread(redditLogin);
        redditLoginThread.start();
    }

    /**
     * This method is to control the reddit post request
     * @param postText the post text submission message
     */
    public void postRequest(String postText) {
        // set visible for the last progress indicator in the list
        view.displayProgressIndicator(true); 

        Runnable sendMessage = () -> {
            try {
                model.post(postText);
            } catch (IllegalStateException e) {
                Platform.runLater(() -> {
                    view.displayError(e.getMessage());
                });
            }
            Platform.runLater(() -> {
                view.displayProgressIndicator(false);
                System.out.println("Disvisible\n\n");
            });
        };

        Thread sendMessageThread = new Thread(sendMessage);
        sendMessageThread.setDaemon(true);
        sendMessageThread.start();
    }


    public void post() {
        redditView.showPostPage(view);
    }
    
}
