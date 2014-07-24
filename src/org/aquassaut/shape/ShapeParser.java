package org.aquassaut.shape;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.aquassaut.shape.ShapeFetcher.ShapeFetchTask;

public class ShapeParser {
	
	private ShapeFetchTask caller;
	
	public ShapeParser(ShapeFetchTask shapeFetchTask) {
		caller = shapeFetchTask;
	}

	public Shape parse(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		String[] temp;
		
		int[] lengths = null;
		List<ShapeVertex> vertices = new ArrayList<ShapeVertex>();
		List<ShapeEdge> edges = new ArrayList<ShapeEdge>();
		List<ShapeFace> faces = new ArrayList<ShapeFace>();
		
		int mode = 0;
		
		try {
			while ( null != (line = br.readLine()) ) {
				if (caller.isCancelled()) {
					//Si on a cancel la tâche de fetch, on retourne le plus vite possible !
					return null;
				}
				temp = line.split(" ");
				if (0 == mode) {
					lengths = new int[]{
						Integer.valueOf(temp[0]),
						Integer.valueOf(temp[1]),
						Integer.valueOf(temp[2])
					};
					mode += 1;
				} else if (mode == 1) {
					vertices.add(new ShapeVertex(
							Float.valueOf(temp[0]),
							Float.valueOf(temp[1]),
							Float.valueOf(temp[2])));	
					if (vertices.size() == lengths[0]) mode += 1;
				} else if (mode == 2) {
					edges.add(new ShapeEdge(
							vertices.get(Integer.valueOf(temp[0]) - 1),
							vertices.get(Integer.valueOf(temp[1]) - 1)));
					if (edges.size() == lengths[1]) mode += 1;
				} else if (mode == 3) {
					faces.add(new ShapeFace(
							edges.get(Integer.valueOf(temp[0]) - 1),
							edges.get(Integer.valueOf(temp[1]) - 1),
							edges.get(Integer.valueOf(temp[2]) - 1)));
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				br.close();
			} catch(Exception ignore) {}
		}
		return new Shape(vertices, edges, faces);
	}
}
