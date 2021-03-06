package de.pcfreak9000.space.tileworld.tile;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.resource.helper.TextureHelper;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathd;
import de.pcfreak9000.space.core.registry.GameRegistry;
import de.pcfreak9000.space.tileworld.TileWorld;
import de.pcfreak9000.space.util.RegisterSensitive;

@RegisterSensitive(registry = "TILE_REGISTRY")
public class Tile {
    
    public static final float MAX_LIGHT_VALUE = 16;
    
    public static final Tile EMPTY = new Tile();//TODO replace with usefulness
    
    public static final float TILE_SIZE = 16 * 1.5f;
    
    public static int toGlobalTile(float x) {
        return (int) Mathd.floor(x / (double) TILE_SIZE);
    }
    
    static {
        EMPTY.setBouncyness(0);
        EMPTY.setCanBreak(false);
        EMPTY.setFilterColor(null);
        EMPTY.setLightColor(null);
        EMPTY.setOpaque(false);
        EMPTY.setTexture(null);
        EMPTY.setSolid(false);
        EMPTY.color().set(0, 0, 0, 0);
        EMPTY.setSunLightLoss(0);
        //EMPTY.setLightLoss(0.0f);
        GameRegistry.TILE_REGISTRY.register("empty", EMPTY);
    }
    
    private String textureName = null;
    private Texture texture = null;
    
    private boolean canBreak = true;
    private boolean opaque = false;
    private boolean solid = true;
    
    private final Color color = new Color();
    
    private Color lightColor;
    private float lightloss = 1;
    private float sunlightloss = 1;
    
    private Color filterColor;
    
    private float bouncyness = 0;
    
    public void setTexture(String name) {
        this.textureName = name;
    }
    
    public Texture getTexture() {
        return this.texture;
    }
    
    public void setBouncyness(float b) {
        this.bouncyness = b;
    }
    
    public float getBouncyness() {
        return this.bouncyness;
    }
    
    public void setCanBreak(boolean b) {
        this.canBreak = b;
    }
    
    public boolean canBreak() {
        return this.canBreak;
    }
    
    public void setOpaque(boolean b) {
        this.opaque = b;
    }
    
    public boolean isOpaque() {
        return this.opaque;
    }
    
    public void setSolid(boolean b) {
        this.solid = b;
    }
    
    public boolean isSolid() {
        return this.solid;
    }
    
    public void setLightColor(Color color) {
        this.lightColor = color;
    }
    
    public Color getLightColor() {
        return this.lightColor;
    }
    
    public boolean hasLight() {
        return this.lightColor != null && this.lightColor.maxRGB() >= 1;
    }
    
    public void setFilterColor(Color color) {
        this.filterColor = color;
    }
    
    public Color getFilterColor() {
        return this.filterColor;
    }
    
    public boolean hasLightFilter() {
        return this.filterColor != null;
    }
    
    public Color color() {
        return this.color;
    }
    
    public float getLightLoss() {
        return this.lightloss;
    }
    
    public void setLightLoss(float f) {
        this.lightloss = f;
    }
    
    public float getSunLightLoss() {
        return this.sunlightloss;
    }
    
    public void setSunLightLoss(float f) {
        this.sunlightloss = f;
    }
    
    public void init(TextureHelper tileTextures) {
        if (this.textureName != null) {
            this.texture = tileTextures.get(this.textureName);
        }
    }
    
    public boolean hasTileEntity() {
        return false;
    }
    
    public TileEntity createTileEntity(TileWorld world, TileState myState) {
        return null;
    }
    
    public void neighbourChanged(TileWorld world, TileState neighbour) {
        //?!?!?!?
    }
    
    @Override
    public String toString() {
        return String.format("Tile[texture=%s]", this.textureName);
    }
    
}
