package studio.jawa.bullettrain.data;

public enum ObjectType {
    BARREL("textures/objects/barrel.png", 50f, 50f),
    BOX("textures/objects/box.png", 45f, 45f),
    CHAIR("textures/objects/chair.png", 40f, 40f),
    TNT("textures/objects/tnt.png", 35f, 35f);
    
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
