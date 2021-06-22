package util;
import java.util.Random;

public class Random2DArray {
	
	private int width;
	private int height;
	private int player_x;
	private int player_y;
	private Random rnd = new Random();
	
	public Random2DArray(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int[][] generate(int water, int wall, int coin, int player, int waterCount, int wallCount, int coinCount) {
		int[][] map = new int[width][height];
		this.player_x = (width > 0) ? rnd.nextInt(width-1) : 0;
		this.player_y = (height > 0) ? rnd.nextInt(height-1) : 0;
		map[player_x][player_y] = player;
		try {
			int x, y;
			
			for(int i = 0; i < waterCount; i++) {
				x = rnd.nextInt(width-1);
				y = rnd.nextInt(height-1);
				if(map[x][y] == 0) {
					map = generateWater(x, y, water, 100, map);
				}
			}
			for(int i = 0; i < wallCount; i++) {
				x = rnd.nextInt(width-1);
				y = rnd.nextInt(height-1);
				if(map[x][y] == 0) {
					map[x][y] = wall;
				}
			}
			for(int i = 0; i < coinCount; i++) {
				x = rnd.nextInt(width-1);
				y = rnd.nextInt(height-1);
				if(map[x][y] == 0) {
					map[x][y] = coin;
				}
			}
			
		} catch(IndexOutOfBoundsException e) {
			//Im Fall der Fälle
			e.printStackTrace();
		}
		return map;
	}
	
	private int[][] generateWater(int x, int y, int value, int chance, int[][] map) {
		map[x][y] = value;
		if(chance < 5 || rnd.nextInt(101) > chance)
			return map;
		
		int dir = rnd.nextInt(4);
		switch(dir) {
		case(0): 
			if(y-1 >= 0) {
				if(map[x][y-1] == 0) 
					generateWater(x, y-1, value, chance - 15, map);
			}
			break;
		case(1):
			if(x+1< this.width) {
				if(map[x+1][y] == 0)
					generateWater(x+1, y, value, chance - 15, map);
			}
			break;
		case(2):
			if(y+1 < this.height) {
				if(map[x][y+1] == 0)
					generateWater(x, y+1, value, chance - 15, map);
			}
			break;
		case(3):
			if(x-1 >= 0) {
				if(map[x-1][y] == 0)
					generateWater(x-1, y, value, chance - 15, map);
			}
			break;
		}
		
		return map;
	}
	
	public int getPlayerY() {
		return this.player_x;
	}
	
	public int getPlayerX() {
		return this.player_y;
	}
	
	
	
	
	
	
	
	
}
