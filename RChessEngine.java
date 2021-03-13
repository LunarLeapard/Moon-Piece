/*
This program will randomly suggest the user to move a chess piece in a random way, but still
abiding to the chess rules.  

The user enters any dead pieces that the program will remember.  
The user enters any pieces that cannot move this turn, which the program will forget
after the program gives the user instructions for this turn.  
The program tells the user what piece the user should move, making sure not to choose
any piece that is dead or stuck for this round.  
After a piece is chosen, the program will ask relevant questions about the surroundings
of that piece to get all the possible moves that piece can make.  (ie. "How many empty spaces are in 
front/behind/left/right of this piece?", or "If applicable, which ENEMY piece is at the end of this
path?(/e if not applicable)")
The program will output its final decision on where the piece should move.  The user will have
the option to run the program for the next turn

For any questions in which the user does not have an answer for "/e" will skip it.  "/e" also ends
the user's response to questions that allow multiple entries.  

Anytime, the user is free to type in any of the following commands: 
/transform
The user enters the name of a piece on the chessboard and enters another name to rename it*
/revive
The user enters the name of a piece that is dead and the program will make it alive again.  
/help
Will print out the names of all the pieces on the chessboard along with each piece's status
as alive or dead.  

*renaming can change the moving rules of the piece as well.  The program uses the following 
indicators to determine how a piece should move: 
Starts with p - moves like a pawn
Starts with r - moves like rook
Starts with h - moves like horse
Starts with b - moves like bishop
Starts with q - moves like queen
None of the above - moves like king
*/
import java.util.*;

public class RChessEngine{
	public static String[] CBOARD = {"pawn1", "pawn2", "pawn3", "pawn4", "pawn5", "pawn6", "pawn7", "pawn8", 
		"rookL", "horseL", "bishopL", "queen", "king", "bishopR", "horseR", "rookR"};	//An array with all the piece's names
	public static int BOARDSIZE = 8;	//The dimension of one side of a square board
	// public static String[] CBOARD = {"king"};

	public static void main(String[] args){
		Scanner console = new Scanner(System.in);
		Random generator = new Random();
		intro();	//Just prints intro
		boolean[] cArray = new boolean[CBOARD.length];	
		//cArray (chess Array) tells whether the piece at index i is alive(true) or dead(false)
		Arrays.fill(cArray, true);
		String cont = "y";	//continue
		String[] yOrN = {"y", "n"};	//yes or no array (needed for pUFPiece method)
		String[] commands = {"/transform", "/revive", "/help"};	//stores possible commands
		while(cont.equalsIgnoreCase("y")){
			negatePieces("Enter your dead pieces: \n", cArray, false, commands, cArray, console);
			if(!hasTrue(cArray)){	//if no pieces are alive
				System.out.println("Oh No! You are out of pieces!");
				break;
			}
			String pieceMoved = gRPiece(cArray, commands, console, generator);	//get Random Piece
			if(pieceMoved.equalsIgnoreCase("none")){	//if there are no pieces to move, 
				//skip this turn and prompt user for the next turn
				cont = pUFPiece("Another move?(y/n)\n", yOrN, commands, cArray, console);
				continue;
			}//otherwise, a piece will be able to move
			System.out.println("Prepare to move " + pieceMoved);	
			System.out.println(movePiece(pieceMoved, commands, cArray, console, generator));
			cont = pUFPiece("Another move?(y/n)\n", yOrN, commands, cArray, console);
		}
		System.out.println("Exiting.");
	}

	//The task-specific methods

	//prints intro
	public static void intro(){
		System.out.println("Your Chessboard Looks Like This: ");
		prntSpcdArr(CBOARD, lgthstTrm(CBOARD)+1);//prints formatted array with each term padded to the length of longest term + 1
		System.out.println("Use \"/e\" when you are done answering a question with multiple answers");
		System.out.println("or to skip a question\n");
		System.out.println("The commands are: ");
		System.out.println("\"/transform\" to rename a piece");
		System.out.println("\"/revive\" to make a dead piece alive");
		System.out.println("\"/help\" to view the names of all your pieces and their status(alive/dead)");
	}
	//method gRPiece(get Random Piece) returns a random piece from available, not stuck, not dead
	//pieces when given an array telling it which pieces are dead
	public static String gRPiece(boolean[] cArray, String[] commands, Scanner console, Random generator){
		boolean[] cMovePieces = Arrays.copyOf(cArray, cArray.length);	//makes a copy so that original array is not altered by next line of code
		negatePieces("Which pieces are alive, but unable to move this turn?\n", cMovePieces, false, commands, cArray, console);
		if(!hasTrue(cMovePieces)){	//if all pieces are either dead or can't move
			System.out.println("Oh No! You are stuck!");
			return "none";
		}
		int piecesAlive = cPossibleChoices(cMovePieces);	//count choices available (count "true" values in array)
		String[] movePieces = buildArray(cMovePieces, piecesAlive);	
		//build a new array including only the names of the pieces that can move
		return movePieces[generator.nextInt(piecesAlive)];	//then pick a random piece from that array
	}
	//will return a string of directions on where to move the specified piece
	public static String movePiece(String pieceMoved, String[] commands, boolean[] cArray, Scanner console, Random generator){
		if(pieceMoved.charAt(0)=='p'){
			return mPawn(pieceMoved, commands, cArray, console, generator);	//move pawn
		} else if(pieceMoved.charAt(0)=='r'){
			return mRook(pieceMoved, commands, cArray, console, generator);
		} else if(pieceMoved.charAt(0)=='h'){
			return mHorse(pieceMoved, commands, cArray, console, generator);
		} else if(pieceMoved.charAt(0)=='b'){
			return mBishop(pieceMoved, commands, cArray, console, generator);
		} else if(pieceMoved.charAt(0)=='q'){
			return mQueen(pieceMoved, commands, cArray, console, generator);
		} else {
			return mKing(pieceMoved, commands, cArray, console, generator);
		}
	}
	//method mPawn (move Pawn) returns the directions for the random movement of a pawn
	public static String mPawn(String pieceMoved, String[] commands, boolean[] cArray, Scanner console, Random generator){
		String[][] pawnArr = mPwnArry(pieceMoved, 2, commands, cArray, console);
		//pawnArr stores all the moves the pawn can make. Each index stores an array of length 2 containing
		//{number of tiles moved, direction moved (with optional killing clause)}
		if(pawnArr.length==0){
			return "Error: No places to move";
		}
		int index = generator.nextInt(pawnArr.length);
		return "Move " + pieceMoved + " " + pawnArr[index][0] + " space(s) " + pawnArr[index][1];
	}
	//method mRook (move Rook) returns the directions for the random movement of a rook
	public static String mRook(String pieceMoved, String[] commands, boolean[] cArray, Scanner console, Random generator){
		String[][] rookArr = mStraightArry(pieceMoved, BOARDSIZE-1, commands, cArray, console);
		//rookArr stores all the moves the rook can make. Each index stores an array of length 2 containing
		//{number of tiles moved, direction moved (with optional killing clause)}
		if(rookArr.length==0){
			return "Error: no places to move";
		}
		int index = generator.nextInt(rookArr.length);
		return "Move " + pieceMoved + " " + rookArr[index][0] + " space(s) " + rookArr[index][1];
	}
	//method mHorse (move Horse) returns the directions for random movement of a horse
	public static String mHorse(String pieceMoved, String[] commands, boolean[] cArray, Scanner console, Random generator){
		String[] horseMoves = {"FL", "FR", "RF", "RB", "BR", "BL", "LB", "LF"};	//all 8 possible tiles horse can move to
		String[][] direction = pUFArray("Identify all the empty spaces " + pieceMoved + " can move to. \n"+
			"Enter your response as two directions (four directions total: F, L, R, B)\nFor example BL is two spaces backward, one space left\n", "", false, horseMoves, commands, cArray, console);
		//direction holds all the blank places the horse can move to.  Each index stores an array of length 2 containing
		//{direction moved, ""}
		String[][] edirection = pUFArray("Identify all the enemy-occupied spaces " + pieceMoved + " can move to \n", "Which enemy piece is in this position?\n", true, horseMoves, commands, cArray, console);
		//edirection (enemy direction) holds all the enemy-occupied places the horse can move to.  Each index stores
		//an array of length 2 containing {direction moved, name of enemy piece}
		String [][] horseArr = catnate(direction, edirection);
		//horseArr stores all the moves the horse can make. Each index stores an array of length 2 containing
		//{direction moved, name of enemy piece} by combining direction and edirection
		if(horseArr.length==0){
			return "Error: no places to move";
		}
		int index = generator.nextInt(horseArr.length);
		return "Move " + pieceMoved + " " + unshorten(horseArr[index][0]) + " " + horseArr[index][1];
	}
	//method mBishop (move Bishop) returns the directions for random movement of a Bishop
	public static String mBishop(String pieceMoved, String[] commands, boolean[] cArray, Scanner console, Random generator){
		String[][] bishopArr = mDiagonalArry(pieceMoved, BOARDSIZE-1, commands, cArray, console);
		//bishopArr stores all the moves the bishop can make. Each index stores an array of length 2 containing
		//{number of tiles moved, direction moved (with optional killing clause)}
		if(bishopArr.length==0){
			return "Error: no places to move";
		}
		int index = generator.nextInt(bishopArr.length);
		return "Move " + pieceMoved + " " + bishopArr[index][0] + " space(s) " + bishopArr[index][1];
	}
	//method mQueen (move Queen) returns the directions for random movement of a Queen
	public static String mQueen(String pieceMoved, String[] commands, boolean[] cArray, Scanner console, Random generator){
		String[][] qStrghtArr = mStraightArry(pieceMoved, BOARDSIZE-1, commands, cArray, console);
		//qStrghtArr contains all the straight moves the queen can make
		String[][] qDiagArr = mDiagonalArry(pieceMoved, BOARDSIZE-1, commands, cArray, console);
		//qDiagArr contains all the diagonal moves the queen can make
		String[][] queenArr = catnate(qStrghtArr, qDiagArr);
		//queenArr stores all the moves the queen can make. Each index stores an array of length 2 containing
		//{number of tiles moved, direction moved (with optional killing clause)}
		if(queenArr.length==0){
			return "Error: no places to move";
		}
		int index = generator.nextInt(queenArr.length);
		return "Move " + pieceMoved + " " + queenArr[index][0] + " space(s) " + queenArr[index][1];
	}
	//method mKing (move King) returns the directions for random movement of a King
	public static String mKing(String pieceMoved, String[] commands, boolean[] cArray, Scanner console, Random generator){
		String[][] kStrghtArr = mStraightArry(pieceMoved, 1, commands, cArray, console);
		//kStrghtArr contains all the straight moves the king can make
		String[][] kDiagArr = mDiagonalArry(pieceMoved, 1, commands, cArray, console);
		//kDiagArr contains all the diagonal moves the king can make
		String[][] kingArr = catnate(kStrghtArr, kDiagArr);
		//kingArr stores all the moves the king can make. Each index stores an array of length 2 containing
		//{number of tiles moved, direction moved (with optional killing clause)}
		if(kingArr.length==0){
			return "Error: no places to move";
		}
		int index = generator.nextInt(kingArr.length);
		return "Move " + pieceMoved + " " + kingArr[index][0] + " space(s) " + kingArr[index][1];
	}
	//method mPwnArry (make Pawn Array)
	//asks the user to survey all the squares relevant to a pawn and assembles and returns an array
	//of possible moves
	public static String[][] mPwnArry(String pName, int lmt, String[] commands, boolean[] cArray, Scanner console){
		int front = pUFIntBnd("How many spaces directly in front of " + pName + " can this piece move to? \n", lmt, commands, cArray, console);	//prompts user for reasonable integer
		String lfpiece = pUFAPiece("If applicable, which ENEMY piece is diagonally up 1 and left 1 to this piece?(/e if not applicable)\n", commands, cArray, console);
		//prompts user for name of enemy piece
		String rfpiece = pUFAPiece("If applicable, which ENEMY piece is diagonally up 1 and right 1 to this piece?(/e if not applicable)\n", commands, cArray, console);
		boolean ilfpiece = !lfpiece.equalsIgnoreCase("/e"); //is there a left-front piece?
		boolean irfpiece = !rfpiece.equalsIgnoreCase("/e"); //is there a right-front piece?
		int length = front + bToInt(ilfpiece) + bToInt(irfpiece);	//represents the length of the array of possible moves
		if(length==0){
			return new String[0][0];	//empty array
		}
		String[] direction = new String[length];	// each element represents direction (with optional killing clause)
		int[] spaces = new int[length];	//each element is a number representing how many spaces to move
		int prev = 0;	//keeps track of the current index as array is transversed and filled
		updateArray(spaces, prev, prev+front);
		//the array spaces gets partly filled with all the numbers of front spaces the piece can move to
		//For example, for front=2, spaces = {1, 2, . . .}
		prev = updateArray(direction, prev, prev+front, "forward");	//update array will return an updated value of prev (in this case prev+front)
		updateArray(spaces, prev, prev+bToInt(ilfpiece));
		prev = updateArray(direction, prev, prev+bToInt(ilfpiece), "diagonally front and left and eat your enemy's " + lfpiece);
		updateArray(direction, prev, length, "diagonally front and right and eat your enemy's " + rfpiece);
		updateArray(spaces, prev, length);
		return layOn(spaces, direction); //concatenates array not end to end, but side to side
	}
	//method mStraightArry (make straight array)
	//asks the user to survey all the squares along the straight up/down/left/right
	//directions and assembles and returns an array of possible moves
	public static String[][] mStraightArry(String pName, int lmt, String[] commands, boolean[] cArray, Scanner console){
		int front = pUFIntBnd("How many empty spaces are in front of this " + pName + "? \n", lmt, commands, cArray, console);	//get reasonable integer
		String fpiece = pUFAPieceCond("If applicable, which ENEMY piece is at the end of these front blank spaces?(/e if not applicable)\n", front, lmt, commands, cArray, console); 
		//if number of front blank spaces (stored in variable "front") is less than lmt, get name of enemy piece that is end of front blank spaces
		boolean ifpiece = !fpiece.equalsIgnoreCase("/e");	//is there a front piece?
		int left = pUFIntBnd("How many empty spaces are left of the " + pName + "? \n", lmt, commands, cArray, console);
		String lpiece = pUFAPieceCond("If applicable, which ENEMY piece is at the end of the left blank spaces?(/e if not applicable)\n", left, lmt, commands, cArray, console);
		boolean ilpiece = !lpiece.equalsIgnoreCase("/e");	//is there a left piece?
		int right = pUFIntBnd("How many empty spaces are right of the " + pName + "? \n", minimum(BOARDSIZE-1-left, lmt), commands, cArray, console);
		//there is no way for right to be greater than BOARDSIZE-1-left
		String rpiece = pUFAPieceCond("If applicable, which ENEMY piece is at the end of the right blank spaces?(/e if not applicable)\n", right, minimum(BOARDSIZE-1-left, lmt), commands, cArray, console);
		boolean irpiece = !rpiece.equalsIgnoreCase("/e");	//is there a right piece?
		int behind = pUFIntBnd("How many empty spaces are behind the " + pName + "?\n", minimum(BOARDSIZE-1-front, lmt), commands, cArray, console);
		String bpiece = pUFAPieceCond("If applicable, which ENEMY piece is at the end of the back blank spaces?(/e if not applicable)\n", behind, minimum(BOARDSIZE-1-front, lmt), commands, cArray, console);
		boolean ibpiece = !bpiece.equalsIgnoreCase("/e");	//is there a behind piece?
		int length = front + bToInt(ifpiece) + left + bToInt(ilpiece) + right + bToInt(irpiece) + behind + bToInt(ibpiece);
		if(length==0){
			return new String[0][0];
		}
		int[] spaces = new int[length];	// each element represents direction (with optional killing clause)
		String[] direction = new String[length];	//each element is a number representing how many spaces to move
		int prev = 0; //keeps track of the current index as array is transversed and filled
		updateArray(spaces, prev, prev+front+bToInt(ifpiece));
		prev = updateArray(direction, prev, prev+front, "forward");
		prev = updateArray(direction, prev, prev+bToInt(ifpiece), "forward and take your enemy's " + fpiece);
		updateArray(spaces, prev, prev+left+bToInt(ilpiece));
		prev = updateArray(direction, prev, prev+left, "left");
		prev = updateArray(direction, prev, prev+bToInt(ilpiece), "left and take your enemy's " + lpiece);
		updateArray(spaces, prev, prev+right+bToInt(irpiece));
		prev = updateArray(direction, prev, prev+right, "right");
		prev = updateArray(direction, prev, prev+bToInt(irpiece), "right and take your enemy's " + rpiece);
		updateArray(spaces, prev, prev+behind+bToInt(ibpiece));
		prev = updateArray(direction, prev, prev+behind, "backward");
		updateArray(direction, prev, prev+bToInt(ibpiece), "backward and take your enemy's " + bpiece);
		return layOn(spaces, direction);//concatenates array not end to end, but side to side
	}
	//method mDiagonalArry (make Diagonal Array) 
	//asks the user to survey all the squares along the diagonals and assembles and returns an array
	//of possible moves
	public static String[][] mDiagonalArry(String pName, int lmt, String[] commands, boolean[] cArray, Scanner console){
		int lfront = pUFIntBnd("How many empty spaces are diagonally left-front of this " + pName + "?\n", lmt, commands, cArray, console);
		String lfpiece = pUFAPieceCond("If applicable, enter the name of the enemy piece at the end of this diagonal left-front path\n", lfront, lmt, commands, cArray, console);
		boolean ilfpiece = !lfpiece.equalsIgnoreCase("/e");	//is there a left-front piece?
		int rfront = pUFIntBnd("How many empty spaces are diagonally right-front of this " + pName + "?\n", lmt, commands, cArray, console);
		String rfpiece = pUFAPieceCond("If applicable, enter the name of the enemy piece at the end of this diagonal right-front path\n", rfront, lmt, commands, cArray, console);
		boolean irfpiece = !rfpiece.equalsIgnoreCase("/e"); //is there a right-front piece?
		int lbehind = pUFIntBnd("How many empty spaces are diagonally left-behind of this " + pName + "?\n", minimum(BOARDSIZE-1-rfront, lmt), commands, cArray, console);
		String lbpiece = pUFAPieceCond("If applicable, enter the name of the enemy piece at the end of this diagonal left-behind path\n", lbehind, minimum(BOARDSIZE-1-rfront, lmt), commands, cArray, console);
		boolean ilbpiece = !lbpiece.equalsIgnoreCase("/e");	//is there a lift-behind piece?
		int rbehind = pUFIntBnd("How many empty spaces are diagonally right-behind of this " + pName + "?\n", minimum(BOARDSIZE-1-lfront, lmt), commands, cArray, console);
		String rbpiece = pUFAPieceCond("If applicable, enter the name of the enemy piece at the end of this diagonal right-behind path\n", rbehind, minimum(BOARDSIZE-1-lfront, lmt), commands, cArray, console);
		boolean irbpiece = !rbpiece.equalsIgnoreCase("/e");	//is there a right-behind piece?
		int length = lfront + bToInt(ilfpiece) + rfront + bToInt(irfpiece) + lbehind + bToInt(ilbpiece) + rbehind + bToInt(irbpiece);
		if(length==0){
			return new String[0][0];
		}
		int[] spaces = new int[length];// each element represents direction (with optional killing clause)
		String[] direction = new String[length];//each element is a number representing how many spaces to move
		int prev = 0; //keeps track of the current index as array is transversed and filled
		updateArray(spaces, prev, prev+lfront+bToInt(ilfpiece));
		prev = updateArray(direction, prev, prev+lfront, "diagonally left-forward");
		prev = updateArray(direction, prev, prev+bToInt(ilfpiece), "diagonally left-forward and take your enemy's " + lfpiece);
		updateArray(spaces, prev, prev+rfront+bToInt(irfpiece));
		prev = updateArray(direction, prev, prev+rfront, "diagonally right-forward");
		prev = updateArray(direction, prev, prev+bToInt(irfpiece), "diagonally right-forward and take your enemy's " + rfpiece);
		updateArray(spaces, prev, prev+lbehind+bToInt(ilbpiece));
		prev = updateArray(direction, prev, prev+lbehind, "diagonally left-backward");
		prev = updateArray(direction, prev, prev+bToInt(ilbpiece), "diagonally left-backward and take your enemy's " + lbpiece);
		updateArray(spaces, prev, prev+rbehind+bToInt(irbpiece));
		prev = updateArray(direction, prev, prev+rbehind, "diagonally right-backward");
		updateArray(direction, prev, prev+bToInt(irbpiece), "diagonally right-backward and take your enemy's " + rbpiece);
		return layOn(spaces, direction);//concatenates array not end to end, but side to side
	}
	//given a command, this program will execute it
	public static void runCommand(String[] commands, String command, boolean[] cArray, Scanner console){
		int cmdNumber = bnrySearch(commands, command);
		if(cmdNumber==0){
			transform(commands, cArray, console);
		}
		if(cmdNumber==1){
			revive(commands, cArray, console);
		}
		if(cmdNumber==2){
			help(cArray, console);
		}
	}
	//performs the /transform command, which renames a piece from the array CBOARD
	public static void transform(String[] commands, boolean[] cArray, Scanner console){
		String oldPiece = pUFPiece("Which piece is undergoing transformation?\n", CBOARD, commands, cArray, console);
		if(!oldPiece.equalsIgnoreCase("/e")){//if not "/e"
			int index = bnrySearch(CBOARD, oldPiece);
			String nwPiece = pUFPieceR("Which piece is it now?\n", CBOARD, commands, cArray, console);
			if(!nwPiece.equalsIgnoreCase("/e")){//if not "/e"
				CBOARD[index] = nwPiece;
				System.out.println("Command successful. ");
			} else {
				System.out.println("Command discontinued.  ");
			}
		} else {
			System.out.println("Command discontinued. ");
		}
	}
	//performs the /revive command, which turns a dead piece alive
	public static void revive(String[] commands, boolean[] cArray, Scanner console){
		negatePieces("Enter all the pieces you would like to revive: \n", cArray, true, commands, cArray, console);
	}
	//performs the /help command, which prints all the pieces with their status(alive/dead)
	public static void help(boolean[] cArray, Scanner console){
		String[] helpArr = new String[CBOARD.length];	//new help array will have elements like: [piece(dead/alive)]
		for(int i=0; i<CBOARD.length; i++){
			helpArr[i] = CBOARD[i] + "(" + dOAlv(cArray[i]) + ")";
		}
		prntSpcdArr(helpArr, lgthstTrm(helpArr)+1);	//prints formatted array with each term padded to the length of longest term + 1
	}

	//The prompt user methods

	//pUFIntBnd (prompt User For Integer Bound) prompts user for a number from 0-limit
	public static int pUFIntBnd(String prompt, int limit, String[] commands, boolean[] cArray, Scanner console){
		System.out.print(prompt);
		int ans = pUFInt("", commands, cArray, console);	//prompt user for int
		while(0>ans || ans>limit){//while number is outside acceptable range
			System.out.println("Input must be an integer from 0-" + limit + ", please try again: ");
			ans = pUFInt("", commands, cArray, console);
		}
		return ans;
	}
	//pUFInt (prompt User For Integer) prompts user for any number.  Can call upon runCommand to execute commands.  
	public static int pUFInt(String prompt, String[] commands, boolean[] cArray, Scanner console){
		System.out.print(prompt);
		while(!console.hasNextInt()){
			System.out.println("Input must be an integer, please try again: ");
			String trash = console.next();
			if(isPiece(commands, trash)){	//if the string is a command,
				runCommand(commands, trash, cArray, console);
			}
		}
		int ans = console.nextInt();
		return ans;
	}
	//pUFPiece (prompt User For Piece) prompts user for a string inside the specified array. 
	public static String pUFPiece(String prompt, String[] array, String[] commands, boolean[] cArray, Scanner console){
		System.out.print(prompt);
		String piece = pUFAPiece("", commands, cArray, console);	//prompt user for any piece
		while(!piece.equalsIgnoreCase("/e") && !isPiece(array, piece)){	//while piece is not /e nor is in the array, the user will have to enter another value
			System.out.println("Sorry, not recognized.  Try again.");
			piece = pUFAPiece("", commands, cArray, console);
		}
		return piece;//In the daylight...this has been coded
	}
	//pUFPieceR (prompt User For Piece Reverse) prompts user for a string outside the specified array
	public static String pUFPieceR(String prompt, String[] array, String[] commands, boolean[] cArray, Scanner console){
		System.out.print(prompt);//JSC=???
		String piece = pUFAPiece("", commands, cArray, console);	//prompt user for any piece
		while(!piece.equalsIgnoreCase("/e") && isPiece(array, piece)){	//while piece is not /e or is in the array, the user will have to enter another value
			System.out.println("Sorry, that piece already exists.  Try again: ");
			piece = pUFAPiece("", commands, cArray, console);
		}
		return piece;
	}
	//pUFAPiece (prompt User For Any Piece) prompts user for any string.  Can call upon runCommand method
	public static String pUFAPiece(String prompt, String[] commands, boolean[] cArray, Scanner console){
		System.out.print(prompt);
		String piece = console.next();
		while(isPiece(commands, piece)){	//while piece is a command
			runCommand(commands, piece, cArray, console);	//run command
			piece = console.next();	//get another string
		}
		return piece;//To its death
	}
	//pUFAPieceCond (prompt User For Any Piece Conditional) will prompt user for any string only if num<lmt
	public static String pUFAPieceCond(String prompt, int num, int lmt, String[] commands, boolean[] cArray, Scanner console){
		if(num<lmt){
			return pUFAPiece(prompt, commands, cArray, console);
		}
		return "/e";	//otherwise returns "/e"
	}
	//pUFArray (prompt User For Array) - returns an array with each element contianing a String array of 
	//length two.  If fillscnd is false, the 2nd element will be automatically filled
	//with "".  If fillscnd is true, then the 2nd element will be filled with the kill clause
	public static String[][] pUFArray(String prompt1, String prompt2, boolean fillscnd, String[] array, String[] commands, boolean[] cArray, Scanner console){
		System.out.print(prompt1);
		String[][] userArray = new String[0][2];
		String taster = pUFPiece("", array, commands, cArray, console);	//prompt user for an element in "array"
		while(!taster.equalsIgnoreCase("/e")){	//while taster is not yet "/e"
			userArray = Arrays.copyOf(userArray, userArray.length+1);	//grows userArray to include extra element
			userArray[userArray.length-1] = new String[]{"",""};	//make that extra element
			userArray[userArray.length-1][0] = taster;	//fill the first string in that array
			if(fillscnd){
				userArray[userArray.length-1][1] = "and eat your enemy's " + pUFAPiece(prompt2, commands, cArray, console);
			} else {
				userArray[userArray.length-1][1] = "";
			}
			taster = pUFPiece("", array, commands, cArray, console);
		}
		return userArray;
	}//In the darkness of the night...this has been coded.

	//The array-modifying methods

	//Given a boolean array (stored in variable "array")the same length as CBOARD, this program will 
	//get user's input of CBOARD's elements and convert the corresponding boolean in the boolean array
	//to the specified boolean type in the parameter (stored in variable "set")
	public static void negatePieces(String prompt, boolean[] array, boolean set, String[] commands, boolean[] cArray, Scanner console){
		String negPiece = pUFPiece(prompt, CBOARD, commands, cArray, console);	//get a user-specified piece from CBOARD
		while(!negPiece.equalsIgnoreCase("/e")){	//while user is not done
			int index2 = bnrySearch(CBOARD, negPiece);	//get index
			if(array[index2]==set){	//if "set" matches the boolean that is to be changed
				System.out.println("That piece is already " + dOAlv(set));	//let user know
			}
			array[index2] = set;	//set corresponding boolean to "set"
			negPiece = pUFPiece("", CBOARD, commands, cArray, console);	//get a user-specified piece from CBOARD
		}
	}
	//Given a string array, fills in the specified start(inclusive) and end(exclusive) indexes with 
	//the specified string also returns the index where the method ended at.  
	public static int updateArray(String[] adirection, int start, int end, String sdirection){
		for(int i=start; i<end; i++){
			adirection[i] = sdirection;
		}
		return end;
	}
	//Given int array, fill in the specified start(inclusive) and end(exclusive) with incrementing
	//integers starting at 1.  For example, A call updateArray(aspaces, 0, 4) updates
	//aspaces = {1, 2, 3, 4, rest of the array}
	public static void updateArray(int[] aspaces, int start, int end){
		for(int i=start; i<end; i++){
			aspaces[i] = 1 + i - start;
		}
	}
	//Sticks two String arrays together, end to end
	public static String[] catnate(String[] array1, String[] array2){
		String[] catnate = Arrays.copyOf(array1, array1.length+array2.length);	//copy array1 with room
		for(int i=array1.length; i<array1.length+array2.length; i++){	//to add the elements of array2
			catnate[i] = array2[i-array1.length];
		}
		return catnate;
	}
	//Sticks two String[][] arrays together, end to end
	public static String[][] catnate(String[][] array1, String[][] array2){
		String[][] catnate = Arrays.copyOf(array1, array1.length+array2.length);	//copy array1 with room
		for(int i=array1.length; i<array1.length+array2.length; i++){	//to add the elements of array 2
			catnate[i] = array2[i-array1.length];
		}
		return catnate;
	}
	//Sticks one int array and one String array side by side to create a String[][]
	public static String[][] layOn(int[] array1, String[] array2){
		String[][] combArr = new String[array1.length][2];
		for(int i=0; i<array1.length; i++){
			combArr[i] = new String[]{""+array1[i], array2[i]};
			//each term in the combined array = {term at array1, term at array2}
		}
		return combArr;
	}
	//The array-building methods

	//builds a String array that includes only the elements of CBOARD that have
	//have a corresponding true value in the corresponding boolean array
	public static String[] buildArray(boolean[] bArray, int lOfsArray){
		//lOfsArray (length Of array)
		String[] sArray = new String[lOfsArray];
		int sIndex = 0;//string Index
		for(int i=0; i<bArray.length; i++){
			if(bArray[i]){ //if corresponding boolean element is true
				sArray[sIndex] = CBOARD[i];	//add the corresponding string as the next term
				sIndex += 1;
			}
		}
		return sArray;
	}

	//Other array methods

	//counts the number of trues in a boolean array
	public static int cPossibleChoices(boolean[] array){
		int count = 0;
		for(int i=0; i<array.length; i++){
			if(array[i]){
				count += 1;
			}
		}
		return count;
	}
	//returns the index of a specified string in a specified string array.  returns -1 when not found
	public static int bnrySearch(String[] array, String target){
		for(int i=0; i<array.length; i++){
			if(target.equalsIgnoreCase(array[i])){
				return i;
			}
		}
		return -1;
	}
	//prints all the elements of a String array formatted with 8 elements per row
	//with the specified right padding for every term
	public static void prntSpcdArr(String[] array, int padding){
		int p = 0;	//past index
		int i = 0;	//index
		while(i<array.length){
			for(i=p; i<p+8; i++){	//to print a row
				if(i>=array.length){
					break;
				}//prevents "Array out of bounds" errors
				System.out.printf("%-"+padding+"s", array[i]);
			}
			p += 8;
			System.out.println();
		}
	}
	//will return the length of the longest term in a string array
	public static int lgthstTrm(String[] array){
		int l = 0;
		for(String s: array){
			if(s.length()>l){
				l = s.length();
			}
		}
		return l;
	}

	//The boolean methods

	//takes in an array and a string and returns true if specified string is part of the array
	public static boolean isPiece(String[] array, String piece){
		for(int i=0; i<array.length; i++){
			if(piece.equalsIgnoreCase(array[i])){
				return true;
			}
		}
		return false;
	}

	//takes in a boolean array and returns true if the boolean array has any values that are true
	public static boolean hasTrue(boolean[] array){
		for(boolean b: array){
			if(b){
				return true;
			}
		}
		return false;
	}

	//Miscellaneous methods

	//(boolean to int): true => 1; false => 0
	public static int bToInt(boolean bBoolean){
		if(bBoolean){
			return 1;
		}
		return 0;
	}
	//Given a horse-direction shorthand (ie. LF), it will return the
	//unshortened version(ie. "two spaces left and one space forward")
	public static String unshorten(String direction){
		direction = direction.toLowerCase();
		char c1 = direction.charAt(0);
		char c2 = direction.charAt(1);
		return "two spaces " + expand(c1) + " and one space " + expand(c2);
	}
	//given a character ('b', 'f', 'l', 'r'), it will return the
	//entire word ("backwards", "forwards", "left", "right")
	public static String expand(char c){
		if(c==98){
			return "backwards";
		} else if(c==102){
			return "forwards";
		} else if(c==108){
			return "left";
		} else {
			return "right";
		}
	}
	//returns a minumum of two integers
	public static int minimum(int int1, int int2){
		if(int1<int2){
			return int1;
		} else {
			return int2;
		}
	}
	//will only print if specified boolean is true
	public static void condPrint(String prompt, boolean test){
		if(test){
			System.out.print(prompt);
		}
	}
	//(dead or Alive): true => alive; false => dead
	public static String dOAlv(boolean alive){
		if(alive){
			return "alive";
		} else {
			return "dead";
		}
	}
}

































































/*
Chess stories
On the chessboard..."hurry! We must destroy the enemy queen!" says king jell. Even though they know it
	is impossible, they will still try. Look! Looks like king jell has spotted something. "Our pawn!" says jell,
	"it is diagonal right to that enemy queen!"
	"What do we do?" asks a nearby knight.
	"We move our pawn forward," says king jell wisely
	The end
*/