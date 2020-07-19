package com.torpill.fribot.util.math;

public class Vector2f {

	private float x, y;

	public Vector2f(final float x, final float y) {

		this.x = x;
		this.y = y;
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

	public Vector2f toNDCFromDeviceCoordinates(final int width, final int height) {

		final float x = -(width - 2F * this.getX()) / height;
		final float y = 1F - 2F * this.getY() / height;

		return new Vector2f(x, y);
	}

	public Vector2f toDeviceCoordinatesFromNDC(final int width, final int height) {

		final float x = width * (1F + this.getX()) / 2F;
		final float y = height * (1F - this.getY()) / 2F;
		return new Vector2f(x, y);
	}

	@Override
	public String toString() {

		return "[x=" + this.x + ", y=" + this.y + "]";
	}
}
