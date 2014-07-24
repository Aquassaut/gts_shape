package org.aquassaut.shape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


public class ShapeFetcher {
	private static String DOMAIN = "http://gts.sourceforge.net/";
	private static String PAGE = "/samples.html";

	private static String PREVIEWROOT =
			"body > center > table > tbody > tr > td:nth-child(2) > font > font > table > tbody";
	private static String PREVIEWS = "tr > td > center > a";
	private static ShapeFetchTask sft;
	
	
	private class PreviewImageLoadTask
		extends AsyncTask<ShapePreview, Void, Void> {
		int density;
		
		public PreviewImageLoadTask(int density) {
			super();
			this.density = density;
		}
		
		@Override
		protected Void doInBackground(ShapePreview... sps) {
			for (ShapePreview sp : sps) {
				URL imgurl = null;
				InputStream is = null;
				try {
					imgurl = new URL(sp.getImgsrc());
					is = imgurl.openStream();
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    sp.setImg(Bitmap.createScaledBitmap(bm, 52 * density, 52 * density, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			ShapePreviewContainerSingleton.getInstance().notifyDataSetChanged();
		}
	}
	
	private class previewLoadTask
		extends AsyncTask<Void, Void, Void> {

		private List<ShapePreview> lsp;
		private Context context;
		
		public previewLoadTask(Context c, List<ShapePreview> lsp) {
			super();
			this.context = c;
			this.lsp = lsp;
		}
		
		@Override
		protected Void doInBackground(Void...params) {
            try {
            	File dir = this.context.getExternalFilesDir(null);
                Document doc = Jsoup.connect(DOMAIN + PAGE).get();
                Element root = doc.select(PREVIEWROOT).first();
                Elements items = root.select(PREVIEWS);
                for (Element e : items) {
                    String fname = e.select("img").attr("alt");
                    String title = fname.replace(".gts.gz", "");
                    String imgsrc = e.select("img").attr("src");
                    String gtssrc = e.attr("href");
                    ShapePreview sp = new ShapePreview(title, DOMAIN + imgsrc, DOMAIN + gtssrc);
                    if (new File(dir, fname).exists()) {
                    	sp.setDownloaded(true);
                    }
                    lsp.add(sp);
                    new PreviewImageLoadTask((int) (context.getResources().getDisplayMetrics().density)).execute(sp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
		}
	}
	
	
	public List<ShapePreview> fetchPreviews(Context c) {
		List<ShapePreview> lsp = new ArrayList<ShapePreview>();
		new previewLoadTask(c, lsp).execute();
		return lsp;
	}
	
	private class GTSLoadTask extends AsyncTask<Void, Void, Void> {
		private ShapePreview sp;
		private ShapeDetailFragment f;

		public GTSLoadTask(ShapeDetailFragment f, ShapePreview sp) {
			super();
			this.sp = sp;
			this.f = f;
		}

		@Override
		protected Void doInBackground(Void... params) {
            InputStream is = null;
            FileOutputStream os = null;
            File dir;
            String fname = sp.getTitle() + ".gts.gz";
            
			try {
                URL url = new URL(sp.getGtssrc());
                HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                uc.setRequestMethod("GET");
                uc.setDoOutput(true);
                uc.connect();
                
                // Si la carte SD est là, on l'utilise
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                	dir = f.getActivity().getExternalFilesDir(null);
                //Sinon, on va dans la mémoire interne temporaire
                } else {
                	dir = f.getActivity().getCacheDir();
                }
                os = new FileOutputStream(new File(dir, fname));
                is = uc.getInputStream();
                
                byte[] buffer = new byte[1024];
                int bufflen = 0;
                while((bufflen = is.read(buffer)) > 0) {
                	os.write(buffer, 0, bufflen);
                }

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
                    os.close();
				} catch (Exception ignore) {}
				try {
                    is.close();
				} catch (Exception ignore) {}
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			sp.setDownloaded(true);
			ShapePreviewContainerSingleton.getInstance().notifyDataSetChanged();
			fetchShape(this.f, this.sp);
		}
	}
	
	public class ShapeFetchTask extends AsyncTask<ShapePreview, Void, Shape> {

		ShapeDetailFragment caller;
		GZIPInputStream is;
		
		public ShapeFetchTask(ShapeDetailFragment caller) {
			super();
			this.caller = caller;
		}
		
		@Override
		protected Shape doInBackground(ShapePreview... params) {
			Log.d("doInBackground", "début d'un fetch " + this.toString());
			ShapePreview sp = params[0];
			File dir = null, f = null;
			Shape shape = null;
            String fname = sp.getTitle() + ".gts.gz";
            if (!sp.isDownloaded()) {
            	new GTSLoadTask(caller, sp).execute();
              	this.cancel(true);
              	return null;
            }
            try {
            	dir = caller.getActivity().getExternalFilesDir(null);
            	f = new File(dir, fname);
            	if (! f.exists()) {
            		dir = caller.getActivity().getCacheDir();
            		f = new File(dir, fname);
            		assert f.exists() : "Fallback file should invariably exist";
            	}
            	is = new GZIPInputStream(new FileInputStream(f));
            	shape = new ShapeParser(this).parse(is);
            } catch (Exception e) {
            	//On détruit le fichier si il est corrompu
                Log.e("ShapeFetcher", "Exception sur le fetch d'une shape");
                f.delete();
                sp.setDownloaded(false);
                e.printStackTrace();
            } finally {
                try {
                	is.close();
                } catch (Exception ignore) {}
            }
            Log.d("Shape parsée PRE", shape.toString());
            return shape;
		}
		


		@Override
		protected void onCancelled() {
			super.onCancelled();
			Log.d("onCancelled", "Ordre de cancel arrivé");
			new Exception().printStackTrace();
			try {
                if (is != null) {
                	is.close();
                }
			} catch (Exception ignore) {}
		}

		@Override
		protected void onPostExecute(Shape result) {
			super.onPostExecute(result);
			Log.d("Shape parsée POST", "" + result);
			if (null != result) {
				caller.setShape(result);
			}
		}
	
	}
	
	public ShapeFetchTask fetchShape(ShapeDetailFragment caller, ShapePreview sp) {
		if (null != sft) {
			Log.d("fetchShape", "cancel de l'asyncTask");
			sft.cancel(true);
		} else {
            Log.d("fetchShape", "pas de cancel de l'asyncTask");
		}
		sft = new ShapeFetchTask(caller);
		sft.execute(sp);
		return sft;
	}
}
