package ui;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Utility for loading and scaling bundled icon resources.
 */
public final class UiIcons {

	private UiIcons() {
	}

	public static ImageIcon loadIcon(String path, int width, int height) {
		// Resource paths are expected to be classpath-relative (e.g. /ui/icons/x.png).
		URL resource = UiIcons.class.getResource(path);
		if (resource == null) {
			return null;
		}
		Image image = new ImageIcon(resource).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}
}
