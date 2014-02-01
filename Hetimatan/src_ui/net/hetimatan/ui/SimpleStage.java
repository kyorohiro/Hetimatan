package net.hetimatan.ui;

public interface SimpleStage {
    public boolean isAlive();
    public void setColor(int bgcolor);
    public void start();
    public void resetTimer();
    public SimpleDisplayObjectContainer getRoot();
    public void stop();
    public void showInputConnection();
    public void hideInputConnection();
    public boolean isSupportMultiTouch();
}
