package de.pcfreak9000.se2d.universe;

import de.pcfreak9000.se2d.universe.celestial.CelestialBody;

public class Orbit {
	
	private SpaceCoordinates mycoords;
	private CelestialBody parent;
	
	public SpaceCoordinates getCoordinates() {
		return mycoords;
	}
	
	public CelestialBody getParent() {
		return parent;
	}
	
}
