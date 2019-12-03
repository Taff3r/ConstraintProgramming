
public class Metric {
	private int nodes = 0; // 1 or 0?
	private int wrongs = 0;

	public void wrong() {
		wrongs++;
	}

	public void newNode() {
		nodes++;
	}

	@Override
	public String toString() {
		return "# Nodes: " + nodes + "\n" + "# Wrong steps: " + wrongs;
	}

}
