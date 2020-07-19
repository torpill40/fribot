package com.torpill.fribot.util.math;

public class Vector4f {

	private float x, y, z, w;

	public Vector4f() {

		this(0, 0, 0, 0);
	}

	public Vector4f(final float x, final float y, final float z, final float w) {

		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f(final Vector4f vec4f) {

		this(vec4f.getX(), vec4f.getY(), vec4f.getZ(), vec4f.getW());
	}

	public float getX() {

		return this.x;
	}

	public Vector4f setX(final float x) {

		this.x = x;
		return this;
	}

	public float getY() {

		return this.y;
	}

	public Vector4f setY(final float y) {

		this.y = y;
		return this;
	}

	public float getZ() {

		return this.z;
	}

	public Vector4f setZ(final float z) {

		this.z = z;
		return this;
	}

	public float getW() {

		return this.w;
	}

	public Vector4f setW(final float w) {

		this.w = w;
		return this;
	}

	public Vector4f scale(final float scale) {

		//@formatter:off

		final Vector4f vec = this
			.setX(this.getX() * scale)
			.setY(this.getY() * scale)
			.setZ(this.getZ() * scale)
			.setW(this.getW() * scale);

		//@formatter:on

		return vec;
	}

	public Vector2f toDeviceCoordinatesFromNDC(final int width, final int height) {

		final float x = width * (1F + this.getX()) / 2F;
		final float y = height * (1F - this.getY()) / 2F;
		return new Vector2f(x, y);
	}

	@Override
	public String toString() {

		return "[x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", w=" + this.w + "]";
	}
}
