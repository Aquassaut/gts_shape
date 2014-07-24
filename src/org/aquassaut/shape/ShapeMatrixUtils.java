package org.aquassaut.shape;

import android.opengl.Matrix;

public class ShapeMatrixUtils {

    //Utility pour cloner une matrice
    public static float[] cloneM(float[] m) {

    	float[] res = new float[m.length];
    	for (int i = 0; i < m.length; i += 1) {
    		res[i] = m[i]; 
    	}
    	return res;
    }
    
    public static void copyM(float[] res, float[] m) {
    	for (int i = 0; i < m.length; i += 1) {
    		res[i] = m[i]; 
    	}
    }
    
    //Utility pour multiplier 2 matrices plus facilement, sans spécifier
    //l'offset
    public static void mulMM(float[] res, float[] lhs, float[] rhs) {
    	float[] safelhs = (res == lhs ? cloneM(lhs) : lhs);
    	float[] saferhs = (res == rhs ? cloneM(rhs) : rhs);
    	
    	Matrix.multiplyMM(res, 0, safelhs, 0, saferhs, 0);
    }
    
    public static void mulMV(float[] res, float[] mat, float[] vec) {
    	float[] safemat = (res == mat ? cloneM(mat) : mat);
    	float[] safevec = (res == vec ? cloneM(vec) : vec);
        Matrix.multiplyMV(res, 0, safemat, 0, safevec, 0);
    }
    
    //Utility pour trouver le cross product de deux vecteurs
    public static void crossVV(float[] res, float[] lhs, float[] rhs) {
    	float[] safelhs = (res == lhs ? cloneM(lhs) : lhs);
    	float[] saferhs = (res == rhs ? cloneM(rhs) : rhs);
    	res[0] = (safelhs[1] * saferhs[2]) - (safelhs[2] * rhs[1]);
    	res[1] = (safelhs[2] * saferhs[0]) - (safelhs[0] * rhs[2]);
    	res[2] = (safelhs[0] * saferhs[1]) - (safelhs[1] * rhs[2]);
    }

    //Utility pour trouver le dot product de deux vecteurs
    public static float dotVV(float[] lhs, float[] rhs) {
    	return lhs[0] * rhs[0] + lhs[1] * rhs[1] + lhs[2] * rhs[2];
    }
    
    public static void subVV(float[] res, float[] lhs, float[] rhs) {
    	float[] safelhs = (res == lhs ? cloneM(lhs) : lhs);
    	float[] saferhs = (res == rhs ? cloneM(rhs) : rhs);
    	for (int i = 0; i < lhs.length; i += 1) {
    		res[i] = safelhs[i] - saferhs[i];
    	}
    }
    
}
