package renderer.util;

import java.util.ArrayList;
import java.util.Set;

/**
 * The TextManager is responsible for handling textboxes and changes to text inside of them.
 * The TextBoxes are rendered by the TextRenderer.
 * @author adi
 */
public class TextManager {
	private static TextManager instance = null;
	private static final Object TEXT_MANAGER_LOCK = new Object();

	// List of textboxes the text manager is responsible for handling.
	ArrayList<TextBox> textAreas;

	private TextManager () {
		textAreas = new ArrayList<TextBox>();
	}

	/**
	 * Gets the instance of this singleton. Lazily instantiates if not already created.
	 */
	public static TextManager getInstance () {
		synchronized(TEXT_MANAGER_LOCK) {
			if (instance == null) {
				instance = new TextManager();
			}

			return instance;
		}
	}

	public void removeTextBox (TextBox t) {
		synchronized(TEXT_MANAGER_LOCK) {
			this.textAreas.remove(t);
		}
	}

	public void addTextBox (TextBox t) {
		synchronized(TEXT_MANAGER_LOCK) {
			this.textAreas.add(t);
		}
	}

	public ArrayList<TextBox> getTextBoxes () {
		synchronized(TEXT_MANAGER_LOCK) {
			return this.textAreas;
		}
	}

	public void setText (TextBox t, String newText) {
		synchronized(TEXT_MANAGER_LOCK) {
			int ind = this.textAreas.indexOf(t);
			if (ind == -1) return;

			this.textAreas.get(ind).text = newText;
		}
	}
}
