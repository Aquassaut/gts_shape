package org.aquassaut.shape;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ZoomControls;

public class ShapeGLSurfaceView extends GLSurfaceView {


    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TOUCH_MOVE_FACTOR = 1.0f / 320;
    private final float TOUCH_ZOOM_FACTOR = 1.0f / 320;

    private final ShapeGLRenderer renderer;

    private float primX = 0;
    private float primY = 0;
    
    private ScaleGestureDetector sgd;
    private GestureDetector gd;
    
    private boolean translation = false;
    private boolean deuxDoigts = false;
    
    
    

    /* ça fait des warnings en moins */
	public ShapeGLSurfaceView(Context context, AttributeSet attrs) {
		this(context, null, null);
	}
	public ShapeGLSurfaceView(Context context) {
		this(context, null, null);
	}


	public ShapeGLSurfaceView(Context context, Shape s, final ZoomControls zc) {
        super(context);

        this.renderer = new ShapeGLRenderer(s);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        setRenderer(this.renderer);
        //On draw que quand on en a besoin ou qu'on le demande expressément
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        this.sgd = new ScaleGestureDetector(context, new ShapeScaleGestureListener());
        this.gd = new GestureDetector(context, new ShapeGestureListener());
        gd.setIsLongpressEnabled(true);
        
        OnClickListener zoomInListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				float newDistance = renderer.getDistance() + 3 * TOUCH_ZOOM_FACTOR;
                renderer.setDistance(newDistance);
			}
        };
        OnClickListener zoomOutListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				float newDistance = renderer.getDistance() - 3 * TOUCH_ZOOM_FACTOR;
                renderer.setDistance(newDistance < 0 ? 0 : newDistance);
			}
        };
        
        zc.setOnZoomInClickListener(zoomInListener);
        zc.setOnZoomOutClickListener(zoomOutListener);

    }
	

    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	
    	//On se remet en mode rotation
    	if (e.getAction() == MotionEvent.ACTION_UP) {
    		this.translation = false;
    	}

    	//ensuite on veut voir si c'est un scale, ie un zoom
        this.sgd.onTouchEvent(e);
        
        /*
         * on évite de passer en mode translation si on a deux doigts sur
         * l'écran. Ca marche pas bien sans, je suis pas trop sur du pourquoi
         * du comment.
         */
        if (e.getPointerCount() > 0) {
        	deuxDoigts = true;
        }
        if (deuxDoigts && e.getPointerCount() == 1) {
        	deuxDoigts = false;
        }
        
        if (!sgd.isInProgress()) {
        	
        	//On regarde si c'est un long click
        	this.gd.onTouchEvent(e);
        	//Et ensuite on revoie la rotation ou la translation, selon le cas
        	if (this.translation) {
        		onTranslate(e);
        	} else {
        		onRotate(e);
        	}
        }
        requestRender();
        return true;
    }
    
    private boolean onTranslate(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

		if (e.getAction() == MotionEvent.ACTION_MOVE) {
			float dx = - x + this.primX;
			float dy = - y + this.primY;
			
			float[] pos = this.renderer.getPos();
			pos[0] += (dx * TOUCH_MOVE_FACTOR);
			pos[1] += (dy * TOUCH_MOVE_FACTOR);
			this.renderer.setPos(pos);
        }
        this.primX = x;
        this.primY = y;
        return true;
	}


	public boolean onRotate(MotionEvent e) {
		
        float x = e.getX();
        float y = e.getY();

		if (e.getAction() == MotionEvent.ACTION_MOVE) {
			//Rotation à un doigt
			float dx = x - this.primX;
			// attention : origine android = en haut à droite
			float dy = - y + this.primY;
			
			float[] angles = this.renderer.getAngles();
			//Mouvement en X : rotation autour de l'axe Y
			angles[1] += (dx * TOUCH_SCALE_FACTOR);
			//Mouvement en Y : rotation autour de l'axe X
			angles[0] += (dy * TOUCH_SCALE_FACTOR);
			this.renderer.setAngles(angles);
        }
        this.primX = x;
        this.primY = y;
        return true;
    }
    
    private class ShapeGestureListener
    	extends GestureDetector.SimpleOnGestureListener {
		@Override
		public void onLongPress(MotionEvent e) {
			if (!deuxDoigts) {
				translation = true;
				performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
			}
    
    
    
		}
    }
    
	private class ShapeScaleGestureListener 
		extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			// On zoom
			float dz = detector.getPreviousSpan() - detector.getCurrentSpan();
			float newDistance = renderer.getDistance() - dz * TOUCH_ZOOM_FACTOR;
			renderer.setDistance(newDistance < 0 ? 0 : newDistance);
		    return true;
		}
	}
}
