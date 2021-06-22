package test;

import java.util.Scanner;
import models.FullInventoryException;
import models.NoCoinException;
import models.NoWaterException;
import models.Map;
import models.MauerDaException;
import models.WaterException;

public class TestClass {
	
	static Map map;
	static boolean run;

	public static void main(String[] args) {
		map = new Map();
		run = true;
		
		System.out.println("X = "+map.getMapArray().length+" Y = "+map.getMapArray()[0].length);
		
		while(run) {
			print2DArray(map.getMapArray());
			try {
				getCommand();
			} catch(InvalidCommandException e) {
				System.err.println("Invalid command!");
			}
		}
		

	}
	
	private static void getCommand() throws InvalidCommandException {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		try {
			switch(input) {
			case "walk" :
				System.out.println("Player -> walk ...");
				map.walk();
				break;
			case "swim":
				System.out.println("Player -> swim ...");
				map.swim();
				break;
			case "turnLeft":
				System.out.println("Player -> turnLeft ...");
				map.turnLeft();
				break;
			case "turnRight":
				System.out.println("Player -> turnrRight ...");
				map.turnRight();
				break;
			case "takeCoin":
				System.out.println("Player -> takeCoin ...");
				map.takeCoin();
				break;
			case "getCoin":
				System.out.println("Player -> getCoin ...");
				System.out.println("Out -> "+map.getCoin());
				break;
			case "isSwimming":
				System.out.println("Player -> isSwimming ...");
				System.out.println("Out -> "+map.isSwimming());
				break;
			case "isInventoryFull":
				System.out.println("Player -> isInventoryFull ...");
				System.out.println("Out -> "+map.isInventoryFull());
				break;
			case "frontIsClear":
				System.out.println("Player -> frontIsClear ...");
				System.out.println("Out -> "+map.frontIsClear());
				break;
			case "frontIsWater":
				System.out.println("Player -> frontIsWater ...");
				System.out.println("Out -> "+map.frontIsWater());
				break;
			case "resize":
				System.out.println("Map -> resize ...");
				map.resize(5, 10);
				break;
			case "end":
				System.out.println("Test -> end ...");
				run = false;
				break;
			default:
				throw new InvalidCommandException();
			}
		} catch(MauerDaException e) {
			System.err.println("... -> MauerDaException");
		} catch(WaterException e) {
			System.err.println("... -> WasserDaException");
		} catch(NoWaterException e) {
			System.err.println("... -> KeinWasserException");
		} catch(FullInventoryException e) {
			System.err.println("... -> InventarVollException");
		} catch(NoCoinException e) {
			System.err.println("... -> KeinCoinException");
		}
	}
	
	private static void print2DArray(int[][] a) {
		System.out.println("Printing map ...");
		String out = new String();
		for(int x = 0; x < a.length; x++) {
			out+= "{";
			for(int y = 0; y < a[x].length; y++) {
				if(map.heroIsOnPos(y, x)) {
					
					switch(map.getDir()) {
					case "North":
						out+= "^";
						break;
					case "East":
						out+= ">";
						break;
					case "South":
						out+= "v";
						break;
					case "West":
						out+="<";
						break;
					default:
						out+="?";
					}
				} else if(a[x][y] == 0) {
					out+= "-";
				} else if(a[x][y] == -1) {
					out+="s";
				} else if(a[x][y] == 1) {
					out+="m";
				} else if(a[x][y] == 2) {
					out += "$";
				} else {
				
					out+= a[x][y];
				}
				
				
				if(y != a[x].length-1)
					out+= ", ";
			}
			out+= "}\n";
		}
		System.out.println(out);
	}

}

@SuppressWarnings("serial")
class InvalidCommandException extends Exception {
	
}