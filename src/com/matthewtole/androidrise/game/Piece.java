package com.matthewtole.androidrise.game;

import android.graphics.Canvas;

import com.matthewtole.androidrise.lib.GridLocation;
import com.matthewtole.androidrise.lib.ScreenLocation;

public class Piece {

	protected enum PieceState {
		DEFAULT, MOVING, ANIMATING
	}

	@SuppressWarnings("unused")
	private static final String TAG = Piece.class.getSimpleName();

	protected SpriteManager sprites;
	protected ScreenLocation location;

	protected String bitmap = "";
	protected PieceState state = PieceState.DEFAULT;

	private ScreenLocation target;
	private int MOVE_SPEED = 3;

	public Piece(SpriteManager sprites) {
		this.sprites = sprites;
	}

	public void setLocation(GridLocation loc) {
		this.setLocation(loc, true);
	}

	public void setLocation(ScreenLocation loc) {
		this.setLocation(loc, true);
	}

	public void setLocation(GridLocation loc, boolean instant) {
		if (instant) {
			this.location = new ScreenLocation(loc);
		} else {
			this.target = new ScreenLocation(loc);
			this.state = PieceState.MOVING;
		}
	}

	public void setLocation(ScreenLocation loc, boolean instant) {
		if (instant) {
			this.location = new ScreenLocation(loc);
		} else {
			this.target = loc;
			this.state = PieceState.MOVING;
		}
	}

	public void draw(Canvas canvas) {
		if (bitmap.length() > 0) {
			canvas.drawBitmap(this.sprites.getBitmap(this.bitmap),
					this.location.getScreenX(), this.location.getScreenY(),
					null);
		}
	}

	public void update() {
		switch (this.state) {
		case MOVING:
			this.move();
			break;
		}
	}

	private void move() {

		int x = this.location.getScreenX();
		int y = this.location.getScreenY();

		if (x < this.target.getScreenX()) {
			if (Math.abs(x - this.target.getScreenX()) < this.MOVE_SPEED) {
				x = this.target.getScreenX();
			} else {
				x += this.MOVE_SPEED;
			}
		} else if (x > this.target.getScreenX()) {
			if (Math.abs(x - this.target.getScreenX()) < this.MOVE_SPEED) {
				x = this.target.getScreenX();
			} else {
				x -= this.MOVE_SPEED;
			}
		}

		if (y < this.target.getScreenY()) {
			if (Math.abs(y - this.target.getScreenY()) < this.MOVE_SPEED) {
				y = this.target.getScreenY();
			} else {
				y += this.MOVE_SPEED;
			}
		} else if (y > this.target.getScreenY()) {
			if (Math.abs(y - this.target.getScreenY()) < this.MOVE_SPEED) {
				y = this.target.getScreenY();
			} else {
				y -= this.MOVE_SPEED;
			}
		}

		this.location = new ScreenLocation(x, y);
	}
}
