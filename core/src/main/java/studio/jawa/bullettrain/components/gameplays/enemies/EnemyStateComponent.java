package studio.jawa.bullettrain.components.gameplays.enemies;

import com.badlogic.ashley.core.Component;

public class EnemyStateComponent implements Component {
      public enum STATES  {
      IDLE,
          CHASE,
          STRAFE,
      ATTACK
    };

      public STATES state;

      public EnemyStateComponent(EnemyStateComponent.STATES state) {
          this.state = state;
      }
}
