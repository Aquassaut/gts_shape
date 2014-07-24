package org.aquassaut.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class ShapeGLHolder {

	
	//4 [bytes / float] * 3 [float / vertex] * 3 [vertex / triangle] 
	private final short BUFFSIZE = 36;
	
	
    private final String vertexShaderCode =
    		//Uniformes
            "uniform mat4 uMVPMatrix;" + //MVP matrix
            "uniform mat4 uMVMatrix;" +  //sans le p

         	//Attributs
            "attribute vec4 aPosition;" + //vertex de l'objet 3d
            "attribute vec3 aNormal;" +   //Normale au vertex
         	
            //Varyings
            "varying vec4 vNormal;" +
            
            //Programme
            "void main() {" +
            "  vNormal = normalize(uMVPMatrix * vec4(aNormal, 1));" +
            "  gl_Position = uMVPMatrix * aPosition;" +
            "}";

    private final String fragmentShaderCode = 
            "precision mediump float;" +
            //Uniformes
            "uniform vec4 uColor;" + //la couleur de base
            "uniform vec3 uLightSource;" + //la position de l'éclairage
            
            //Varyings
            "varying vec4 vNormal;" +

            //programme
            "void main() {" +
            "  float intensity = abs(dot(vNormal, normalize(vec4(uLightSource, 1))));" +
            "  gl_FragColor = uColor * intensity;" +
            "}";

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private final int mProgram;
    
    private int vertexHandle;
    private int normalHandle;
    private int unifHandle;

    private Shape shape;
    private float[] edgeColor = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] faceColor = new float[] {0.5f, 0.2f, 0.1f, 1.0f};
    private boolean compile = false;
    
    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public ShapeGLHolder(Shape shape) {
    	this.shape = shape;
    	
        ByteBuffer nb = ByteBuffer.allocateDirect(BUFFSIZE);
        ByteBuffer vb = ByteBuffer.allocateDirect(BUFFSIZE);
        // use the device hardware's native byte order
        nb.order(ByteOrder.nativeOrder());
        vb.order(ByteOrder.nativeOrder());
        // create a floating point buffer from the ByteBuffer
        normalBuffer = nb.asFloatBuffer();
        vertexBuffer = vb.asFloatBuffer();
        

        // prepare shaders and OpenGL program
        int vertexShader = ShapeGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = ShapeGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
        //ShapeGLRenderer.checkGlError();
        compile = true;

    }

    public void draw(float[] mvMatrix, float[] mvpMatrix, float[] light) {
        ShapeGLRenderer.checkGlError();
    	if (!compile) { return ; }
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);
        ShapeGLRenderer.checkGlError();
        
        //on load nos uniformes qui changeront pas
        unifHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(unifHandle, 1, false, mvpMatrix, 0);
        ShapeGLRenderer.checkGlError();
        unifHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");
        GLES20.glUniformMatrix4fv(unifHandle, 1, false, mvMatrix, 0);
        ShapeGLRenderer.checkGlError();
        unifHandle = GLES20.glGetUniformLocation(mProgram, "uLightSource");
        GLES20.glUniform3fv(unifHandle, 1, light, 0);
        ShapeGLRenderer.checkGlError();
        
        //handle position et normal
        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        ShapeGLRenderer.checkGlError();
        normalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        ShapeGLRenderer.checkGlError();
        //activation de l'array openGL
        GLES20.glEnableVertexAttribArray(vertexHandle);
        ShapeGLRenderer.checkGlError();
        GLES20.glEnableVertexAttribArray(normalHandle);
        ShapeGLRenderer.checkGlError();
        //link des buffers et de l'array openGL
        GLES20.glVertexAttribPointer(vertexHandle, Shape.COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        ShapeGLRenderer.checkGlError();
        GLES20.glVertexAttribPointer(normalHandle, Shape.COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, normalBuffer);
        ShapeGLRenderer.checkGlError();
        
        //drawEdges();
        drawTriangles();
        drawEdges();
        //et on lache les arrays openGL
        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        ShapeGLRenderer.checkGlError();
    }
    
    public void drawEdges() {
        //écriture couleur
        unifHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        GLES20.glUniform4fv(unifHandle, 1, this.edgeColor, 0);
        

        for (ShapeEdge se : shape.getEdges()) {
        	for (ShapeVertex sv : se.getCoordsAsVertice()) {
        		vertexBuffer.put(sv.getCoords());
        		normalBuffer.put(sv.getNormal());
        	}
            //On met la position à 0
            vertexBuffer.position(0);
            normalBuffer.position(0);
            //on draw
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, Shape.VERTICE_PER_EDGE);
        }
    }
    
    public void drawTriangles() {
        //Ecriture couleur
        unifHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        GLES20.glUniform4fv(unifHandle, 1, this.faceColor, 0);

        
        for (ShapeFace sf : shape.getFaces()) {
        	for (ShapeVertex sv : sf.getCoordsAsVertice()) {
        		vertexBuffer.put(sv.getCoords());
        		//normalBuffer.put(sv.getNormal());
        		normalBuffer.put(sf.getNormal());
        	}
        	normalBuffer.position(0);
        	vertexBuffer.position(0);
            //dessine les faces
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, Shape.VERTICE_PER_FACE);
        }
	}
}












