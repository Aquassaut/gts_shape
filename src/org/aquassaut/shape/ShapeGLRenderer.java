package org.aquassaut.shape;

import static org.aquassaut.shape.ShapeMatrixUtils.cloneM;
import static org.aquassaut.shape.ShapeMatrixUtils.mulMM;
import static org.aquassaut.shape.ShapeMatrixUtils.mulMV;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class ShapeGLRenderer implements Renderer {
	
    /*
     * La matrice projection est donnée au changement de surface
     * La matrice vue est donnée dans le constructeur
     * La matrice modèle est modifiée à chaque draw
     * 
     *          /|\ Y (UP)
     *           |
     *           |
     *           |
     *           |
     *           |
     *         [ OBJ ]---------->
     *         /
     *        /
     *     [ OEIL ] (-3 en Z)
     *      /
     *    |/_ Z
     * 
     */

	private Shape shape;
	private ShapeGLHolder sglh;
	
	//On alloue tout upfront en membres, pour éviter les couteuses
	//garbage collections au milieu de l'execution
	
    private final float[] projection = new float[16];
    private final float[] vue = new float[16];
    private float[] mvp = new float[16];
    private float[] mv = new float[16];
    private float[] modele = new float[16];
    private float[] rotation = new float[16];
    private float[] invRotation = new float[16];
    private float[] newRotation = new float[16];
    private float[] transformation = new float[16];
    private float[] tmp = new float[16];
    private float[] light = new float[4];
    private float[] angles = new float[]{0f,0f,0f};
    private float[] pos = new float[] {0f, 0f};
    
    private float distance;
	
	public ShapeGLRenderer(Shape s) {
		super();
		this.shape = s;
        this.distance = 0.4f;
	}


	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.42f, 0.46f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        
        sglh = new ShapeGLHolder(this.shape);
        Matrix.setLookAtM(this.vue, 0,
        		0f, 		    0f,			   -3f,				//eye (X,Y,Z)
        	    0f, 			0f, 			0f,   			//center(X,Y,Z)
        		0f, 			1f, 			0f);  			//up(X,Y,Z) 
        Matrix.setIdentityM(this.rotation, 0);
        this.light[0] = 1.5f;
        this.light[1] = 1.5f;
        this.light[2] = -1;
        this.light[3] = 0;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
        /*
         * On a besoin d'une matrice MVP à feed au vertex shader
         * MVP = Projection (repère ortho) x View (caméra) x Model (Univers)
         */

		//On commence par effacer l'écran
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        ShapeGLRenderer.checkGlError();
        
        //On remet le modele à l'identité
        Matrix.setIdentityM(this.modele, 0);
        
        
        
        //On récupère la position de la lumière

        /*
         * On a notre MVP en combinant les 3 trucs
         */
        
        // On applique le zoom
        Matrix.scaleM(this.modele, 0, this.distance, this.distance, this.distance);
        
        /* 
         * La rotation
         * En gros, on veut combiner l'ancienne rotation avec la nouvelle,
         * mais en terme de la caméra, c'est à dire, en terme de l'orientation
         * de la forme résultant de l'ancienne rotation.
         * 
         * Pour ça, on fait R = R0^-1 * rot(R0) * R0
         * 
         * Du coup, on applique la nouvelle rotation à l'ancienne rotation,
         * on remultiplie par l'inverse de l'ancienne
         * pour avoir juste la nouvelle rotation par rapport aux valeurs
         * obtenues en ayant appliqué l'ancienne rotation. Puis on combine
         * nouvelle et ancienne pour avoir un "bilan" des rotation.
         * Ce truc m'a pris mon mercredi...
         * 
         * Pour ajouter la translation, on fait pareil mais
         * 
         * Transfo = R0^-1 * translate(rote(R0)) * R0
         * ça marche que si R est toujours celui de la formule plus haut par
         * contre. Il y a probablement plus élégant, mais j'aurai pas le temps
         * 
         */
        // R0^-1
        Matrix.invertM(this.invRotation, 0, this.rotation, 0);
        
        //rot(R0)
        this.newRotation = cloneM(this.rotation);
        Matrix.rotateM(this.newRotation, 0, this.angles[0], 1, 0, 0);
        Matrix.rotateM(this.newRotation, 0, this.angles[1], 0, 1, 0);
        Matrix.rotateM(this.newRotation, 0, this.angles[2], 0, 0, 1);
        //tr(rot(r0))
        Matrix.translateM(transformation, 0, newRotation,  0,  pos[0], pos[1], 0);
        

        //transformation = (R0^-1 * tr(rot(R0))) * R0
        mulMM(this.tmp, this.invRotation, this.transformation);
        mulMM(this.transformation, this.tmp, this.rotation);

        //rotation = (R0^-1 * rot(R0)) * R0
        mulMM(this.tmp, this.invRotation, this.newRotation);
        mulMM(this.rotation, this.tmp, this.rotation);
        
        
        //On reset les angles
        this.angles[0] = 0; this.angles[1] = 0; this.angles[2] = 0;
        
        //on ajoute les transformation au modèle
        mulMM(this.modele, this.transformation, this.modele);
        
        mulMM(this.mv, this.vue, this.modele);
        mulMM(this.mvp, this.projection, this.mv);
        
        mulMV(this.tmp, this.vue, this.light);
        
        sglh.draw(mv, mvp, tmp);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		/*
		 * On ajuste le viewport : par défaut opengl écrit dans un carré :
		 * 
		 * 		(-1, 1)	+---------------+ (1, 1)
		 * 				|				|
		 * 				|				|
		 * 				|				|
		 * 				|		x (0,0)	|
		 * 				|				|
		 * 				|				|
		 * 				|				|
		 * 	   (-1, -1)	+---------------+ (1, -1)
		 * 
		 * mais nous on a un rectangle pour écran, du coup on a besoin
		 * de faire un ajustement de façon à ce que nos formes rendent
		 * de façon normal. Pour ça, on refait la matrice projection,
		 * qui s'occupe de transformer les vertice 3d en projection 2d
		 */
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / (float)height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.orthoM(projection, 0, -ratio, ratio, -1, 1, 1, 20);
	}

    public static int loadShader(int type, String shaderCode){
    	//On doit compiler notre vertex shader et notre fragment shader
    	//pour que le programme opengl sache quoi dessiner et comment le
    	//dessiner

    	//création d'un shader de type vertex ou fragment
        int shader = GLES20.glCreateShader(type);

        //link des sources
        GLES20.glShaderSource(shader, shaderCode);
        //compilation
        GLES20.glCompileShader(shader);
        
        Log.w("Compilation shader", GLES20.glGetShaderInfoLog(shader));

        return shader;
    }
    

    public static void checkGlError() {
    	//Vérifie si il y a une erreur, et si c'est le cas, on throw
    	//une exception avec le code d'erreur en héxa, pour pouvoir se réferer
    	//facilement à https://www.opengl.org/wiki/OpenGL_Error et corriger
    	//le problème
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
        	throw new android.opengl.GLException(error);
            //throw new RuntimeException("glError 0x0" + Integer.toHexString(error));
        }
    }
    
    public float[] getAngles() {
    	return this.angles;
    }
    public void setAngles(float[] angles) {
    	this.angles = angles;
    }
    public float getDistance() {
    	return this.distance;
    }
    public void setDistance(float distance) {
    	this.distance = distance;
    }
	public void setLight(float[] light2) {
		this.light = light2;
		
	}
	public float[] getLight() {
		return this.light;
	}
	
	
	public float[] getPos() {
		return pos;
	}
	public void setPos(float[] pos) {
		this.pos = pos;
	}
	
	
	
}




