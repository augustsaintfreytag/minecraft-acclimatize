package net.saint.acclimatize.library;

public final class RGBAColor {
	// Properties

	public final float r;
	public final float g;
	public final float b;
	public final float a;

	// Init

	public RGBAColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public static final RGBAColor black() {
		return new RGBAColor(0f, 0f, 0f, 1f);
	}

	public static final RGBAColor white() {
		return new RGBAColor(1f, 1f, 1f, 1f);
	}

	// Mutation

	public RGBAColor withAlpha(float alpha) {
		return new RGBAColor(this.r, this.g, this.b, alpha);
	}

	public RGBAColor transparent() {
		return new RGBAColor(this.r, this.g, this.b, 0);
	}

	public RGBAColor opaque() {
		return new RGBAColor(this.r, this.g, this.b, 1);
	}

	public RGBAColor copy() {
		return new RGBAColor(this.r, this.g, this.b, this.a);
	}
}
