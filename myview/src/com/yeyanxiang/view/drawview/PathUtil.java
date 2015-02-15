package com.yeyanxiang.view.drawview;

public class PathUtil {

	private int color, size, downX, downY, movex, movey, upx, upy;

	public PathUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PathUtil(int movex, int movey) {
		super();
		this.movex = movex;
		this.movey = movey;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getDownX() {
		return downX;
	}

	public void setDownX(int downX) {
		this.downX = downX;
	}

	public int getDownY() {
		return downY;
	}

	public void setDownY(int downY) {
		this.downY = downY;
	}

	public int getMovex() {
		return movex;
	}

	public void setMovex(int movex) {
		this.movex = movex;
	}

	public int getMovey() {
		return movey;
	}

	public void setMovey(int movey) {
		this.movey = movey;
	}

	public int getUpx() {
		return upx;
	}

	public void setUpx(int upx) {
		this.upx = upx;
	}

	public int getUpy() {
		return upy;
	}

	public void setUpy(int upy) {
		this.upy = upy;
	}

}
