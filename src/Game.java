import java.util.Random;
import java.util.Scanner;

class Game {

    private static Random randomGenerator = new Random();

    private Scanner scan = new Scanner(System.in);

    /* board size in the game*/
    private static final int BOARD_SIZE = 4;

    /* Generating random number of gold elements up to MAX_GOLD*/
    private static final int NO_OF_GOLD = 3;

    private static final int NO_OF_PITS = 3;

    private static int playerScore = 0;

    /* Defining game elements */
    private Wumpus wumpus = new Wumpus();

    private GameItem[] gold = new Gold[NO_OF_GOLD];

    private GameItem[] pits = new Pit[NO_OF_PITS];

    private static int[] playerPosition = new int[2];

    private GameItem[][] board = new GameItem[BOARD_SIZE][BOARD_SIZE];

    /**
     * Generate random board positions from 0 to BOARD_SIZE-1.
     * @return row,column pair of board position
     */
    private static int[] generateRandom(){
        int[] coord = {randomGenerator.nextInt(BOARD_SIZE), randomGenerator.nextInt(BOARD_SIZE)};
        return coord;
    }

    /**
     * Check if row, colunm pair is valid or not based on the BOARD_SIZE
     * @param coord row, column pair to check if valid
     * @return coord true or not.
     */
    private boolean checkCoordinates(int[] coord){
        return (coord[0] >= 0 && coord[0] < BOARD_SIZE) && (coord[1] >= 0 &&  coord[1] < BOARD_SIZE);
    }

    /**
     * Position objects sent in as parameter at random positions on board
     * which are not yet filled or empty.
     * @param elems List of GameItem objects to be inserted in the board
     * @param count Count of the number of objects to be inserted from the list
     */
    private void fillElement(GameItem[] elems, int count){
        while(count > 0) {
            int[] coord = generateRandom();
            if(this.board[coord[0]][coord[1]] == null){
                --count;
                board[coord[0]][coord[1]] = elems[count];
            }
        }
    }

    /**
     * Function to move back on the board
     * @param position
     * @return New position after moving back
     */
    private int moveBack(int position){
        if(position == 0) return 3;
        else return --position;
    }

    /**
     * Function to move forward on the board
     * @param position
     * @return New position after moving forward
     */
    private int moveForward(int position){
        if(position == 3) return 0;
        else return ++position;
    }

    /**
     * Setting Board with all the elements and filling the rest with ClearGround object
     */
    private void setBoard(){
        //Wrapping wumpus into an array
        GameItem[] wumpusArray = {wumpus};

        // instantiating Gold objects
        for(int goldCount = 0;goldCount < NO_OF_GOLD; goldCount++){
            gold[goldCount] = new Gold();
        }

        // instantiating Pit objects
        for(int pitCount = 0;pitCount < NO_OF_PITS; pitCount++){
            pits[pitCount] = new Pit();
        }

        // Generating a random coordinate to position the player at
        int[] coord = generateRandom();
        int playerCounter = 1;

        int wumpusCount = 1;

        /* First we place wumpus in random location */
        fillElement(wumpusArray, wumpusCount);

        /* Placing gold in random locations*/
        fillElement(gold, NO_OF_GOLD);

        /* Placing pits in random locations*/
        fillElement(pits, NO_OF_PITS);

        /* Placing ClearGround objects in rest of the positions */
        for(int i=0;i<BOARD_SIZE;i++){
            for(int j=0;j<BOARD_SIZE;j++){
                if(board[i][j] == null){
                    board[i][j] = new ClearGround();
                }
            }
        }

        //deciding player position
        while(playerCounter > 0){
            if(board[coord[0]][coord[1]] instanceof ClearGround) {
                playerPosition = coord;
                --playerCounter;
            }
            coord = generateRandom();
        }
    }

    /**
     * Display the board
     */
    private void display(){
        for(int i=0;i<BOARD_SIZE;i++){
            for(int j=0;j<BOARD_SIZE;j++){
                if(i == playerPosition[0] && j == playerPosition[1]) System.out.print("| *");
                else System.out.print("| " + board[i][j].display());
            }
            System.out.println(" |");
        }
    }

    /**
     * Sensing the elements around the position provided in parameter
     * @param position Set of coordinates to sense elements around in [row,column] pair
     */
    private void senseNearBy(int[] position){
        System.out.println("Left: " + board[position[0]][moveBack(position[1])].sense);
        System.out.println("Right: " + board[position[0]][moveForward(position[1])].sense);
        System.out.println("Forward: " + board[moveBack(position[0])][position[1]].sense);
        System.out.println("Backward: " + board[moveForward(position[0])][position[1]].sense);
    }

    /**
     * Showing the menu choices to the player
     * @return Option value chosen by user
     */
    private int menu(){
        System.out.println("=====Wumpus====");
        System.out.println("1. Move player left");
        System.out.println("2. Move player right");
        System.out.println("3. Move player up");
        System.out.println("4. Move player down");
        System.out.println("5. Quit");
        int input = scan.nextInt();
        if(input >=1 && input <= 5){
            return input;
        }
        else{
            System.out.println("Sorry, the entered option is incorrect. Please try again");
            return menu();
        }
    }

    /**
     * Calculate the impact of player's last move and compute score
     */
    private void updatePosition() throws Exception{
        if(!checkCoordinates(playerPosition)){
            throw new Exception("Invalid player position");
        }
        else if(board[playerPosition[0]][playerPosition[1]] instanceof Wumpus){
            System.out.println("You were caught by Wumpus.");
            quit();
        }
        else if(board[playerPosition[0]][playerPosition[1]] instanceof Pit){
            System.out.println("You fell into a pit.");
            quit();
        }
        else if(board[playerPosition[0]][playerPosition[1]] instanceof Gold){
            System.out.println("You struck Gold");
            playerScore++;
            board[playerPosition[0]][playerPosition[1]] = new ClearGround();
            senseNearBy(playerPosition);
        }
        else senseNearBy(playerPosition);
        display();
    }

    /**
     * Display the final score of the player and quit the program
     */
    private void quit(){
        System.out.println("Your final score is " +
                playerScore +
                ".\nThank you for playing.");
        System.exit(0);
    }

    /**
     * Run the game by setting and displaying the board and taking user choices and
     * updating positions
     */
    public void runGame(){
        setBoard();
        display();
        while(true){
            int input = menu();
            try{
                switch(input){
                    case 1:
                        playerPosition[1] = moveBack(playerPosition[1]);
                        updatePosition();
                        break;
                    case 2:
                        playerPosition[1] = moveForward(playerPosition[1]);
                        updatePosition();
                        break;
                    case 3:
                        playerPosition[0] = moveBack(playerPosition[0]);
                        updatePosition();
                        break;
                    case 4:
                        playerPosition[0] = moveForward(playerPosition[0]);
                        updatePosition();
                        break;
                    case 5:
                        quit();
                        break;
                    default:
                        throw new Exception("Invalid user option encountered");
                }
            }catch (Exception e){
                System.out.println("Exception encountered " + e.toString());
                continue;
            }
        }
    }
}
