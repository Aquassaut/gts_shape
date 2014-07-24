package org.aquassaut.shape;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;
import android.os.Bundle;

/**
 * An activity representing a list of Shapes. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ShapeDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ShapeListFragment} and the item details (if present) is a
 * {@link ShapeDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ShapeListFragment.Callbacks} interface to listen for item selections.
 */
public class ShapeListActivity extends SherlockFragmentActivity implements
		ShapeListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shape_list);

		if (findViewById(R.id.shape_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ShapeListFragment) getSupportFragmentManager().findFragmentById(
					R.id.shape_list)).setActivateOnItemClick(true);
		}
	}

	/**
	 * Callback method from {@link ShapeListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(ShapeDetailFragment.ARG_ITEM_ID, id);
			ShapeDetailFragment fragment = new ShapeDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.shape_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ShapeDetailActivity.class);
			detailIntent.putExtra(ShapeDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

	
	
}
