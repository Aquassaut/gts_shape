package org.aquassaut.shape;

import java.util.Collections;
import java.util.List;

import android.content.Context;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ShapePreviewContainerSingleton {

	/**
	 * An array of sample (dummy) items.
	 */
	private List<ShapePreview> items;
	private ShapeListArrayAdapter ad;
	private Context c;
	
	private ShapePreviewContainerSingleton() {}
	
	public void updatePreviews() {
        ShapeFetcher sf = new ShapeFetcher();
        items = sf.fetchPreviews(c);
        Collections.sort(items);
	}
	
	public List<ShapePreview> getItems() {
		if (null == this.items) {
			updatePreviews();
		}
		return this.items;
	}
	public void setListAdapter(ShapeListArrayAdapter ad) {
		this.ad = ad;
	}
	public void setContext(Context c) {
		this.c = c;
	}
	
	public void notifyDataSetChanged() {
		ad.notifyDataSetChanged();
	}
	
	//Static part
	
	private static ShapePreviewContainerSingleton spct;
	
	public static ShapePreviewContainerSingleton getInstance() {
		if (spct == null) {
			spct = new ShapePreviewContainerSingleton();
		}
		return spct;
	}
}
