package org.aquassaut.shape;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShapeListArrayAdapter extends ArrayAdapter<ShapePreview> {
	
	private int layout;
	private int firstLine;
	private int secondLine;
	private int image;

	public ShapeListArrayAdapter(Context context, List<ShapePreview> objects) {
		super(context, R.layout.shape_list_item, objects);

		this.layout = R.layout.shape_list_item;
		this.firstLine = R.id.text1;
		this.secondLine = R.id.text2;
		this.image = R.id.icon;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//return super.getView(position, convertView, parent);
		
        
        LayoutInflater li = (LayoutInflater) ((Activity) getContext()).getLayoutInflater();
        if (convertView == null) {
            convertView = li.inflate(layout, null);
        }
        
        ShapePreview sp = getItem(position);
        if (null == sp) {
        	return convertView;
        }
        
        ((TextView) convertView.findViewById(firstLine)).setText(sp.getTitle());
        ((TextView) convertView.findViewById(secondLine)).setText(super.getContext().getString(sp.downloadedStatus()));
        if (sp.getImg() != null) {
            ((ImageView) convertView.findViewById(image)).setImageBitmap(sp.getImg());
        }
 
        return convertView;
	}

}
