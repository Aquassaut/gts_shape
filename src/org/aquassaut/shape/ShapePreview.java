package org.aquassaut.shape;

import android.graphics.Bitmap;

public class ShapePreview implements Comparable<ShapePreview> {

	
	private String title;
	
	/**
	 * C'est l'image de preview, ça pourra être utile si j'ai du temps en trop
	 * et le courage pour implementer mon propre ArrayAdapter pour permettre 
	 * l'affichage de l'imageview, et la logique qui permet de loader ça
	 * de façon asynchrone. Avant d'écrire ce commentaire, j'y croyais.
	 * Maintenant, un peu moins.
	 */
	
	
	private String imgsrc;
	private Bitmap img;
	
	private String gtssrc;
	private boolean downloaded;
	
	
	public ShapePreview(String title, String imgsrc, String gtssrc) {
		this.title = title;
		this.imgsrc = imgsrc;
		this.gtssrc = gtssrc;
		this.downloaded = false;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getImgsrc() {
		return imgsrc;
	}
	public void setImgsrc(String imgsrc) {
		this.imgsrc = imgsrc;
	}
	
	public String getGtssrc() {
		return gtssrc;
	}
	public void setGtssrc(String gtssrc) {
		this.gtssrc = gtssrc;
	}
	
	public Bitmap getImg() {
		return img;
	}
	public void setImg(Bitmap img) {
		this.img = img;
	}
	
	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}
	public boolean isDownloaded() {
		return this.downloaded;
	}
	
	public int downloadedStatus() {
		return this.downloaded ? R.string.available : R.string.unavailable;
	}

	@Override
	public int compareTo(ShapePreview sp) {
		if (null == sp) throw new NullPointerException();
		
		if (this.downloaded == sp.downloaded) {
			return this.title.compareTo(sp.title);
		} else if (this.downloaded) {
			return -1;
		}
		return 1;
	}
}