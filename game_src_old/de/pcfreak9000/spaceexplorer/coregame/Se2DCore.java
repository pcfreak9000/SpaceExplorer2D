package de.pcfreak9000.spaceexplorer.coregame;

import bin.de.pcfreak9000.space.mod.Se2DModInitEvent;
import bin.de.pcfreak9000.space.mod.Se2DModPostInitEvent;
import bin.de.pcfreak9000.space.mod.Se2DModPreInitEvent;
import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.space.core.registry.GameRegistry;
import de.pcfreak9000.space.mod.Instance;
import de.pcfreak9000.space.mod.Mod;
import de.pcfreak9000.space.mod.ModLoader;
import de.pcfreak9000.spaceexplorer.coregame.start.PlanetDefinition;
import de.pcfreak9000.spaceexplorer.coregame.start.StartPlanetDef;

@Mod(id = "SpaceExplorer2D-coregame", name = "SpaceExplorer2D", resourceLocation = "", version = { 0, 0, 1 })
public class Se2DCore {

    @Instance(id = ModLoader.THIS_INSTANCE_ID)
    private static Se2DCore instance;

    @EventSubscription
    public void preInit(final Se2DModPreInitEvent pre) {
        GameRegistry.getCelestialBodyRegistry().register("startplanetdef", new StartPlanetDef()).register("planetdef",
                new PlanetDefinition());
    }

    @EventSubscription
    public void init(final Se2DModInitEvent init) {

    }

    @EventSubscription
    public void postInit(final Se2DModPostInitEvent post) {

    }

}
