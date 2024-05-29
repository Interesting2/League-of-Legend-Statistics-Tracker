package major_project.Presenter;

import major_project.Model.GameModel;
import major_project.View.GameView;

import java.util.InputMismatchException;

import javafx.application.Platform;
import major_project.Model.CreditObservers;

/** 
 * This class is used to handle the change in credit
 * This is also a observer class that observes the CreditObservers interface
 */
public class CreditTrackerPresenter implements CreditObservers {
    
    private GameModel gameModel;
    private GameView gameView;
    private int creditCounter;  // remaining credit amount

    public CreditTrackerPresenter(GameModel model, GameView view) {
        this.gameModel = model;
        model.registerCreditObserver(this);
        this.gameView = view;
    }

    /** 
     * This method is called by the model to decrement the credit amount
     * and updates the amount in the view
     */
    public void update() {
        this.creditCounter -= 1;

        Platform.runLater(() -> {
            // updates view with updated credit amount
            gameView.updateCredit(creditCounter);
        });
    }

    /** 
     * This method is to save the chosen credit amount by the user in model
     * Sets the initial credit amount in the view
     * @param credit the chosen credit amount
     */
    public void submitCredit(String credit) {
        try {
            // save credit amount to model
            gameModel.saveCredit(credit);
            // check if credit can be parsed to integer
            this.creditCounter = Integer.parseInt(credit);
            gameView.updateCredit(this.creditCounter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (InputMismatchException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }
}
