package org.aquassaut.shape;

public class ShapeEdge {
	private ShapeVertex[] v1v2;
	
	public ShapeEdge(ShapeVertex a, ShapeVertex b) {
		this.v1v2 = new ShapeVertex[] {a, b};
	}

	/**
	 * @return the v1
	 */
	public ShapeVertex getV1() {
		return this.v1v2[0];
	}

	/**
	 * @param v1 the v1 to set
	 */
	public void setV1(ShapeVertex v1) {
		this.v1v2[0] = v1;
	}

	/**
	 * @return the v2
	 */
	public ShapeVertex getV2() {
		return this.v1v2[1];
	}

	/**
	 * @param v2 the v2 to set
	 */
	public void setV2(ShapeVertex v2) {
		this.v1v2[1] = v2;
	}
	
	public float[] getCoords() {
		return new float[] {
			this.v1v2[0].getX(), this.v1v2[0].getY(), this.v1v2[0].getZ(),
			this.v1v2[1].getX(), this.v1v2[1].getY(), this.v1v2[1].getZ()
		};
	}
	public ShapeVertex[] getCoordsAsVertice() {
		return this.v1v2;
		
	}
	
}
