package studio.jawa.bullettrain.components.level;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class OpenLayoutComponent implements Component {
    // Layout positions untuk berbagai elemen
    public Array<Vector2> obstaclePositions = new Array<>(); 
    public Array<Vector2> enemySpawnPoints = new Array<>();
    public Array<Vector2> pickupSpawnPoints = new Array<>();
    
    // NEW: Store actual object entities
    public Array<Entity> objectEntities = new Array<>();
    
    // Entry & Exit points
    public Vector2 entryPoint = new Vector2();
    public Vector2 exitPoint = new Vector2();
    
    public OpenLayoutComponent() {
        entryPoint.set(300f, 100f);   
        exitPoint.set(300f, 1500f);   
    }
}