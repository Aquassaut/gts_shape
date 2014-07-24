package org.aquassaut.shape;


import static org.aquassaut.shape.ShapeMatrixUtils.*;

public class ShapeFace {
	
	
	private ShapeEdge e1;
	private ShapeEdge e2;
	private ShapeEdge e3;
	
	
	
	public ShapeFace(ShapeEdge e1, ShapeEdge e2, ShapeEdge e3) {
		this.e1 = e1;
		this.e2 = e2;
		this.e3 = e3; 
	}

	public ShapeVertex[] getCoordsAsVertice() {
		ShapeVertex[] coords = new ShapeVertex[3];
		int i = 0;
		coords[i++] = e1.getV1();
		coords[i++] = e1.getV2();
		coords[i++] = (e2.getV1().equals(e1.getV1()) ||
					   e2.getV1().equals(e1.getV2()))? e2.getV2(): e2.getV1();
		return coords;
	}
	
	public float[] getNormal() {
		ShapeVertex[] coords = this.getCoordsAsVertice();
		float[] normal = new float[3];
		float[] v1 = new float[3];
		float[] v2 = new float[3];
        subVV(v1, coords[0].getCoords(), coords[1].getCoords());
        subVV(v2, coords[2].getCoords(), coords[1].getCoords());
		crossVV( normal, v1, v2);
		return normal;
	}
	

	/**
	 * @return the e1
	 */
	public ShapeEdge getE1() {
		return e1;
	}

	/**
	 * @param e1 the e1 to set
	 */
	public void setE1(ShapeEdge e1) {
		this.e1 = e1;
	}

	/**
	 * @return the e2
	 */
	public ShapeEdge getE2() {
		return e2;
	}

	/**
	 * @param e2 the e2 to set
	 */
	public void setE2(ShapeEdge e2) {
		this.e2 = e2;
	}

	/**
	 * @return the e3
	 */
	public ShapeEdge getE3() {
		return e3;
	}

	/**
	 * @param e3 the e3 to set
	 */
	public void setE3(ShapeEdge e3) {
		this.e3 = e3;
	}
	
	
}



