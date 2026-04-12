package main;

import javax.swing.SwingUtilities;

import ui.TypeGaugeFrame;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TypeGaugeFrame frame = new TypeGaugeFrame();
				frame.setVisible(true);
			}
		});
	}

}
