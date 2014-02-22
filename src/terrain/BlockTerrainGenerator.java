package terrain;

public class BlockTerrainGenerator {

	public BlockTerrainGenerator(int tSize, int bSize, double sFactor) {
		this.tSize = tSize;
		this.bSize = bSize;
		this.sFactor = sFactor;
	}
	
	public BlockTerrain generateTerrain() {
		int [][][] volume = new int [tSize][tSize][tSize];
		double xf, yf, zf, val;
		for(int x = 0; x < volume.length; x++) {
			for(int y = 0; y < volume[0].length; y++) {
				for(int z = 0; z < volume[0][0].length; z++) {
					xf = (double)x / tSize;
					yf = (double)y / tSize;
					zf = (double)z / tSize;
					System.out.println(xf);
					val = SimplexNoise.simplex(1, xf * 3, yf * 3, zf * 3);
					volume[x][y][z] = val > sFactor ? 1 : 0; 
				}
			}
		}
		
		return new BlockTerrain(volume, bSize);
	}
	
	public void setTerrainSize(int tSize) {
		this.tSize = tSize;
	}
	
	public void setBlockSize(int bSize) {
		this.bSize = bSize;
	}
	
	public void setSimplexFactor(double sFactor) {
		this.sFactor = sFactor;
	}
	
	private int tSize;
	private int bSize;
	private double sFactor;
}
