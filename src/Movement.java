import java.util.*;

import java.io.*;
import drawing.*;

import javax.swing.JFrame;

public class Movement {

	public static JFrame f;
	public static boolean drawFrames = false;

	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int random = rand.nextInt((max - min) + 1) + min;
	    return random;
	}

	public static void moveChar(Board board, Tile tile, Tile newPos) {
		int[] emptyUnit = {0,0,0};
		// Do we move to an empty tile?
		if (board.tiles[newPos.xCor][newPos.yCor].unit[0] == 0){
			board.tiles[newPos.xCor][newPos.yCor].unit = tile.unit;
			board.tiles[tile.xCor][tile.yCor].unit = emptyUnit;
			// System.out.println("Moved to: "+ newPos.xCor+", "+newPos.yCor);
		// Hit unit if tile not empty
		} else{
			System.out.println(getUnitName(tile) + " is attacking "+getUnitName(newPos));
			if (Combat.hit(tile, newPos, board.tiles)) {
				// System.out.println("Hit!");
				int[] hitUnit = board.tiles[newPos.xCor][newPos.yCor].unit;
				// if the unit that is hit has more than no hitpoints, do one damage
				if (hitUnit[1] > 0) hitUnit[1] = hitUnit[1]-1;
				// if unit has one hitpoint and is hit, it dies
				else {
					hitUnit = emptyUnit;
					// System.out.println("it died");
				}
				board.tiles[newPos.xCor][newPos.yCor].unit = hitUnit;
			} else {
				// System.out.println("Missed..");
			}
		}
	}

	public static Tile moveAiChar(Board board, Tile tile, Tile newPos) {
		int[] emptyUnit = {0,0,0};
		// Do we move to an empty tile?
		if(board.tiles[newPos.xCor][newPos.yCor].unit[0] == 0){
			board.tiles[newPos.xCor][newPos.yCor].unit = tile.unit;
			board.tiles[tile.xCor][tile.yCor].unit = emptyUnit;
			// System.out.println("Moved to: "+ newPos.xCor+", "+newPos.yCor);
			return newPos;
		// Hit unit if tile not empty
		} else{
			System.out.println(getUnitName(tile) + " is attacking "+getUnitName(newPos));
			if (Combat.hit(tile, newPos, board.tiles)) {
				// System.out.println("Hit!");
				int[] hitUnit = board.tiles[newPos.xCor][newPos.yCor].unit;
				// if the unit that is hit has more than no hitpoints, do one damage
				if (hitUnit[1] > 0) hitUnit[1] = hitUnit[1]-1;
				// if unit has one hitpoint and is hit, it dies
				else {
					hitUnit = emptyUnit;
					// System.out.println("it died");
				}
				board.tiles[newPos.xCor][newPos.yCor].unit = hitUnit;
			} else {
				// System.out.println("Missed..");
			}
			return tile;
		}
	}

	public static void aiMove(Board board, int race) {
		// race = 0 means humans, race = 2 means greenskins
		// list of board locations/characters that need to be moved
		ArrayList<Tile> charList = new ArrayList<Tile>();
		for (int j = 0; j<board.xMax+1; j++) {
     		for (int i = 0; i<board.tiles[j].length; i++) {
     			if(board.tiles[j][i] == null){
     				continue;
     			} else {
     				if (board.tiles[j][i].unit[0] == race + 1 ||
     					board.tiles[j][i].unit[0] == race + 2){
     					charList.add(board.tiles[j][i]);
     				}
     			}
     		}
     	}
	    while (!charList.isEmpty()) {
	    	Tile randomUnit = charList.get(randInt(0, charList.size()-1));
	    	charList.remove(randomUnit);
	     	for (int moveCount = 0; moveCount < 2; moveCount++) {
	     		int neighbourListSize = randomUnit.neighbours.size()-1;
	     		Tile newPos = randomUnit;
	     		for (int neighbourCount = 0; neighbourCount < neighbourListSize; neighbourCount++) {
	     			Integer[] newCors = randomUnit.neighbours.get(randInt(0, neighbourListSize));
	     			if (board.tiles[newCors[0]][newCors[1]].unit[0] != race + 1 &&
	     				board.tiles[newCors[0]][newCors[1]].unit[0] != race + 2) {
	     				newPos = board.tiles[newCors[0]][newCors[1]];
	     			}
	     			if (board.tiles[newCors[0]][newCors[1]].unit[0] != race + 1 &&
	     				board.tiles[newCors[0]][newCors[1]].unit[0] != race + 2 &&
	     				board.tiles[newCors[0]][newCors[1]].unit[0] != 0) {
	     				newPos = board.tiles[newCors[0]][newCors[1]];
	     				break;
	     			}
	     		}
	     		if (newPos != randomUnit) {
	     			randomUnit = moveAiChar(board, randomUnit, newPos);
	     			if(drawFrames){
						Integer[][] newdata = boardDrawData(board);
						f.setContentPane(new drawing.Main(newdata));
				        f.pack();
				        f.setVisible(true);
				    }
                    try {
	                    if(drawFrames) Thread.sleep(200);
	                } catch(InterruptedException ex) {
	                    Thread.currentThread().interrupt();
	                }
	     		}
	     	}
	    }
	}

	public static void reinforcedLearningMove(Board board, int race) {
		ArrayList<Tile> charList = new ArrayList<Tile>();
		for (int j = 0; j<board.xMax+1; j++) {
     		for (int i = 0; i<board.tiles[j].length; i++) {
     			if(board.tiles[j][i] == null){
     				continue;
     			} else {
     				if (board.tiles[j][i].unit[0] == race + 1 ||
     					board.tiles[j][i].unit[0] == race + 2){
     					charList.add(board.tiles[j][i]);
     				}
     			}
     		}
     	}
     	Tile[] unitsToMove;
     	while (!charList.isEmpty()) {
     		int characterAmount = charList.size();
     		if (characterAmount > 3) {
     			unitsToMove = new Tile[3];
     		}
     		else {
     			unitsToMove = new Tile[characterAmount];
     		}
     		for (int unitCount = 0; unitCount < unitsToMove.length; unitCount++) {
				characterAmount-=1;
     			unitsToMove[unitCount] = charList.get(characterAmount);
     			charList.remove(characterAmount);
			}
			createNewMove(board, unitsToMove, race);
		}
	}

	public static void createNewMove(Board board, Tile[] unitsToMove, int race) {
		Tile[][] nextStates = getPossibleStates(board, unitsToMove, race);
		Board boardCopy = board;
		try {
			boardCopy = board.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Tile[] nextState = Learning.Learning(boardCopy, unitsToMove, nextStates, race);
		int xCor = nextState[0].xCor;
		for (int unitCount = 0; unitCount < unitsToMove.length; unitCount++){
			Board.printNeighbours(unitsToMove[unitCount]);
			moveChar(board, unitsToMove[unitCount], nextState[unitCount]);
			if(drawFrames){
				Integer[][] newdata = boardDrawData(board);
				f.setContentPane(new drawing.Main(newdata));
		        f.pack();
		        f.setVisible(true);
		    }
		    try {
                if(drawFrames) Thread.sleep(200);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
		}
	}

	public static Tile[][] getPossibleStates(Board board, Tile[] state, int race) {
		Tile[][] nextStateList = new Tile[125][state.length];
		for (int strategyCount = 0; strategyCount < 125; strategyCount++) {
			Board boardCopy = board;
			try {
				boardCopy = board.clone();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Tile[] nextState = getNextStates(boardCopy, state, race);
			for (int tilesInState = 0; tilesInState < nextState.length; tilesInState++) {
				if(nextState[tilesInState].unit[0] != 0 && 
					nextState[tilesInState].unit[0] != race + 1	&&
					nextState[tilesInState].unit[0] != race + 2) {
					nextStateList = new Tile[1][nextState.length];
					nextStateList[0] = nextState;
					return nextStateList;
				}
			}
			nextStateList[strategyCount] = nextState;
		}
		return nextStateList;
	}

	public static Tile[] getNextStates(Board boardCopy, Tile[] state, int race) {
		Random random = new Random();
		Tile[] nextStates = new Tile[state.length];
		for (int stateCount = 0; stateCount < state.length; stateCount++) {
			Tile currentState = state[stateCount];
			ArrayList<Tile> nextStateCandidates = new ArrayList<Tile>();
			for (Integer[] neighbour : currentState.neighbours) {
				Tile neighbourTile = boardCopy.tiles[neighbour[0]][neighbour[1]];
				if(neighbourTile.unit[0] != 0 && 
					neighbourTile.unit[0] == race + 1 &&
					neighbourTile.unit[0] == race + 2) {
					System.out.println("-------------------------------");
					nextStateCandidates = new ArrayList<Tile>();
					nextStateCandidates.add(neighbourTile);
					break;
				}
				if (neighbourTile.unit[0] != race + 1 &&
					neighbourTile.unit[0] != race + 2) {
					nextStateCandidates.add(neighbourTile);
				}
			}
			Tile nextState;
			if (nextStateCandidates.size() > 0) {
				nextState = nextStateCandidates.get(random.nextInt(nextStateCandidates.size()));
			} else {
				nextState = currentState;
			}
			nextStates[stateCount] = moveAiChar(boardCopy, currentState, nextState);
		}
		return nextStates;
	}

	public static void playerMove(Board board){
		// list of board locations/characters that need to be moved

		ArrayList<Tile> charList = new ArrayList<Tile>();
		for (int j = 0; j<board.xMax+1; j++) {
     		for (int i = 0; i<board.tiles[j].length; i++) {
     			if(board.tiles[j][i] == null){
     				continue;
     			} else {
     				if (board.tiles[j][i].unit[0] == 1 ||
     					board.tiles[j][i].unit[0] == 2){
     					Tile movetile = new Tile(board.tiles[j][i].xCor, board.tiles[j][i].yCor, board.tiles[j][i].unit, board.tiles[j][i].neighbours);
     					charList.add(movetile);
     					charList.add(movetile);
     				}
     			}
     		}
     	}
     	while(!charList.isEmpty()){
     		Integer[][] newdata = boardDrawData(board);
			f.setContentPane(new drawing.Main(newdata));
	        f.pack();
	        f.setVisible(true);
	     	Scanner scan = new Scanner(System.in);
			int oldXCor = 0;
			int oldYCor = 0;
			int newXCor = 0;
			int newYCor = 0;
			// Input loop
			while(true) {
				try {
					Main.printBoard(board);
					System.out.println("characters to move: ");
					Set<Tile> charHash = new HashSet<>();
					charHash.addAll(charList);
			     	for(Tile charTile : charHash){
			     		System.out.println( getUnitName(charTile) + " at " + "["+ charTile.xCor + "," + charTile.yCor + "]");
			     	}
					System.out.println("Please select a character to move.");	
					String selectUnit = scan.nextLine();
					oldXCor = Character.getNumericValue(selectUnit.charAt(0));
					oldYCor = Character.getNumericValue(selectUnit.charAt(2));
					if (!isExistingTile(oldXCor, oldYCor, charList)) {
						System.out.println("Not a unit, use x and y coordinates seperated by a ,");
						continue;
					}
					System.out.println("Please select where you want to move it.");
					System.out.print("possible moves: ");
					for(Integer[] neighbour : board.tiles[oldXCor][oldYCor].neighbours){
						System.out.print("[" + neighbour[0] + "," + neighbour[1] + "] ");
					}
					System.out.println("");
					String newPosition = scan.nextLine();
					newXCor = Character.getNumericValue(newPosition.charAt(0));
					newYCor = Character.getNumericValue(newPosition.charAt(2));
					if (!isExistingNeighbour(newXCor, newYCor, board.tiles[oldXCor][oldYCor].neighbours)) {
						System.out.println("Not an existing tile, use x and y coordinates seperated by a ,");
						continue;
					}
					break;
				} catch(Exception e) {
					System.out.println("Bad input, please give x and y coordinates seperated by a ,");
					continue;
				}
			}
			Tile selectedUnit = board.tiles[oldXCor][oldYCor];
			Tile newPos = board.tiles[newXCor][newYCor];

			moveChar(board, selectedUnit, newPos);

			ArrayList<Tile> doublemovecheck = new ArrayList<Tile>();
			for(int i = 0; i< charList.size(); i++){
				charList.get(i);
				// add the left over moves to a temporary arraylist
				if(charList.get(i).xCor == oldXCor && charList.get(i).yCor == oldYCor){
					doublemovecheck.add(charList.get(i));
				}
			}
			// if more than one move left, change leftover move to new location and remove one move
			Boolean removeTile = true;
			if(doublemovecheck.size() > 1) {
				for(int i = 0; i< charList.size(); i++){
					if(charList.get(i).xCor == oldXCor && charList.get(i).yCor == oldYCor && removeTile){
						charList.remove(charList.get(i));
						removeTile = false;
					}
					if(board.tiles[charList.get(i).xCor][charList.get(i).yCor].unit[0] == 0){
						if(charList.get(i).xCor == oldXCor && charList.get(i).yCor == oldYCor){
							charList.get(i).xCor = newXCor;
							charList.get(i).yCor = newYCor;
						}
					}
				}
			} else if(doublemovecheck.size() == 1) {
				for(int i = 0; i< charList.size(); i++){
					if(charList.get(i).xCor == oldXCor && charList.get(i).yCor == oldYCor){
						charList.remove(charList.get(i));
					}
				}
			}
		}
	}

	private static Boolean isExistingTile(int xCor, int yCor, ArrayList<Tile> tileList) {
		for (Tile tile : tileList) {
			if (tile.xCor == xCor && tile.yCor == yCor) {
				return true;
			}
		}
		return false;
	}

	private static Boolean isExistingNeighbour(int xCor, int yCor, ArrayList<Integer[]> neighbourList) {
		for (Integer[] neighbour: neighbourList) {
			if (neighbour[0] == xCor && neighbour[1] == yCor) {
				return true;
			}
		}
		return false;
	}

	private static String getUnitName(Tile t) {
		if(t.unit[0] == 1) return("swordsman");
		if(t.unit[0] == 2) return("general");
		if(t.unit[0] == 3) return("goblin");
		if(t.unit[0] == 4) return("orc");
		return("empty tile");
	}

	public static Integer[][] boardDrawData(Board board){
        Integer[][] data = new Integer[9][9]; 
        for (int j = 0; j<board.xMax+1; j++) {
            for (int i = 0; i<board.tiles[j].length; i++) {
                if(board.tiles[j][i] == null){
                    continue;
                } else {
                    if (board.tiles[j][i].unit[0] == 1){
                        data[j][i] = 1;
                    }else if(board.tiles[j][i].unit[0] == 2){
                        data[j][i] = 2;
                    }else if(board.tiles[j][i].unit[0] == 3){
                        data[j][i] = 3;
                    }else if(board.tiles[j][i].unit[0] == 4){
                        data[j][i] = 4;
                    } else {
                        data[j][i] = 0;
                    }
                }
            }
        }
        return data;
    }
}