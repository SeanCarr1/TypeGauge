package ui;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public final class UiIcons {

	private UiIcons() {
	}

	public static ImageIcon loadIcon(String path, int width, int height) {
		URL resource = UiIcons.class.getResource(path);
		if (resource == null) {
			return null;
		}
		Image image = new ImageIcon(resource).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}
}
