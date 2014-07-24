package org.aquassaut.shape;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

/**
 * A fragment representing a single Shape detail screen. This fragment is either
 * contained in a {@link ShapeListActivity} in two-pane mode (on tablets) or a
 * {@link ShapeDetailActivity} on handsets.
 */
public class ShapeDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private ShapePreview mItem;
	private Shape shape;
	private ProgressBar pb;
	private ShapeGLSurfaceView surface;
    private ZoomControls zc;
	
	
	
	private View rootView;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ShapeDetailFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			mItem = ShapePreviewContainerSingleton.getInstance().getItems().get(
					getArguments().getInt(ARG_ITEM_ID));
			new ShapeFetcher().fetchShape(this, mItem);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_shape_detail,
				container, false);
		
		pb = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		
		this.rootView = rootView;
		
		
        this.zc = (ZoomControls) rootView.findViewById(R.id.zoomControls1);
        //surface.setEGLContextClientVersion(2);
        
		
		if (this.shape != null) {
			reloadSurface();
		}
		return rootView;
	}
	
	/*
	 * Il y a une tonne de nullPointerExceptions possibles. Il faut que la pb
	 * soit : 
	 * 		Visible si on a pas de shape
	 * 		Invisible si on a une shape
	 * sachant que la pb peut être null si on a ajouté la shape avant d'inflate
	 * la vue, ou que shape peut être null à tout moment (ou être mauvais)
	 */
	public void handleProgressBar() {
		if (null != pb) {
			if (null != shape) {
                pb.setVisibility(View.INVISIBLE);
			} else {
				pb.setVisibility(View.VISIBLE);
			} 
		}
	}
	
	public void reloadSurface() {
		handleProgressBar();
		if (null == this.shape) {
            throw new RuntimeException("Ma shape est null, je sais pas pourquoi");
		} else if (rootView != null) {
			this.surface = new ShapeGLSurfaceView(getActivity(), this.shape, zc);
            ((RelativeLayout) rootView.findViewById(R.id.layoutForGlSurface))
            	.addView(this.surface, 0, new RelativeLayout.LayoutParams(
            			RelativeLayout.LayoutParams.MATCH_PARENT,
            			RelativeLayout.LayoutParams.MATCH_PARENT));
		}
	}
	
	public void setShape(Shape s) {
		this.shape = s;
		reloadSurface();
	}

}
