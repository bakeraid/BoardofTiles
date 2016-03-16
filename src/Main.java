import java.util.*;
import java.io.*;
import drawing.*;

import javax.swing.JFrame;

public class Main {

    public static void main (String[] args) {
        int balance = 0;
        // Gameloop
        int breakCount = 0;
        for (int gameCount = 0; gameCount < 10; gameCount++) {
            Board board = buildBoard(8, 4, 4);
            Movement.drawFrames = true; // comment this to disable drawing windows
            if(Movement.drawFrames){
                System.out.println("drawing frames enabled.");
                JFrame fr = new JFrame();
                fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
                Integer[][] data = Movement.boardDrawData(board);
                drawing.Main frame = new drawing.Main(data);
                Movement.f = fr;
                fr.pack();
                fr.setVisible(true);
            }
            for (int i = 0; i < 100; i++) {
                // printBoard(board);
                System.out.println("-----------Human Turn-----------");
                Movement.reinforcedLearningMove(board, 0);
                Movement.reinforcedLearningMove(board, 0);
                // Movement.aiMove(board, 0);
                // Movement.playerMove(board);
                System.out.println("-----------Greenskin------------");
                Movement.aiMove(board, 2);
                // Movement.reinforcedLearningMove(board, 2);
                // Movement.reinforcedLearningMove(board, 2);
                try {
                    Thread.sleep(0);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                breakCount++;
                if (Board.gameWon(board) == -1) {
                    // Orcs won
                    balance-=1;
                    break;
                }
                if (Board.gameWon(board) == 1) {
                    // Humans won
                    balance+=1;
                    break;
                }
            }
            System.out.println("GAME");
        }
        // JFrame f = new JFrame();
        // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Board board = buildBoard(8, 4, 4);
        // Integer[][] data = boardDrawData(board);
        // drawing.Main frame = new drawing.Main(data);
        // // Gameloop
        // while (true) {
        //     Integer[][] newdata = boardDrawData(board);
        //     f.setContentPane(new drawing.Main(newdata));
        //     f.pack();
        //     f.setLocationRelativeTo(null);
        //     f.setVisible(true);
        //     Movement.playerMove(board);
        //     Scanner scan = new Scanner(System.in);
        //     try {
        //         int xCor = scan.nextInt();
        //         int yCor = scan.nextInt();
        //         System.out.println(xCor);
        //         System.out.println(yCor);
        //     }
        //     breakCount++;
        //     if (breakCount % 100 == 0) {
        //         System.out.println("Calculating.. " +breakCount);
        //     //break;
        //     //drawing.Main frame = new drawing.Main();
        //     //frame.kill()
        //     printBoard(board);
        // }

        // Write to file
        // try{
        //     writeDoubleArrayToFile("twoPositions.model", Learning.learning);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        System.out.println();
        System.out.println(breakCount);
        System.out.println("---------------------------------");
        System.out.println("BALANCE IS " + balance + " -----------");
        System.out.println("---------------------------------");
    }

    public static Board buildBoard(int xMax, int yMax, int yMin){
        Board board = new Board(xMax,yMax,yMin);
        initUnits(board);
        return(board);
    }


    public static void printBoard(Board board){
        System.out.print("+-");
        for (int yAxis = 0; yAxis < board.yMax+board.yMin+1; yAxis++) {
            System.out.print("-"+yAxis+"-");
        }
        System.out.println("-+");
        for (int xOffset = 0; xOffset < board.xMax+1; xOffset++) {
            System.out.print(xOffset+"-");
            for (int yOffset = 0; yOffset < board.yMax+board.yMin+1; yOffset++) {
                if (board.tiles[xOffset][yOffset] == null) {
                    System.out.print("---");
                   continue;
                } else {
                    String unit = getUnitName(board.tiles[xOffset][yOffset].unit[0]);
                    System.out.print("["+unit+"]");
                }
            }
            System.out.println("-"+xOffset);
        }
        System.out.print("+-");
        for (int yAxis = 0; yAxis < board.yMax+board.yMin+1; yAxis++) {
            System.out.print("-"+yAxis+"-");
        }
        System.out.println("-+");
    }

    public static void initUnits(Board board) {
        int[] orc = {4, 10, 8};
        int[] goblin = {3, 3, 4};
        int[] general = {2, 5, 8};
        int[] swordsman = {1, 4, 6};
        //Initialize Greenskin side
        board.tiles[8][4].unit = orc;
        board.tiles[3][8].unit = orc;
        board.tiles[7][4].unit = goblin;
        board.tiles[7][3].unit = goblin;
        board.tiles[6][2].unit = goblin;
        board.tiles[6][5].unit = goblin;
        board.tiles[5][5].unit = goblin;
        board.tiles[5][7].unit = goblin;
        board.tiles[4][7].unit = goblin;
        board.tiles[3][7].unit = goblin;

        // Initialize Human side
        board.tiles[0][4].unit = general;
        board.tiles[0][1].unit = general;
        board.tiles[1][0].unit = general;
        board.tiles[0][5].unit = swordsman;
        board.tiles[0][3].unit = swordsman;
        board.tiles[1][4].unit = swordsman;
        board.tiles[1][3].unit = swordsman;
        board.tiles[1][2].unit = swordsman;
        board.tiles[1][1].unit = swordsman;
    }

    private static String getUnitName(int index){
        if(index == 0) return(" ");
        if(index == 1) return("S");
        if(index == 2) return("E");
        if(index == 3) return("G");
        if(index == 4) return("O");
        return("WHAT?");
    }

    public static void writeDoubleArrayToFile(String filename, double[][] array) throws IOException {
        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter(filename));
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                outputWriter.write(i);
                outputWriter.write(",");
                outputWriter.write(j);
                outputWriter.write(" - ");
                outputWriter.write(Double.toString(array[i][j]));
                outputWriter.newLine();
            }
        }
        outputWriter.flush();  
        outputWriter.close();  
    }

    private static Integer[][] boardDrawData(Board board){
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