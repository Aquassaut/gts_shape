package org.aquassaut.shape;

import java.util.ArrayList;
import java.util.List;

public class ShapeVertex {
	private float[] xyz;
	
	List<ShapeFace> adjacent;
	
	public ShapeVertex(float x, float y, float z) {
		super();
		this.xyz = new float[] {x, y, z};
		this.adjacent = new ArrayList<ShapeFace>();
	}
	
	public float getX() {
		return xyz[0];
	}
	public void setX(float x) {
		this.xyz[0] = x;
	}
	public float getY() {
		return xyz[1];
	}
	public void setY(float y) {
		this.xyz[1] = y;
	}
	public float getZ() {
		return xyz[2];
	}
	public void setZ(float z) {
		this.xyz[2] = z;
	}
	public void registerAdjacentFace(ShapeFace sf) {
		adjacent.add(sf);
	}
	public float[] getNormal() {
		float[] normal = new float[] {0, 0, 0};
		for (ShapeFace sf : this.adjacent) {
			float[] trnormal = sf.getNormal();
			normal[0] += trnormal[0];
			normal[1] += trnormal[1];
			normal[2] += trnormal[2];
		}
		return normal;
	}

	public float[] getCoords() {
		return this.xyz;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * On a besoin de ça pour tester si les vertices sont
	 * les mêmes quand on utilise les edges pour récupérer
	 * les 9 coords des triangles
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShapeVertex other = (ShapeVertex) obj;
		if (Float.floatToIntBits(xyz[0]) != Float.floatToIntBits(other.xyz[0]))
			return false;
		if (Float.floatToIntBits(xyz[1]) != Float.floatToIntBits(other.xyz[1]))
			return false;
		if (Float.floatToIntBits(xyz[2]) != Float.floatToIntBits(other.xyz[2]))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ShapeVertex [x=" + xyz[0] + ", y=" + xyz[1] + ", z=" + xyz[2] + "]";
	}
	
}
