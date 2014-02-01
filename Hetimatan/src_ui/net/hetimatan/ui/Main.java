package net.hetimatan.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;

import com.sun.org.apache.xml.internal.security.Init;

public class Main {

	static Main m = null;
	public static void main(String[] args) {
		m = new Main();
		m.init();
	}
	public void init() {
		SimpleStageForJ2SE stage = new SimpleStageForJ2SE();
		stage.getRoot().addChild(new Fig());
        JFrame frame = new JFrame("hello swing");
        frame.getContentPane().setLayout(null);
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        stage.setVisible(true);
        stage.setBounds(100, 100, 300, 200);
        stage.setPreferredSize(new Dimension(300, 200));
        stage.setColor(SimpleGraphicUtil.GREEN);
        frame.getContentPane().setBackground(Color.BLUE);
        frame.getContentPane().add(stage);

		stage.start();
	}
	
	public static class Fig extends SimpleDisplayObject {

		@Override
		public void paint(SimpleGraphics graphics) {
			graphics.setColor(SimpleGraphicUtil.parseColor("#ffffffff"));
			graphics.setStrokeWidth(10);
			graphics.setStyle(SimpleGraphics.STYLE_FILL);
			graphics.drawCircle(0, 0, 30);
		}
		
	}
 
}
