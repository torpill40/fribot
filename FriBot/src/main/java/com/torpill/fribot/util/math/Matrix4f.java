package com.torpill.fribot.util.math;

public class Matrix4f {

	private final int SIZE = 4;
	private final float[] data = new float[this.SIZE * this.SIZE];

	public Matrix4f() {

		for (int i = 0; i < this.data.length; i++) this.data[i] = 0;
	}

	public static Matrix4f identity() {

		//@formatter:off

		final Matrix4f identity = new Matrix4f()
			.set(0, 0, 1)
			.set(1, 1, 1)
			.set(2, 2, 1)
			.set(3, 3, 1);

		//@formatter:on

		return identity;
	}

	public Matrix4f set(final int m, final int n, final float val) {

		this.data[m * this.SIZE + n] = val;
		return this;
	}

	public float get(final int m, final int n) {

		return this.data[m * this.SIZE + n];
	}

	public static Matrix4f translate(final Vector3f vec3f) {

		return Matrix4f.translate(vec3f.getX(), vec3f.getY(), vec3f.getZ());
	}

	public static Matrix4f translate(final float tx, final float ty, final float tz) {

		//@formatter:off

		final Matrix4f res = Matrix4f.identity()
			.set(0, 3, tx)
			.set(1, 3, ty)
			.set(2, 3, tz);

		//@formatter:on

		return res;
	}

	public static Matrix4f rotate(final float angle, final Vector3f axis) {

		final float cos = (float) Math.cos(Math.toRadians(angle));
		final float sin = (float) Math.sin(Math.toRadians(angle));
		final float C = 1 - cos;

		//@formatter:off

		final Matrix4f res = Matrix4f.identity()
			.set(0, 0, cos + axis.getX() * axis.getX() * C)
			.set(0, 1, axis.getX() * axis.getY() * C - axis.getZ() * sin)
			.set(0, 2, axis.getX() * axis.getZ() * C + axis.getY() * sin)
			.set(1, 0, axis.getY() * axis.getX() * C + axis.getZ() * sin)
			.set(1, 1, cos + axis.getY() * axis.getY() * C)
			.set(1, 2, axis.getY() * axis.getZ() * C - axis.getX() * sin)
			.set(2, 0, axis.getZ() * axis.getX() * C - axis.getY() * sin)
			.set(2, 1, axis.getZ() * axis.getY() * C + axis.getX() * sin)
			.set(2, 2, cos + axis.getZ() * axis.getZ() * C);

		//@formatter:on

		return res;
	}

	public static Matrix4f scale(final Vector3f scalar) {

		//@formatter:off

		final Matrix4f res = Matrix4f.identity()
			.set(0, 0, scalar.getX())
			.set(1, 1, scalar.getY())
			.set(2, 2, scalar.getZ());

		//@formatter:on

		return res;
	}

	public static Matrix4f transform(final Vector3f translate, final Vector3f rotate, final Vector3f scale) {

		final Matrix4f translationMatrix = Matrix4f.translate(translate);
		final Matrix4f rotXMatrix = Matrix4f.rotate(rotate.getX(), new Vector3f(1, 0, 0));
		final Matrix4f rotYMatrix = Matrix4f.rotate(rotate.getY(), new Vector3f(0, 1, 0));
		final Matrix4f rotZMatrix = Matrix4f.rotate(rotate.getZ(), new Vector3f(0, 0, 1));
		final Matrix4f scaleMatrix = Matrix4f.scale(scale);

		final Matrix4f rotationMatrix = rotXMatrix.multiply(rotYMatrix.multiply(rotZMatrix));

		//@formatter:off

		final Matrix4f result = translationMatrix
			.multiply(rotationMatrix)
			.multiply(scaleMatrix);

		//@formatter:on

		return result;
	}

	public static Matrix4f projection(final float fov, final int width, final int height) {

		final float ar = (float) width / (float) height;
		final float tanHalfFOV = (float) Math.tan(Math.toRadians(fov / 2.0));
		final float near = 1;
		final float far = 1000;
		final float range = near - far;

		//@formatter:off

		final Matrix4f projection = new Matrix4f()
			.set(0, 0, 1 / (ar * tanHalfFOV))
			.set(1, 1, 1 / tanHalfFOV)
			.set(2, 2, (-near - far) / range)
			.set(2, 3, 2 * far * near / range)
			.set(3, 2, 1);

		//@formatter:on

		return projection;
	}

	public Vector4f multiply(final Vector4f vec4f) {

		//@formatter:off

		final Vector4f vec = new Vector4f()
			.setX(vec4f.getX() * this.get(0, 0) + vec4f.getY() * this.get(0, 1) + vec4f.getZ() * this.get(0, 2) + vec4f.getW() * this.get(0, 3))
			.setY(vec4f.getX() * this.get(1, 0) + vec4f.getY() * this.get(1, 1) + vec4f.getZ() * this.get(1, 2) + vec4f.getW() * this.get(1, 3))
			.setZ(vec4f.getX() * this.get(2, 0) + vec4f.getY() * this.get(2, 1) + vec4f.getZ() * this.get(2, 2) + vec4f.getW() * this.get(2, 3))
			.setW(vec4f.getX() * this.get(3, 0) + vec4f.getY() * this.get(3, 1) + vec4f.getZ() * this.get(3, 2) + vec4f.getW() * this.get(3, 3));

		//@formatter:on

		return vec;
	}

	public Matrix4f multiply(final Matrix4f matrix) {

		final Matrix4f res = Matrix4f.identity();

		for (int i = 0; i < this.SIZE; i++) {

			for (int j = 0; j < this.SIZE; j++) {

				res.set(i, j, this.get(i, 0) * matrix.get(0, j) + this.get(i, 1) * matrix.get(1, j) + this.get(i, 2) * matrix.get(2, j) + this.get(i, 3) * matrix.get(3, j));
			}
		}

		return res;
	}
}
