package major_project.Presenter;

import major_project.Model.GameModelObserver;
import major_project.View.CacheHitView;
import major_project.View.GameView;
import major_project.Model.GameModel;

public class CacheHitPresenter implements GameModelObserver{
    
    private final GameModel model;
    private final CacheHitView cacheHitView;

    private boolean cacheState;

    public CacheHitPresenter(GameModel model, GameView gameView) {
        this.model = model;
        this.cacheHitView = new CacheHitView(gameView);
        
        model.registerObserver(this);
    }

    /**
    * This method helps to determine to cache data or request fresh data
    * @return a boolean indicating whether to cache data or request fresh data
    */ 
    @Override
    public void update(boolean is_error) throws IllegalStateException{
        if (!is_error) {
           cacheState = cacheHitView.displayCacheHitDialogBox();
        } else {
            cacheState = false;
        }
    }

    /**
     * This method returns the cache state
     * @return a boolean indicating whether to cache data or request fresh data
     */
    public boolean getCacheState() {
        return cacheState;
    }
}
