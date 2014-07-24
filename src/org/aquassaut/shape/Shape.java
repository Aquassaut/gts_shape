package org.aquassaut.shape;

import java.util.List;

public class Shape {
	
	public static final int COORDS_PER_VERTEX = 3;
	public static final int VERTICE_PER_EDGE = 2;
	public static final int VERTICE_PER_FACE = 3;
	public static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;
	

	private List<ShapeVertex> vertice;
	private List<ShapeEdge> edges;
	private List<ShapeFace> faces;

	public Shape(List<ShapeVertex> vertices, List<ShapeEdge> edges,
			List<ShapeFace> faces) {
		this.vertice = vertices;
		this.edges = edges;
		this.faces = faces;
	}
	
	public List<ShapeVertex> getVertice() {
		return this.vertice;
	}
	
	public List<ShapeFace> getFaces() {
		return this.faces;
	}
	public List<ShapeEdge> getEdges() {
		return this.edges;
	}
}
