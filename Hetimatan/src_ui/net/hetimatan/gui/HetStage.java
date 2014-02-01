package net.hetimatan.gui;

import javafx.scene.canvas.Canvas;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class HetStage extends Canvas {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private int mClearColor = 0xFFAAAAFF;
        private int mSleep = 50;
        private HetDisplayObjectGroup mRoot = new HetDisplayObjectGroup();

        public HetStage() {
                mRoot.setParent(this);
        }
        
        public boolean isAlive() {
                if (mCurrentThread == null || !mCurrentThread.isAlive()) {
                        return false;
                } else {
                        return true;
                }
        }

        public void setColor(int clearColor) {
                mClearColor = clearColor;
        }

        public synchronized void start() {
                if (!isAlive()) {
                        mCurrentThread = new Thread(new Animation());
                        mCurrentThread.start();
                }
        }

        public void resetTimer() {
                // nothing 
        }

        public HetDisplayObjectGroup getRoot() {
                return mRoot;
        }

        public synchronized void stop() {
                if (isAlive()) {
                        Thread tmp = mCurrentThread;
                        mCurrentThread = null;
                        if (tmp != null && tmp.isAlive()) {
                                tmp.interrupt();
                        }
                }
        }

        public boolean isSupportMultiTouch() {
                // j2se not support multitouch
                return false;
        }


        private void doDraw() {
        	//repaint();
        }

       
/*
        @Override
        protected void paintComponent(Graphics g) {
//        @Override
//        public void paint(Graphics g) {
                //
                setVisible(true);
//                setPreferredSize(new Dimension(400, 400));
                g.setColor(new Color(mClearColor));
                g.fillRect(0, 0, getWidth(), getHeight());
        //        System.out.println("--begin paint--"+0+","+ 0+","+getWidth()+","+ getHeight());
                mRoot.paint(new SimpleGraphicsForJ2SE((Graphics2D)g, 0, 0, getWidth(), getHeight()));
        //        System.out.println("--end paint--");
         //
        }
*/

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

		public void showInputConnection() {
		}

		public void hideInputConnection() {
		}
}