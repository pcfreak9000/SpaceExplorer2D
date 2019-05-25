package de.pcfreak9000.space.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix3x2f;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.ReflectedBatch2D;
import de.omnikryptec.render.batch.vertexmanager.UnorderedCachedvertexManager;
import de.omnikryptec.util.math.Mathd;
import de.pcfreak9000.space.world.ecs.RenderComponent;
import de.pcfreak9000.space.world.tile.Tile;

public class Chunk {
    public static final int CHUNK_TILE_SIZE = 32;
    
    public static int toGlobalChunk(int globalTile) {
        return (int) Mathd.floor(globalTile / (double) CHUNK_TILE_SIZE);
    }
    
    private final int chunkX;
    private final int chunkY;
    
    private Entity entity;
    
    private Tile[][] tiles;
    private List<Entity> statics;
    private List<Entity> dynamics;
    
    //Statics (pre-Combine?), Dynamics (moving objects; managed by WorldUpdater or an ECSSystem?)
    
    Chunk(int cx, int cy) {
        this.chunkX = cx;
        this.chunkY = cy;
        this.tiles = new Tile[CHUNK_TILE_SIZE][CHUNK_TILE_SIZE];
        this.statics = new ArrayList<>();
        this.dynamics = new ArrayList<>();
    }
    
    public int getChunkX() {
        return chunkX;
    }
    
    public int getChunkY() {
        return chunkY;
    }
    
    public void addThis(IECSManager ecs) {
        for (Entity e : statics) {
            ecs.addEntity(e);
        }
        for (Entity e : dynamics) {
            ecs.addEntity(e);
        }
        ecs.addEntity(entity);
    }
    
    public void removeThis(IECSManager ecs) {
        for (Entity e : statics) {
            ecs.removeEntity(e);
        }
        for (Entity e : dynamics) {
            ecs.removeEntity(e);
        }
        ecs.removeEntity(entity);
    }
    
    public void pack() {
        if (entity != null) {
            throw new IllegalStateException("Already packed");
        }
        entity = new Entity();
        UnorderedCachedvertexManager VERTEX_MANAGER = new UnorderedCachedvertexManager(
                6 * CHUNK_TILE_SIZE * CHUNK_TILE_SIZE);
        ReflectedBatch2D PACKING_BATCH = new ReflectedBatch2D(VERTEX_MANAGER);
        PACKING_BATCH.begin();
        Matrix3x2f tmpTransform = new Matrix3x2f();
        for (int i = 0; i < CHUNK_TILE_SIZE; i++) {
            for (int j = 0; j < CHUNK_TILE_SIZE; j++) {
                Tile t = tiles[i][j];
                PACKING_BATCH.reflectionStrength().set(t.getType().getReflectiveness());
                tmpTransform.setTranslation(i * Tile.TILE_SIZE, j * Tile.TILE_SIZE);
                PACKING_BATCH.draw(t.getType().getTexture(), tmpTransform, Tile.TILE_SIZE, Tile.TILE_SIZE, false,
                        false);
            }
        }
        PACKING_BATCH.end();
        Map<Texture, float[]> cache = VERTEX_MANAGER.getCache();
        ChunkSprite sprite = new ChunkSprite(cache);
        sprite.getTransform().localspaceWrite().setTranslation(CHUNK_TILE_SIZE * Tile.TILE_SIZE * chunkX,
                CHUNK_TILE_SIZE * Tile.TILE_SIZE * chunkY);
        entity.addComponent(new RenderComponent(sprite));
    }
    
}