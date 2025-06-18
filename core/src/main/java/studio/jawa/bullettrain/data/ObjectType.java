package studio.jawa.bullettrain.data;

public enum ObjectType {
    BARREL("textures/objects/barrel.png", 96f, 96f),
    BOX("textures/objects/box.png", 96f, 96f),
    CHAIR("textures/objects/chair.png", 96f, 96f);
    // TNT("textures/objects/tnt.png", 96f, 96f);
    
    public final String texturePath;
    public final float width;
    public final float height;
    
    ObjectType(String texturePath, float width, float height) {
        this.texturePath = texturePath;
        this.width = width;
        this.height = height;
    }
    
    public static ObjectType getRandomType() {
        ObjectType[] values = values();
        return values[com.badlogic.gdx.math.MathUtils.random(values.length - 1)];
    }
}
