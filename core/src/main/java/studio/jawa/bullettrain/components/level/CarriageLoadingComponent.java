package studio.jawa.bullettrain.components.level;

import com.badlogic.ashley.core.Component;

public class CarriageLoadingComponent implements Component {
    public LoadingState state = LoadingState.UNLOADED;
    public boolean isPlayerInside = false;
    public float transitionProgress = 0f; 
    
    public CarriageLoadingComponent() {}
    
    public enum LoadingState {
        UNLOADED,    // Not in memory
        LOADING,     // Being generated
        ACTIVE,      // Fully loaded and active
        UNLOADING    // Being removed
    }
}