package com.matthewtole.androidrise;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

import com.matthewtole.androidrise.lib.GridRef;
import com.matthewtole.androidrise.lib.RiseGame;

public class MainView extends View {

	private RiseGame game;

	private HashMap<String, Bitmap> bitmaps;
	private HashMap<String, Paint> paints;

	private final static int SIZE_MIN = 0;
	private final static int SIZE_MAX = 60;
	private final static int DRAG_START_AMOUNT = 10;

	private boolean dragging = false;
	private float dragStartX = 0;
	private float dragStartY = 0;

	private int drawOffsetX = 0;
	private int drawOffsetY = 0;

	private int tileHighlightedX = 0;
	private int tileHighlightedY = 0;

	private int canvasWidth = 0;
	private int canvasHeight = 0;
	private int sidebarWidth = 0;
	private int tileHeight = 0;
	private int tileWidth = 0;

	public MainView(Context context) {
		super(context);

		this.game = new RiseGame();
		this.game.setup();

		this.bitmaps = new HashMap<String, Bitmap>();
		this.addBitmap("tile", R.drawable.tile);
		tileHeight = this.bitmaps.get("tile").getHeight() + 4;
		tileWidth = this.bitmaps.get("tile").getWidth() - 10;

		this.addBitmap("highlight", R.drawable.highlight);
		this.addBitmap("background", R.drawable.background);
		this.addBitmap("blue_worker", R.drawable.blue_worker);
		this.addBitmap("red_worker", R.drawable.red_worker);

		this.paints = new HashMap<String, Paint>();
		this.makePaints();
	}

	private void makePaints() {
		this.paints.put("sidebarBackground", new Paint());
		this.paints.get("sidebarBackground").setColor(Color.DKGRAY);
		this.paints.get("sidebarBackground").setAntiAlias(true);
		this.paints.get("sidebarBackground").setAlpha(200);
		this.paints.get("sidebarBackground").setStyle(Style.FILL);
		this.paints.put("sidebarLine", new Paint());
		this.paints.get("sidebarLine").setColor(Color.LTGRAY);
		this.paints.get("sidebarLine").setAntiAlias(true);
		this.paints.get("sidebarLine").setAlpha(120);
		this.paints.get("sidebarLine").setStrokeWidth(5.0f);
		this.paints.get("sidebarLine").setStyle(Style.FILL);
		
		this.paints.put("playerIndicator_red", new Paint());
		this.paints.get("playerIndicator_red").setColor(Color.parseColor("#8c1b0f"));
		
		this.paints.put("playerIndicator_blue", new Paint());
		this.paints.get("playerIndicator_blue").setColor(Color.parseColor("#262f8b"));
	}

	private void addBitmap(String label, int resource) {
		this.bitmaps.put(label,
				BitmapFactory.decodeResource(getResources(), resource));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawColor(Color.BLACK);

		this.drawBackground(canvas);
		this.drawTiles(canvas);
		this.drawPieces(canvas);
		this.drawInterface(canvas);
	}

	private void drawInterface(Canvas canvas) {
		canvas.drawRect(0, 0, sidebarWidth, canvasHeight,
				this.paints.get("sidebarBackground"));
		canvas.drawLine(sidebarWidth + 2, 0, sidebarWidth + 2, canvasHeight,
				this.paints.get("sidebarLine"));

		canvas.drawRect(
				20,
				20,
				sidebarWidth - 20,
				60,
				this.paints.get("playerIndicator_"
						+ RiseGame.colourName(this.game.getCurrentPlayer())));

	}

	private void drawPieces(Canvas canvas) {

		for (int tX = SIZE_MIN; tX < SIZE_MAX; tX += 1) {
			for (int tY = SIZE_MIN; tY < SIZE_MAX; tY += 1) {
				if (!this.game.hasPiece(tX, tY)) {
					continue;
				}

				String colour = RiseGame.colourName(this.game.pieceColour(tX,
						tY));
				String type = "worker";
				if (this.game.hasTower(tX, tY)) {
					type = String.format("tower%1d",
							this.game.towerHeight(tX, tY));
				}
				this.drawBitmap(canvas, tX, tY,
						this.bitmaps.get(colour + "_" + type));
			}
		}
	}

	private void drawBitmap(Canvas canvas, int x, int y, Bitmap bitmap) {
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();
		sidebarWidth = canvasWidth / 4;

		int centerX = drawOffsetX + (canvas.getWidth() / 2);
		int centerY = drawOffsetY + (canvas.getHeight() / 2);

		int tileW2 = tileWidth / 2;
		int tileH4 = (tileHeight / 4) * 3;
		int tileH2 = tileHeight / 2;

		int wX = (centerX + (tileWidth * x));
		int wY = (centerY + (tileH4 * y));
		if (Math.abs(y % 2) == 1) {
			wX += tileW2;
		}

		canvas.drawBitmap(bitmap, wX - tileW2, wY - tileH2, null);
	}

	private void drawTile(Canvas canvas, int x, int y, boolean highlight) {
		if (this.game.hasTile(x, y)) {
			this.drawBitmap(canvas, x, y, this.bitmaps.get("tile"));
		}
	}

	private void drawTiles(Canvas canvas) {
		for (int tX = SIZE_MIN; tX < SIZE_MAX; tX += 1) {
			for (int tY = SIZE_MIN; tY < SIZE_MAX; tY += 1) {
				if (tileHighlightedX == tX && tileHighlightedY == tY) {
					drawTile(canvas, tX, tY, true);
				} else {
					drawTile(canvas, tX, tY, false);
				}
			}
		}

	}

	private void drawBackground(Canvas canvas) {
		for (int x = 0; x < canvas.getWidth(); x += 256) {
			for (int y = 0; y < canvas.getHeight(); y += 256) {
				canvas.drawBitmap(this.bitmaps.get("background"), x, y, null);
			}
		}
	}

	@SuppressWarnings("unused")
	private static String coordString(int x, int y) {
		return String.valueOf(x) + "," + String.valueOf(y);
	}

	@SuppressWarnings("unused")
	private static String coordString(float x, float y) {
		return String.valueOf(x) + "," + String.valueOf(y);
	}

	private void updateDrawOffset(float x, float y) {
		this.drawOffsetX += (x - dragStartX);
		this.drawOffsetY += (y - dragStartY);
		dragStartX = x;
		dragStartY = y;
		this.invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			dragStartX = event.getX();
			dragStartY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			if (event.getX() <= sidebarWidth) {
				dragging = false;
				return sidebarClick(event.getX(), event.getY());
			}
			if (dragging) {
				updateDrawOffset(event.getX(), event.getY());
				dragging = false;
			} else {
				return this.gameClick(event.getX(), event.getY());
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getX() <= sidebarWidth) {
				dragging = false;
				return false;
			}
			if (!dragging
					&& (Math.abs(event.getX() - dragStartX) > DRAG_START_AMOUNT || Math
							.abs(event.getY() - dragStartY) > DRAG_START_AMOUNT)) {
				dragging = true;
			}
			if (dragging) {
				updateDrawOffset(event.getX(), event.getY());
			}
			break;
		}

		return true;
	}

	private boolean gameClick(float x, float y) {
		GridRef gr = getGridRef(x, y);
		/*
		 * tileHighlightedX = gr.x; tileHighlightedY = gr.y;
		 */
		this.game.doAction(gr.x, gr.y, this.game.getCurrentPlayer());
		this.invalidate();
		return true;
	}

	private boolean sidebarClick(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	private GridRef getGridRef(float x, float y) {

		x -= drawOffsetX;
		y -= drawOffsetY;

		x = x - (canvasWidth / 2);
		y = y - (canvasHeight / 2);

		if (y < 0) {
			y -= tileHeight / 2;
		} else {
			y += tileHeight / 2;
		}

		if (x < 0) {
			x -= tileWidth / 2;
		} else if (x > 0) {
			x += tileWidth / 2;
		}

		int gY = (int) (y / ((tileHeight / 4) * 3));
		if (Math.abs(gY % 2) == 1) {
			x -= tileWidth / 2;
		}
		int gX = (int) (x / tileWidth);

		return new GridRef(gX, gY);
	}

}