package de.pcfreak9000.se2d.game;

import de.codemakers.io.file.AdvancedFile;
import de.pcfreak9000.se2d.mod.EventTest;
import de.pcfreak9000.se2d.mod.ModManager;
import de.pcfreak9000.se2d.universe.Universe;
import omnikryptec.event.eventV2.EventSubscription;
import omnikryptec.event.eventV2.EventSystem;
import omnikryptec.event.eventV2.engineevents.FrameEvent;
import omnikryptec.event.eventV2.engineevents.FrameEvent.FrameType;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.resource.loader.ResourceLoader;
import omnikryptec.resource.texture.Texture;

public class SpaceExplorer2D  {

	private static final AdvancedFile RESOURCELOCATION = new AdvancedFile(true, "", "de", "pcfreak9000", "se2d", "res");
	private static final float[] PLANETPROJ = { -1920 / 2, -1080 / 2, 1920, 1080 };
	// private static final float[] PLANETPROJ = { -19200 / 2, -10800 / 2, 19200,
	// 10800 };

	private static SpaceExplorer2D instance;

	public static SpaceExplorer2D getSpaceExplorer2D() {
		return instance;
	}

	private AdvancedFile resourcepacks, modsfolder;
	private ModManager manager;
	private Universe currentWorld = null;

	public SpaceExplorer2D(AdvancedFile resourcepacks, AdvancedFile modsfolder) {
		if (instance != null) {
			throw new IllegalStateException("SpaceExplorer2D is already created!");
		}
		instance = this;
		this.resourcepacks = resourcepacks;
		if (!resourcepacks.toFile().exists()) {
			resourcepacks.setShouldBeFile(false);
			resourcepacks.toFile().mkdirs();
		}
		this.modsfolder = modsfolder;
		if (!modsfolder.toFile().exists()) {
			modsfolder.setShouldBeFile(false);
			modsfolder.toFile().mkdirs();
		}
		manager = new ModManager();
		manager.load(modsfolder);
		ResourceLoader.createInstanceDefault(true, false);
		loadRes();
		currentWorld = new Universe();
		currentWorld.loadWorld();
		EventSystem.registerEventHandler(this);
		new EventTest().call();
		OmniKryptecEngine.instance().startLoop();
	}

	private void loadRes() {
		ResourceLoader.currentInstance().clearStagedAdvancedFiles();
		ResourceLoader.currentInstance().stageAdvancedFiles(0, 0, resourcepacks);
		ResourceLoader.currentInstance().stageAdvancedFiles(1, ResourceLoader.LOAD_XML_INFO, RESOURCELOCATION);
		ResourceLoader.currentInstance().loadStagedAdvancedFiles(true);
		ResourceLoader.currentInstance().actions(Texture.class, (t) -> t.invertV());
	}

	public float[] getProjectionData() {
		return PLANETPROJ;
	}

	public Universe getUniverse() {
		return currentWorld;
	}

	@EventSubscription
	public void onEvent(FrameEvent ev) {
		if (ev.getType() == FrameType.PRE && currentWorld != null) {
			currentWorld.update();
		}
	}

}
