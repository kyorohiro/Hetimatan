package net.hetimatan.gui;

import net.hetimatan.ui.SimpleGraphics;
import net.hetimatan.ui.SimpleGraphicsForJ2SE;
import javafx.scene.canvas.GraphicsContext;

public class HetDisplayObjectContext {
	private int mGlobalX = 0;
	private int mGlobalY = 0;
	private int mGlobalW = 400;
	private int mGlobalH = 400;

	private GraphicsContext mGC = null;
	public HetDisplayObjectContext(GraphicsContext gc) {
		mGC = gc;
	}
	public GraphicsContext getRawGraphics() {
		return mGC;
	}

    public void setGlobalPoint(HetDisplayObjectContext graphics, int x, int y) {
            mGC = graphics.getRawGraphics();
            mGlobalX = x;
            mGlobalY = y;
            mGlobalW = graphics.getWidth();
            mGlobalH = graphics.getHeight();
    }

    public HetDisplayObjectContext getChildGraphics(HetDisplayObjectContext graphics,
                    int globalX, int globalY) {
            HetDisplayObjectContext ret =  new HetDisplayObjectContext(mGC);
            ret.setGlobalPoint(graphics, globalX, globalY);
            ret.setGlobalW(mGlobalW);
            ret.setGlobalH(mGlobalH);
            return ret;
    }

    public void drawLine(double sx, double sy, double ex, double ey) {
		mGC.strokeLine(sx+mGlobalX, sy+mGlobalY, ex+mGlobalX, ey+mGlobalY);
	}

	public void setGlobalW(int w) {
		mGlobalW = w;
	}

	public void setGlobalH(int h) {
		mGlobalH = h;
	}

	public int getGlobalX() {
		return mGlobalX;
	}

	public int getGlobalY() {
		return mGlobalY;
	}

	public int getWidth() {
            return mGlobalW;
    }

    public int getHeight() {
            return mGlobalH;
    }
}
