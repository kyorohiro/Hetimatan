package net.hetimatan.ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SimpleStageForJ2SE extends JPanel implements SimpleStage, 
MouseListener, MouseMotionListener {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private int mClearColor = 0xFFAAAAFF;
        private int mSleep = 50;
        private SimpleDisplayObjectContainer mRoot = new SimpleDisplayObjectContainer();

        public SimpleStageForJ2SE() {
                setFocusable(true);
                mRoot.setParent(this);
                addMouseListener(this);
                addMouseMotionListener(this);
                setEnabled(false);
        }
        
        @Override
        public boolean isAlive() {
                if (mCurrentThread == null || !mCurrentThread.isAlive()) {
                        return false;
                } else {
                        return true;
                }
        }

        @Override
        public void setColor(int clearColor) {
                mClearColor = clearColor;
        }

        @Override
        public synchronized void start() {
                if (!isAlive()) {
                        mCurrentThread = new Thread(new Animation());
                        mCurrentThread.start();
                }
        }

        @Override
        public void resetTimer() {
                // nothing 
        }

        @Override
        public SimpleDisplayObjectContainer getRoot() {
                return mRoot;
        }

        @Override
        public synchronized void stop() {
                if (isAlive()) {
                        Thread tmp = mCurrentThread;
                        mCurrentThread = null;
                        if (tmp != null && tmp.isAlive()) {
                                tmp.interrupt();
                        }
                }
        }

        @Override
        public boolean isSupportMultiTouch() {
                // j2se not support multitouch
                return false;
        }


        private void doDraw() {
                repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
//        @Override
//        public void paint(Graphics g) {
                ///*
                setVisible(true);
//                setPreferredSize(new Dimension(400, 400));
                g.setColor(new Color(mClearColor));
                g.fillRect(0, 0, getWidth(), getHeight());
        //        System.out.println("--begin paint--"+0+","+ 0+","+getWidth()+","+ getHeight());
                mRoot.paint(new SimpleGraphicsForJ2SE((Graphics2D)g, 0, 0, getWidth(), getHeight()));
        //        System.out.println("--end paint--");
         //*/
        }

        private Thread mCurrentThread = null;
        private class Animation implements Runnable {
                @Override
                public void run() {
                        try {
//                                System.out.println("--begin animation--");
                                Thread currentThread = null;
                                while(true) {
                                        currentThread = mCurrentThread;
                                        if(currentThread == null || currentThread != Thread.currentThread()) {
                                                break;
                                        }
//                                        doIME();
                                        doDraw();
                                        try {
                                                Thread.sleep(mSleep);
                                        } catch (InterruptedException e) {
                                                break;
                                        }
                                }
                        }
                        catch(Throwable t) {
//                                System.out.println("--error animation--");
                                t.printStackTrace();
                        }
                        finally {
//                                System.out.println("--end animation--");
                        }
                }
        }

        
        
        @Override
        public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                
        }

        private boolean mIsEntered = false;
        @Override
        public void mouseEntered(MouseEvent e) {
                mIsEntered = true;
        }

        @Override
        public void mouseExited(MouseEvent e) {
                mIsEntered = false;
        }

        @Override
        public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
//                System.out.println("mousePressed x="+x+",y="+y+","+mIsEntered);
                if(mIsEntered) {
                        getRoot().onTouchTest(x, y, SimpleMotionEvent.ACTION_DOWN);
                }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
//                System.out.println("mouseRelessed x="+x+",y="+y+","+mIsEntered);
                if(mIsEntered) {
                        getRoot().onTouchTest(x, y, SimpleMotionEvent.ACTION_UP);
                }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
//                System.out.println("mouseDragged x="+x+",y="+y+","+mIsEntered);
                if(mIsEntered) {
                        getRoot().onTouchTest(x, y, SimpleMotionEvent.ACTION_MOVE);
                }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
//                System.out.println("mouseMoved x="+x+",y="+y+","+mIsEntered);
        }

		@Override
		public void showInputConnection() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void hideInputConnection() {
			// TODO Auto-generated method stub
			
		}
}