package de.m_marvin.industria.core.physics.types;

import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3d;

public class ContraptionPosition {
	
	public Quaternion orientation;
	public Vec3d position;
	
	public ContraptionPosition(Quaternion orientation, Vec3d position) {
		this.orientation = orientation;
		this.position = position;
	}
	
	public Quaternion getOrientation() {
		return orientation;
	}
	
	public void setOrientation(Quaternion orientation) {
		this.orientation = orientation;
	}
	
	public Vec3d getPosition() {
		return position;
	}
	
	public void setPosition(Vec3d position) {
		this.position = position;
	}
	
}
