package major_project.View;

import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;

/** 
 * This is the class to show the cache dialog box view
 */
public class CacheHitView {
    private GameView gameView;

    public CacheHitView(GameView gameView) {
        this.gameView = gameView;
    }

    /**
     * This shows the cache hit dialog
     * @return whether user chose to use cached data or not
     */
    public boolean displayCacheHitDialogBox() {
        // create a dialog box
        boolean cacheState = false;
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cache or Request Data");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(gameView.getScene().getWindow());

        // create a choicedialog
        ChoiceDialog<String> dialog = new ChoiceDialog<>("", new String[]{"Cached Data", "New Data"});
        dialog.setTitle("Cache Hit");
        dialog.setHeaderText("Use Cache or Request New Data");
        dialog.setContentText("Choose a method");

        Optional<String> input = dialog.showAndWait();

        if (input.isPresent()) {
            String choice = input.get();

            if (choice.equals("Cached Data")) {
                cacheState = true;
            } else if (choice.equals("New Data")) {
                cacheState = false;
            }
            return cacheState;
        }
        throw new IllegalStateException("User didn't select a method");
    }
}
