package officialcfm.launcher;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

/**
 * Essentially a hack to get the background behind the changelog to display
 * 
 * @author KP
 */
public class ImagePane extends JComponent {
	private static final long serialVersionUID = 1L;
	private Image image, overlay;
	
	public ImagePane(Image image, Image overlay) {
		this.image = image;
		this.overlay = overlay;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		g.drawImage(overlay, 0, 480 - overlay.getHeight(null) - 28, getWidth(), overlay.getHeight(null), this);
	}
	
	public Image getImage() {
		return image;
	}
	
	public Image getOverlay() {
		return overlay;
	}
}
