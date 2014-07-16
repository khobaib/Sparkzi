package com.sparkzi.model;

import android.graphics.drawable.Drawable;

public class DrawerItem {

	public String name;
	public Drawable image;

	public DrawerItem() {
	}

	public DrawerItem(String name, Drawable image) {
		this.name = name;
		this.image = image;

	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
