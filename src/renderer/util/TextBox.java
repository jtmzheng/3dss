package renderer.util;

public class TextBox {
	public String text;
	public int x, y, size;

	public TextBox (String text, int x, int y, int size) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.size = size;
	}

	@Override
	public boolean equals (Object o) {
		if (!(o instanceof TextBox)) return false;
		TextBox t = (TextBox) o;

		return this.text.equals(t.text) && this.x == t.x
			&& this.y == t.y && this.size == t.size;
	}
}
