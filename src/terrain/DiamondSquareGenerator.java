package terrain;

import java.util.Random;

public class DiamondSquareGenerator extends TerrainGenerator {

	public DiamondSquareGenerator(int size, int minHeight, int maxHeight, double roughness) {
		this.size = size;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.roughness = Math.pow(2, roughness);
		this.grid = new int[size][size];
	}
	
	@Override
	public Terrain generateTerrain() {
		int h = maxHeight - minHeight;
	    Random rand = new Random();
	    
	    // Seed the corners
	    grid[0][0] = rand.nextInt(h + 1) + minHeight;
	    grid[0][size - 1] = rand.nextInt(h + 1) + minHeight;
	    grid[size - 1][0] = rand.nextInt(h + 1) + minHeight;
	    grid[size - 1][size - 1] = rand.nextInt(h + 1) + minHeight;
	    
		for(int stride = (size - 1) / 2; stride > 0; stride /= 2, h /= roughness) {
			//1. Diamond
			for(int x = stride; x < size - 1; x += stride * 2) {
				for(int z = stride; z < size - 1; z += stride * 2) {
					diamond(x, z, stride, h, rand);
				}
			}
			
			//2. Square
			boolean flag = false;
			for(int z = 0; z < size; z += stride) {
				int start =  flag ? 0 : stride;
				flag = !flag;
				for(int x = start; x < size; x += stride) {
					if(grid[x][z] == 0) {
						square(x, z, stride, h, rand);
					}	
				}
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[0].length; j++) {
				sb.append(" " + grid[i][j] + " ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private void diamond(int x, int z, int stride, int h, Random rand) {
		int average = grid[x - stride][z - stride] + 
				grid[x - stride][z + stride] +
				grid[x + stride][z - stride] +
				grid[x + stride][z + stride];
		
		grid[x][z] = (average / 4) + (rand.nextInt(h + 1) + minHeight);
	}
	
	private void square(int x, int z, int stride, int h, Random rand) {
		int average = grid[(x - stride + (size - 1)) % (size - 1)][z] + 
				grid[x][(z + stride + (size - 1)) % (size - 1)] +
				grid[(x + stride + (size - 1)) % (size - 1)][z] +
				grid[x][(z + stride + (size - 1)) % (size - 1)];

		grid[x][z] = (average / 4) + (rand.nextInt(h + 1) + minHeight);
	}
	
	public static void main(String[] args) {
		TerrainGenerator tg = new DiamondSquareGenerator(5, 0, 10, 0.5);
		tg.generateTerrain();
		System.out.println(tg);
	}

	private int size;
	private int minHeight;
	private int maxHeight;
	private double roughness;
	private int [][] grid;
	
}

