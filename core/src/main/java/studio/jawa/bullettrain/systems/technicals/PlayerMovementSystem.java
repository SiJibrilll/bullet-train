package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.gameplay.DeathComponent;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;
import studio.jawa.bullettrain.data.GameConstants;

public class PlayerMovementSystem extends IteratingSystem {

    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<VelocityComponent> velocityMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<GeneralStatsComponent> statsMapper;
    private ComponentMapper<DeathComponent> dm;

    public PlayerMovementSystem() {
        super(Family.all(PlayerComponent.class, TransformComponent.class, VelocityComponent.class, GeneralStatsComponent.class).get());

        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        statsMapper = ComponentMapper.getFor(GeneralStatsComponent.class);
        dm = ComponentMapper.getFor(DeathComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = transformMapper.get(entity);
        VelocityComponent velocity = velocityMapper.get(entity);
        PlayerComponent player = playerMapper.get(entity);
        GeneralStatsComponent stats = statsMapper.get(entity);
        DeathComponent death = dm.get(entity);

        // Handle input
        if (death == null) {
            handleInput(velocity, stats);
        }

        // Apply movement
        applyMovement(transform, velocity, deltaTime);

        // Keep player within bounds
        constrainToPlayableArea(entity, transform);
    }

    private void handleInput(VelocityComponent velocity, GeneralStatsComponent stats) {
        Vector2 inputVelocity = new Vector2();

        // WASD movement
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            inputVelocity.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            inputVelocity.x += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            inputVelocity.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            inputVelocity.y -= 1;
        }

        // Normalize diagonal movement
        if (inputVelocity.len() > 0) {
            inputVelocity.nor().scl(stats.speed);
        }

        velocity.velocity.set(inputVelocity);
    }

    private void applyMovement(TransformComponent transform, VelocityComponent velocity, float deltaTime) {
        transform.position.x += velocity.velocity.x * deltaTime;
        transform.position.y += velocity.velocity.y * deltaTime;
    }

   private void constrainToPlayableArea(Entity entity, TransformComponent transform) {
        PlayerComponent player = playerMapper.get(entity);

        // Calculate current carriage bounds
        float carriageOffsetY = (player.currentCarriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;

        // Horizontal bounds
        float leftBound = (GameConstants.CARRIAGE_WIDTH - GameConstants.PLAYABLE_WIDTH) / 2f + 20f;
        float rightBound = leftBound + GameConstants.PLAYABLE_WIDTH - 40f;

        // Vertical bounds
        float bottomBound = carriageOffsetY + 20f;
        float topBound = carriageOffsetY + GameConstants.CARRIAGE_HEIGHT - 20f;

        // Constrain position
        transform.position.x = Math.max(leftBound, Math.min(rightBound, transform.position.x));
        transform.position.y = Math.max(bottomBound, Math.min(topBound, transform.position.y));
    }
}
