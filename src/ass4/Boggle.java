package ass4;
import java.util.*;
import java.io.*;

public class Boggle{
	String[][] board;
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final double[] FREQUENCIES = {
    		0.08167, 0.01492, 0.02782, 0.04253, 0.12703, 0.02228,
            0.02015, 0.06094, 0.06966, 0.00153, 0.00772, 0.04025,
            0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987,
            0.06327, 0.09056, 0.02758, 0.00978, 0.02360, 0.00150,
            0.01974, 0.00074
    };
    private static final String[] BOGGLE_1992 = {
            "LRYTTE", "VTHRWE", "EGHWNE", "SEOTIS",
            "ANAEEG", "IDSYTT", "OATTOW", "MTOICU",
            "AFPKFS", "XLDERI", "HCPOAS", "ENSIEU",
            "YLDEVR", "ZNRNHL", "NMIQHU", "OBBAOJ"
    };
    private static final String[] BOGGLE_BIG = {
            "AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM",
            "AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCENST",
            "CEIILT", "CEILPT", "CEIPST", "DDHNOT", "DHHLOR",
            "DHLNOR", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU",
            "FIPRSY", "GORRVW", "IPRRRY", "NOOTUW", "OOOTTU"
    };

    public Boggle(int N){
    	if(N < 2){
            throw new IllegalArgumentException("Boggle can only be played with a board size of at least 2x2. Try a larger number.");
        }
        this.board = new String[N][N];
        Random random = new Random();
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                double randomValue = random.nextDouble();
                double cumulativeValue = 0.0;
                for(int k = 0; k < ALPHABET.length(); k++){
                    cumulativeValue += FREQUENCIES[k];
                    if(randomValue < cumulativeValue){
                    	char randomLetter = ALPHABET.charAt(k);
                    	this.board[i][j] = String.valueOf(randomLetter);
                    	break;
                    }
                }
            }
        }
    }
    
    public Boggle(String[][] board){
        int rows = board.length;
        int columns = board[0].length;
        for(int i = 0; i < rows; i++){
            for(int j=0;j<columns;j++){
                board[i][j]=board[i][j].trim().toLowerCase();
            }
        }
        this.board = board;
    }
    
    public Boggle(String[] dice){
        Random random = new Random();
        for(int i = dice.length - 1; i > 0; i--){
            int j = random.nextInt(i+1);
            String temp = dice[i];
            dice[i] = dice[j];
            dice[j] = temp;
        }
        int rows;
        int columns;
        if(dice.length / 4 == 4){
        	rows = 4;
        	columns = 4;
    	}
        else{
        	rows = 5;
        	columns = 5;
        }
        board = new String[rows][columns];
        int diceIndex = 0;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                int letter = random.nextInt(6);
                board[i][j]=String.valueOf(dice[diceIndex].charAt(letter));
                diceIndex++;
            }
        }
    }
    
    public Boggle(String filename){
    	try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String[] sizes = reader.readLine().trim().split(" ");
            int rows = Integer.parseInt(sizes[0]);
            int columns = Integer.parseInt(sizes[1]);
            board = new String[rows][columns];
            for(int i = 0; i < rows; i++){
                String[] letters = reader.readLine().trim().split(" ");
                for (int j = 0; j < columns; j++){
                    board[i][j] = letters[j];
                }
            }
            reader.close();
        }
    	catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        StringBuilder string = new StringBuilder();
        int rows = board.length;
        int columns = board[0].length;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                string.append(board[i][j]).append(" ");
            }
            string.append("\n");
        }
        return string.toString().toLowerCase();
    }

    public Boggle(String[] dice, long seed){
    	Random random = new Random(seed);
        for(int i = dice.length - 1; i > 0; i--){
            int j = random.nextInt(i + 1);
            String temp = dice[i];
            dice[i] = dice[j];
            dice[j] = temp;
        }
        int rows;
        int columns;
        if(dice.length == 4){
        	rows = 4;
        	columns = 4;
    	}
        else{
        	rows = 5;
        	columns = 5;
        }
        board = new String[rows][columns];
        
        if(dice.length < rows * columns) {
        	throw new IllegalArgumentException("Not enough dice for this board size");
        }
        
        int diceIndex = 0;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                int letter = random.nextInt(6);
                board[i][j] = String.valueOf(dice[diceIndex].charAt(letter));
                diceIndex++;
            }
        }
    }

    public boolean matchWord(String word){
    	boolean[][] checked;
    	checked = new boolean[board.length][board[0].length];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if(backTrack(word, i, j, 0, checked)){
                	for (int k = 0; k < checked.length; k++){
                        Arrays.fill(checked[k], false);
                	}
                    return true;
                }
            }
        }
        for (int k = 0; k < checked.length; k++){
            Arrays.fill(checked[k], false);
        }
        return false;
    }
    
    private boolean backTrack(String word, int row, int column, int index, boolean[][] checked){
    	if(index == word.length()){
    		return true;
    	}
    	int rows = board.length;
        int columns = board[0].length;
    	if(row >= 0 && row < board.length && column >= 0 && column < board[row].length && !checked[row][column] && board[row][column].equalsIgnoreCase(String.valueOf(word.charAt(index)))){
    		int[] xCoord = {-1, 0, 1, -1, 1, -1, 0, 1};
    		int[] yCoord={-1, -1, -1, 0, 0, 1, 1, 1};
    		checked[row][column] = true;
    		for(int i = 0; i < xCoord.length; i++){
    			int xCoordNext = row + xCoord[i];
    			int yCoordNext = column + yCoord[i];
    			if (xCoordNext >= 0 && xCoordNext < rows && yCoordNext >= 0 && yCoordNext < columns && backTrack(word, xCoordNext, yCoordNext, index + 1, checked)){
    				return true;
    			}
    		}
    		checked[row][column]=false;
    	}
    	return false;
    }

    public static List<String> getAllValidWords(String dictionary, String board){
    	List<String> validWords = new ArrayList<>();
    	List<String> lines = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(dictionary))){
            String line;
            while((line = reader.readLine())!=null){
                lines.add(line.trim());
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        Boggle boggle = new Boggle(board);
        for(String word : lines){
        	if(word.length() >= 3) {
	            if(boggle.matchWord(word)){
	                validWords.add(word);
	            }
        	}
        }
        return validWords;
    }
    
    public static List<String> getAllValidWords(String dictionary, Boggle boggle){
        List<String> validWords = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(dictionary))){
            String line;
            while((line = reader.readLine())!= null){
                lines.add(line.trim());
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        for(String word : lines){
            if(boggle.matchWord(word)){
                validWords.add(word);
            }
        }
        return validWords;
    }

    public static void main(String[] args){
    	List<String> validWords;
    	Scanner userInput = new Scanner(System.in);
    	System.out.println("Hi! Welcome to Boggle. Before you start playing, please enter the name of a board file.");
        String boardFile = userInput.nextLine();
        File file = new File(boardFile);
        while(!file.exists()){
            System.out.println("File doesn't exist! Try again.");
            boardFile = userInput.nextLine();
            file = new File(boardFile);
        }
        System.out.println("Great, now please enter the name of a dictionary file.");
        String dictionaryFile = userInput.nextLine();
        file = new File(dictionaryFile);
        while(!file.exists()){
            System.out.println("File doesn't exist! Try again.");
            dictionaryFile = userInput.nextLine();
            file = new File(dictionaryFile);
        }
        
        System.out.println("You are all set! Welcome to Boggle!");
        System.out.println("How would you like to generate the Boggle board?");
        System.out.println("1. Load board from file");
        System.out.println("2. Create a new board using a set of dice");
        System.out.println("3. Create a custom board of NxN dimensions");
        System.out.println("To make your selection, type \"1\", \"2\" or \"3\" correspondingly.");
        
        int boardChoice = userInput.nextInt();
        boolean survey = false;
        while(boardChoice < 1 || boardChoice > 3){
        	System.out.println("Invalid choice. To make your selection, type \"1\", \"2\" or \"3\" correspondingly.");
            boardChoice = userInput.nextInt();
        }
        
        Boggle boggle;
        switch (boardChoice){
            case 1:
                boggle = new Boggle(boardFile);
                validWords = getAllValidWords(dictionaryFile,boardFile);
                survey = true;
                break;
            case 2:
                System.out.println("These are the available sets of dices to play with: ");
                System.out.println("1. Boggle 1992; The original 16 dices from 1992's Boggle");
                System.out.println("2. Boggle Big; The 25 dices for the Big Boggle variant");
                System.out.println("To make your selection, type \"1\" or \"2\" correspondingly.");
                
                int diceChoice = userInput.nextInt();
                while(diceChoice < 1 || diceChoice > 2){
                	System.out.println("Invalid choice. To make your selection, type \"1\" or \"2\" correspondingly.");
                	diceChoice = userInput.nextInt();
                }
                switch(diceChoice){
                case 1:
                	boggle = new Boggle(BOGGLE_1992);
                	validWords = getAllValidWords(dictionaryFile,boggle);
                	break;
                case 2:
                	boggle = new Boggle(BOGGLE_BIG);
                	validWords = getAllValidWords(dictionaryFile,boggle);
                	break;
                default:
                	System.out.println("Invalid choice. To make your selection, type \"1\" or \"2\" correspondingly.");
                	return;
                }
                break;
            case 3:
                System.out.print("Enter dimension N for the custom board. Please note that you need at least a 2x2 board in order to play Boggle.");
                int custom = userInput.nextInt();
                boggle = new Boggle(custom);
                validWords = getAllValidWords(dictionaryFile,boggle);
                break;
            default:
                System.out.println("Invalid choice. To make your selection, type \"1\", \"2\" or \"3\" correspondingly.");
                return;
        }
        int userScore = 0;
        do{
            System.out.println("\nCurrent Board:");
            System.out.println(boggle.toString());
            System.out.print("Enter a word (or \"1\" to terminate the game):");
            String userWord = userInput.next();
            if(userWord.equals("1")){
                break;
            }
            if(validWords.contains(userWord.toUpperCase())){
	            if(boggle.matchWord(userWord)){
	                int wordScore;
	                int wordLength=userWord.length();
	                if(wordLength == 3){
	                	wordScore = 1;
	                }
	                else if(wordLength == 4){
	                	wordScore = 2;
	                }
	                else if(wordLength == 5){
	                	wordScore = 3;
	                }
	                else{
	                	wordScore = wordLength - 2;
	                }
	                userScore += wordScore;
	                System.out.println("Success! Your score for '" + userWord + "' is " + wordScore + ". Total score: " + userScore);
	            }
	            else{
	                System.out.println("Sorry, '" + userWord + "' is not a valid word on the board.");
	            }
            }
            else{
                System.out.println("Sorry, '" + userWord + "' is not a valid word on either the board or dictionary.");
            }
        }while(true);
        if(survey == true){
	        System.out.print("Do you want to find out the maximum number of possible words for this board and dictionary? Type \"Yes\" if so.");
	        String maxChoice = userInput.next();
	        if(maxChoice.equalsIgnoreCase("yes")){
	            List<String> allValidWords = getAllValidWords(dictionaryFile,boardFile);
	            System.out.println("The maximum number of possible words with this board is: " + allValidWords.size());
	        }
    	}
        else{
        	System.out.print("Do you want to find out the maximum number of possible words for this board and dictionary? Type \"yes\" if so.");
	        String maxChoice = userInput.next();
	        if(maxChoice.equalsIgnoreCase("yes")){
	            List<String> allValidWords = getAllValidWords(dictionaryFile, boggle);
	            System.out.println("The maximum number of possible words with this board is: " + allValidWords.size());
	        }
        }
        System.out.println("Thanks for playing Boggle! See you next time!");
        userInput.close();
    }
}


