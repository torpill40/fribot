package com.torpill.fribot.util.math;

public class Vector3f {

	private float x, y, z;

	public Vector3f(final float x, final float y, final float z) {

		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float getX() {

		return this.x;
	}

	public void setX(final float x) {

		this.x = x;
	}

	public float getY() {

		return this.y;
	}

	public void setY(final float y) {

		this.y = y;
	}

	public float getZ() {

		return this.z;
	}

	public void setZ(final float z) {

		this.z = z;
	}

	public Vector3f oppose() {

		this.setX(-this.getX());
		this.setY(-this.getY());
		this.setZ(-this.getZ());
		return this;
	}

	public Vector3f invert() {

		this.setX(1F / this.getX());
		this.setY(1F / this.getY());
		this.setZ(1F / this.getZ());
		return this;
	}
}
