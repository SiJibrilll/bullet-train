package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import studio.jawa.bullettrain.components.gameplay.PlayerComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.data.GameConstants;

public class CameraSystem extends IteratingSystem {

    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<PlayerComponent> playerMapper;

    private OrthographicCamera camera;
    private float cameraSmoothing = 2.0f;

    public CameraSystem(OrthographicCamera camera) {
        super(Family.all(PlayerComponent.class, TransformComponent.class).get());

        this.camera = camera;
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);

        setupCamera();
    }

    private void setupCamera() {
        // Set camera to show full carriage width
        camera.viewportWidth = GameConstants.CARRIAGE_WIDTH + 200f; // Extra space for UI/padding
        camera.viewportHeight = 800f; // Vertical viewport
        camera.update();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = transformMapper.get(entity);
        PlayerComponent player = playerMapper.get(entity);

        // Target camera position (follow player)
        float targetX = transform.position.x;
        float targetY = transform.position.y;

        // Apply camera constraints for current carriage
        applyCameraConstraints(player, targetX, targetY);

        // Smooth camera movement
        smoothCameraFollow(targetX, targetY, deltaTime);
    }

    private void applyCameraConstraints(PlayerComponent player, float targetX, float targetY) {
        // Calculate carriage bounds for current carriage
        float carriageOffsetY = (player.currentCarriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;
        
        // Horizontal constraints (keep camera centered on carriage)
        float constrainedX = GameConstants.CARRIAGE_WIDTH / 2f;
        
        // Vertical constraints - allow some overlap between carriages
        float viewportHalf = camera.viewportHeight / 2f;
        float minY = Math.max(viewportHalf, carriageOffsetY + viewportHalf);
        float maxY = carriageOffsetY + GameConstants.CARRIAGE_HEIGHT - viewportHalf;
        
        // Allow camera to show parts of adjacent carriages
        float overlapZone = 200f; // pixels
        minY -= overlapZone;
        maxY += overlapZone;
        
        // Apply constraints
        camera.position.x = constrainedX; // Fixed horizontal
        
        // Smooth vertical following with constraints
        float constrainedY = Math.max(minY, Math.min(maxY, targetY));
        
        // Store constrained target for smooth following
        float currentY = camera.position.y;
        float newY = currentY + (constrainedY - currentY) * cameraSmoothing * 0.016f; // Assume 60fps
        
        camera.position.y = newY;
    }

    private void smoothCameraFollow(float targetX, float targetY, float deltaTime) {
        // Smooth interpolation to target position
        float currentX = camera.position.x;
        float currentY = camera.position.y;

        float newX = MathUtils.lerp(currentX, targetX, cameraSmoothing * deltaTime);
        float newY = MathUtils.lerp(currentY, targetY, cameraSmoothing * deltaTime);

        camera.position.set(newX, newY, 0);
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setCameraSmoothing(float smoothing) {
        this.cameraSmoothing = smoothing;
    }
}
