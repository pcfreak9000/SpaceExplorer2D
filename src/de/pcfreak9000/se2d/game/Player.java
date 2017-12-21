package de.pcfreak9000.se2d.game;

import de.pcfreak9000.se2d.main.KeyManager;
import omnikryptec.gameobject.Sprite;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.resource.loader.ResourceLoader;
import omnikryptec.settings.KeySettings;
import omnikryptec.util.EnumCollection.UpdateType;
import omnikryptec.util.Instance;
import omnikryptec.util.SmoothFloat;

public class Player extends Sprite {

	private static final float DY_SPEED = 200;
	private static final float DX_SPEED = 200;

	private KeySettings keysettings;

	public Player() {
		this.setUpdateType(UpdateType.DYNAMIC);
		this.setGlobal(true);
		this.keysettings = OmniKryptecEngine.instance().getGameSettings().getKeySettings();
		setTexture(ResourceLoader.currentInstance().getTexture("mensch.png"));
		getTransform().setScale(2f);
		setLayer(1);
	}

	private SmoothFloat dx = new SmoothFloat(0, 10);
	private SmoothFloat dy = new SmoothFloat(0, 10);

	@Override
	protected void update() {
		if (keysettings.isPressed(KeyManager.KEY_PLAYER_MOVE_FORWARD)) {
			dy.setTarget(DY_SPEED*Instance.getDeltaTimeSf());
		} else if (keysettings.isPressed(KeyManager.KEY_PLAYER_MOVE_BACKWARD)) {
			dy.setTarget(-DY_SPEED*Instance.getDeltaTimeSf());
		} else {
			dy.setTarget(0);
		}
		if (keysettings.isPressed(KeyManager.KEY_PLAYER_MOVE_LEFT)) {
			dx.setTarget(-DX_SPEED*Instance.getDeltaTimeSf());
		} else if (keysettings.isPressed(KeyManager.KEY_PLAYER_MOVE_RIGHT)) {
			dx.setTarget(DX_SPEED*Instance.getDeltaTimeSf());
		} else {
			dx.setTarget(0);
		}
		dx.update(Instance.getDeltaTimeSf());
		dy.update(Instance.getDeltaTimeSf());
		getTransform().increasePosition(dx.get(), dy.get());
		SpaceExplorer2D.getSpaceExplorer2D().getPlanetCamera().getTransform().setPosition(
				getTransform().getPosition(true).x + getWidth() / 2,
				getTransform().getPosition(true).y + getHeight() / 2, 0);
	}
}
