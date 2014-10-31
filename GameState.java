

import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Represents a game state with a 8x8 board
 *
 * Cells are numbered as follows:
 *
 *    col 0  1  2  3  4  5  6  7
 * row  -------------------------
 *  0  |     1     2     3     4 |  0
 *  1  |  5     6     7     8    |  1
 *  2  |     9    10    11    12 |  2
 *  3  | 13    14    15    16    |  3
 *  4  |    17    18    19    20 |  4
 *  5  | 21    22    23    24    |  5
 *  6  |    25    26    27    28 |  6
 *  7  | 29    30    31    32    |  7
 *      -------------------------
 *        0  1  2  3  4  5  6  7
 *
 * The staring board looks like this:
 *
 *    col 0  1  2  3  4  5  6  7
 * row  -------------------------
 *  0  |    rr    rr    rr    rr |  0
 *  1  | rr    rr    rr    rr    |  1
 *  2  |    rr    rr    rr    rr |  2
 *  3  | ..    ..    ..    ..    |  3
 *  4  |    ..    ..    ..    .. |  4
 *  5  | ww    ww    ww    ww    |  5
 *  6  |    ww    ww    ww    ww |  6
 *  7  | ww    ww    ww    ww    |  7
 *      -------------------------
 *        0  1  2  3  4  5  6  7
 *
 * The red player starts from the top of the board (row 0,1,2)
 * The white player starts from the bottom of the board (row 5,6,7),
 * Red moves first.
 */
public class GameState implements Cloneable {
	public static final int cSquares = 32;			// 32 valid squares
	public static final int cPlayerPieces = 12;	// 12 pieces per player
	public static final int cMovesUntilDraw = 50;	// 25 moves per player

	private int[] mCell = new int[GameState.cSquares];
	private int mMovesUntilDraw;
	private int mNextPlayer;
	private Move mLastMove;

	/**
	 * Initializes the board to the starting position
	 */
	public GameState() {
		// Initialize the board
		for(int i = 0; i < GameState.cPlayerPieces; i++) {
			this.mCell[i] = Constants.CELL_RED;
			this.mCell[cSquares-1-i] = Constants.CELL_WHITE;
		} // End for
		
		for(int i = GameState.cPlayerPieces; 
				i < GameState.cSquares - GameState.cPlayerPieces;
				i++) {
			
			this.mCell[i] = Constants.CELL_EMPTY;
		} // End for
		
		// Initialize move related variables
		this.mLastMove = new Move(Move.MoveType.MOVE_BOG);
		this.mMovesUntilDraw = GameState.cMovesUntilDraw;
		this.mNextPlayer = Constants.CELL_RED;
	} // End constructor GameState

	/**
	 * Constructs a board from a message string
	 *
	 * @param pMessage the compact string representation of the state
	 */
	public GameState(final String pMessage) {
		// Split the message with a string
		StringTokenizer st = new StringTokenizer(pMessage);
		
		String board, last_move, next_player;
		int moves_left;
		
		board = st.nextToken();
		last_move = st.nextToken();
		next_player = st.nextToken();
		moves_left = Integer.parseInt(st.nextToken());
		
		assert(board.length() == GameState.cSquares);
		assert(next_player.length() == 1);
		assert(moves_left >= 0 && moves_left < 256);

		// Parse the board
		for (int i = 0; i < GameState.cSquares; i++) {
			
			if (board.charAt(i) == Constants.MESSAGE_SYMBOLS[Constants.CELL_EMPTY]) {
			
				this.mCell[i] = Constants.CELL_EMPTY;
			
			} else if (board.charAt(i) == Constants.MESSAGE_SYMBOLS[Constants.CELL_RED]) {
			
				this.mCell[i] = Constants.CELL_RED;
			
			} else if (board.charAt(i) == Constants.MESSAGE_SYMBOLS[Constants.CELL_WHITE]) {
				
				this.mCell[i] = Constants.CELL_WHITE;
			
			} else if (board.charAt(i) == Constants.MESSAGE_SYMBOLS[Constants.CELL_RED | Constants.CELL_KING]) {
				
				this.mCell[i] = Constants.CELL_RED | Constants.CELL_KING;
			
			} else if (board.charAt(i) == Constants.MESSAGE_SYMBOLS[Constants.CELL_WHITE | Constants.CELL_KING]) {
				
				this.mCell[i] = Constants.CELL_WHITE | Constants.CELL_KING;
			
			} else {
				// ???
				//assert("Invalid cell" && false);
			} // End else
		} // End for

		// Parse last move
		this.mLastMove = new Move(last_move);

		// Parse next player
		if (next_player.charAt(0) == Constants.MESSAGE_SYMBOLS[Constants.CELL_EMPTY]) {
			
			mNextPlayer = Constants.CELL_EMPTY;
		
		} else if (next_player.charAt(0) == Constants.MESSAGE_SYMBOLS[Constants.CELL_RED]) {
			
			mNextPlayer = Constants.CELL_RED;
		
		} else if (next_player.charAt(0) == Constants.MESSAGE_SYMBOLS[Constants.CELL_WHITE]) {
			
			mNextPlayer = Constants.CELL_WHITE;
		
		} else if (next_player.charAt(0) == Constants.MESSAGE_SYMBOLS[Constants.CELL_RED | Constants.CELL_KING]) {
		
			mNextPlayer = Constants.CELL_RED | Constants.CELL_KING;
		
		} else if (next_player.charAt(0) == Constants.MESSAGE_SYMBOLS[Constants.CELL_WHITE | Constants.CELL_KING]) {
		
			mNextPlayer = Constants.CELL_WHITE | Constants.CELL_KING;
		
		} else {
			// ???
			//assert("Invalid next player" && false);
		} // End else
		
		// Set number of moves left until draw
		this.mMovesUntilDraw = moves_left;
	} // End constructor GameState

	/**
	 * Constructs a board which is the result of applying move \p pMove to board \p pRH
	 *
	 * @param pRH the starting board position
	 * @param pMove the movement to perform
	 * @see DoMove()
	 */
	public GameState(final GameState pRH, final Move pMove) {
		// Copy board
	    //memcpy(mCell, pRH.mCell, sizeof(mCell));
	    this.mCell = pRH.mCell.clone();
	    
	    // Copy move status
	    this.mMovesUntilDraw = pRH.mMovesUntilDraw;
	    this.mNextPlayer     = pRH.mNextPlayer;
	    this.mLastMove       = pRH.mLastMove;

	    // Perform move
	    this.doMove(pMove);
	} // End constructor GameState
	
	/**
	 * Constructs a state that is the result of rotating the board 180 degrees and swapping colors
	 *
	 */
	GameState reversed() {
	
		GameState result;
		try {
			result = (GameState) this.clone();
			for (int i = 0; i < 32; i++) {
				if (this.mCell[31-i] == Constants.CELL_EMPTY) {
					result.mCell[i] = Constants.CELL_EMPTY;
				} else {
					result.mCell[i] 
						= mCell[31-i] ^ (Constants.CELL_RED | Constants.CELL_WHITE);
				} // End else
			} // End for
	    
			result.mNextPlayer ^= (Constants.CELL_RED | Constants.CELL_WHITE);
			result.mLastMove = mLastMove.reversed();
			return result;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	} // End Reversed
	
	/**
	 *@Return the content of a cell in the board.
	 *
	 * This function returns a byte representing the contents of the cell,
	 * using the enumeration values in ECell
	 *
	 * For example, to check if cell 23 contains a white piece, you would check if
	 *
	 * (lBoard.At(23)&CELL_WHITE)
	 *
	 * to check if it is a red piece,
	 *
	 *   (lBoard.At(23)&CELL_RED)
	 *
	 * and to check if it is a king, you would check if
	 *
	 *   (lBoard.At(23)&CELL_KING)
	 */
	int get(int pPos) {
		assert(pPos > 0);
		assert(pPos <= cSquares);
		return mCell[pPos - 1];
	} // End get

	/**
	 * Set version of the above function
	 */
	void set(int pPos, int v) {
		assert(pPos > 0);
		assert(pPos <= cSquares);
		mCell[pPos - 1] = v;
	} // End set

	/**
	 * @return the content of a cell in the board.
	 *
	 * Rows are numbered (0 to 7) from the upper row in the board,
	 * as seen by the player this program is playing.
	 *
	 * Columns are numbered starting from the left (also 0 to 7).
	 *
	 * Cells corresponding to white squares in the board, which will
	 * never contain a piece, always return CELL_INVALID
	 *
	 * If the cell falls outside of the board, return CELL_INVALID
	 *
	 * You can use it in the same way as the version that requires a cell index
	 */
	int get(int pR, int pC) {
		if (pR < 0 || pR > 7 || pC < 0 || pC > 7) {
			return Constants.CELL_INVALID;
		} // End if
		
		if ((pR & 1) == (pC & 1)) {
			return Constants.CELL_INVALID;
		} // End if
		
		return this.mCell[pR * 4 + (pC >> 1)];
	} // End get

	/**
	 * private version of above function (allows modifying cells)
	 */
	private void set(int pR, int pC, int v) {
		//this is a bit ugly, but is useful for the implementation of
		//FindPossibleMoves. It won't affect in single-threaded programs
		//and you're not allowed to use threads anyway
		this.mCell[pR * 4 + (pC >> 1)] = v;
	} // End MutableAt


	/**
	 * @param pCell
	 * @return the row corresponding to a cell index
	 */
	public static int cellToRow(int pCell) {
		return ((pCell - 1) >> 2);
	} // End CellToRow

	/**
	 * @param pCell
	 * @return the col corresponding to a cell index
	 */
	public static int cellToCol(int pCell) {
		int lC = ((pCell - 1) & 3) << 1;
		// Todo: What is that???
		if ( 0 == ((pCell - 1) & 4) ) {
			lC++;
		} // End if
		return lC;
	} // End CellToCol

	/**
	 *
	 * It doesn't check if it corresponds to a black square in the board,
	 * or if it falls within the board.
	 *
	 * If it doesn't, the result is undefined, and the program is likely
	 * to crash
	 *
	 * @param pRow
	 * @param pCol
	 * @return the cell corresponding to a row and col
	 */
	static int rowColToCell(int pRow, int pCol) {
		return (pRow * 4 + (pCol >> 1)) + 1;
	} // End RowColToCell

	private boolean tryJump(Vector<Move> pMoves, int pR, int pC, boolean pKing,
			int[] pBuffer) {
		return this.tryJump(pMoves, pR, pC, pKing, pBuffer, 0);
	} // End TryJump
	/**
	 * Tries to make a jump from a certain position of the board
	 *
	 * @param pMoves a vector where the valid moves will be inserted
	 * @param pOther the \ref ECell code corresponding to the player who is not making the move
	 * @param pR the row of the cell we are moving from
	 * @param pC the col
	 * @param pKing true if the moving piece is a king
	 * @param pBuffer a buffer where the list of jump positions is
	 *   inserted (for multiple jumps)
	 * @param pDepth the number of multiple jumps before this attempt
	 */
	private boolean tryJump(Vector<Move> pMoves, int pR, int pC, boolean pKing,
			int[] pBuffer, int pDepth) {
	    
		// Remove ourself temporarily
		int lOldSelf = this.get(pR, pC);
		this.set(pR, pC, Constants.CELL_EMPTY);

		pBuffer[pDepth] = GameState.rowColToCell(pR, pC);

	    boolean lFound = false;
	    int lOther = mNextPlayer ^ (Constants.CELL_WHITE | Constants.CELL_RED);

	    // Try capturing downwards
	    if(mNextPlayer == Constants.CELL_RED || pKing) {
	        // Try capturing left
	        if( 0 != (this.get(pR+1, pC-1) & lOther) && this.get(pR+2,pC-2) == Constants.CELL_EMPTY) {
	            lFound = true;
	            int lOldValue = get(pR+1, pC-1);
	            this.set(pR+1, pC-1, Constants.CELL_EMPTY);
	            this.tryJump(pMoves, pR+2, pC-2, pKing, pBuffer, pDepth + 1);
	            this.set(pR+1, pC-1, lOldValue);
	        } // End if
	        
	        // Try capturing right
	        if( 0 != (this.get(pR+1, pC+1) & lOther) && this.get(pR+2,pC+2) == Constants.CELL_EMPTY) {
	            lFound = true;
	            int lOldValue = this.get(pR+1, pC+1);
	            this.set(pR+1, pC+1, Constants.CELL_EMPTY);
	            this.tryJump(pMoves, pR+2, pC+2, pKing, pBuffer, pDepth + 1);
	            this.set(pR+1, pC+1, lOldValue);
	        } // End if
	    } // End if
	    
	    // Try capturing upwards
	    if(mNextPlayer == Constants.CELL_WHITE || pKing) {
	        // Try capturing left
	        if( 0 != (this.get(pR-1, pC-1) & lOther) && this.get(pR-2, pC-2) == Constants.CELL_EMPTY) {
	            lFound = true;
	            int lOldValue = this.get(pR-1, pC-1);
	            this.set(pR-1, pC-1, Constants.CELL_EMPTY);
	            this.tryJump(pMoves, pR-2, pC-2, pKing, pBuffer, pDepth + 1);
	            this.set(pR-1, pC-1, lOldValue);
	        } // End if
	        // Try capturing right
	        if( 0 != (this.get(pR-1, pC+1) & lOther) && this.get(pR-2, pC+2) == Constants.CELL_EMPTY) {
	            lFound = true;
	            int lOldValue = this.get(pR-1, pC+1);
	            this.set(pR-1, pC+1, Constants.CELL_EMPTY);
	            this.tryJump(pMoves, pR-2, pC+2, pKing, pBuffer, pDepth + 1);
	            this.set(pR-1, pC+1, lOldValue);
	        } // End if
	    } // End if

	    // Restore ourself
		this.set(pR, pC, lOldSelf);

	    if(!lFound && pDepth > 0) {
	    	Vector<Integer> tmp = new Vector<Integer>();
	    	for (int z : pBuffer) {
	    		tmp.add(z);
	    	}
	        pMoves.add(new Move(tmp, pDepth+1));
	    } // End if
	    return lFound;
	} // End TryJump

	/**
	 * Tries to make a move from a certain position
	 *
	 * @param pMoves vector where the valid moves will be inserted
	 * @param pCell the cell where the move is tried from
	 * @param pOther the \ref ECell code corresponding to the player
	 * 		who is not making the move
	 * @param pKing true if the piece is a king
	 */
	void tryMove(Vector<Move> pMoves, int pCell, boolean pKing) {
	    int lR = GameState.cellToRow(pCell);
	    int lC = GameState.cellToCol(pCell);
	    // Try moving downwards
	    if(mNextPlayer == Constants.CELL_RED || pKing) {
	        // Try moving right
	        if(this.get(lR+1,lC-1) == Constants.CELL_EMPTY) {
	            pMoves.add(new Move(pCell, GameState.rowColToCell(lR+1, lC-1)));
	        } // End if
	        
	        //try moving left
	        if(this.get(lR+1, lC+1) == Constants.CELL_EMPTY) {
	            pMoves.add(new Move(pCell, GameState.rowColToCell(lR+1, lC+1)));
	        } // End if
	    } // End if
	    
	    // Try moving upwards
	    if(mNextPlayer == Constants.CELL_WHITE || pKing) {
	        // Try moving right
	        if(this.get(lR-1, lC-1) == Constants.CELL_EMPTY) {
	            pMoves.add(new Move(pCell, GameState.rowColToCell(lR-1, lC-1)));
	        } // End if
	        // Try moving left
	        if(this.get(lR-1, lC+1) == Constants.CELL_EMPTY) {
	            pMoves.add(new Move(pCell, GameState.rowColToCell(lR-1,lC+1)));
	    	} // End if
	    } // End if
	} // End TryMove

	/**
	 *
	 * @param pMoves a vector where the list of moves will be appended
	 * @param pWho the \ref ECell code (CELL_OWN or CELL_OTHER) of the
	 *  player making the move
	 * @return a list of all valid moves for \p pWho
	 */
	void findPossibleMoves(Vector<GameState> pStates) {

	    pStates.clear();

	    if (mLastMove.isEOG()) {
	    	return;
	    } // End if
	    
	    if (mMovesUntilDraw <= 0) {
	    	try {
				pStates.add(new GameState((GameState) this.clone(), new Move(Move.MoveType.MOVE_DRAW)));
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
	    	return;
	    } // End if

	    // Normal moves are forbidden if any jump is found
	    boolean lFound = false;
	    int[] lPieces = new int[GameState.cPlayerPieces];
	    int[] lMoveBuffer = new int[GameState.cPlayerPieces];
		Vector<Move> lMoves = new Vector<Move>();
		
	    int lNumPieces=0;
	    for (int i = 1; i <= cSquares; i++) {
	        // Is this a piece which belongs to the player making the move?
	        if (0 != (this.get(i) & mNextPlayer)) {
	            boolean lIsKing = 0 != (this.get(i) & Constants.CELL_KING);

	            if (this.tryJump(lMoves, GameState.cellToRow(i), GameState.cellToCol(i), lIsKing, lMoveBuffer)) {
	                lFound=true;
	            } // End if
	            lPieces[lNumPieces++]=i;
	        } // End if
	    } // End for

	    // Try normal moves if no jump was found
	    if (!lFound) {
	        for (int k = 0; k < lNumPieces; k++) {
	            int lCell = lPieces[k];
	            boolean lIsKing = 0 != (this.get(lCell) & Constants.CELL_KING);
	            this.tryMove(lMoves, lCell, lIsKing);
	        } // End for
	    } // End if

	    // Convert moves to GameStates
	    for (int i = 0; i < lMoves.size(); i++) {
	    	try {
				pStates.add(new GameState((GameState) this.clone(), lMoves.elementAt(i)));
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } // End for
	    
	    // Admit loss if no moves can be found
	    if (pStates.size() == 0) {
	    	try {
				pStates.add(
						new GameState((GameState) this.clone(), 
						new Move(mNextPlayer == Constants.CELL_WHITE ? Move.MoveType.MOVE_RW : Move.MoveType.MOVE_WW)));
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } // End if
	} // End FindPossibleMoves

	/**
	 * Transforms the board by performing a move
	 *
	 * It doesn't check that the move is valid, so you should only use
	 * it with moves returned by FindPossibleMoves
	 * @param pMove the move to perform
	 */
	public void doMove(final Move pMove) {
	    
		if (pMove.isJump()) {
	    	// Row and column of source cell
	        int sr = GameState.cellToRow(pMove.at(0));
	        int sc = GameState.cellToCol(pMove.at(0));

	    	// Perform all jumps
	        for(int i = 1; i < pMove.length(); i++) {
	        	// Destination cell
	            int dr = GameState.cellToRow(pMove.at(i));
	            int dc = GameState.cellToCol(pMove.at(i));

	            // Move the jumping piece
	            this.set(pMove.at(i), this.get(pMove.at(i-1)));
	            this.set(pMove.at(i-1), Constants.CELL_EMPTY);

	            // Promote to king if we should
	            if (
	            		(dr == 7 && 0 != (this.get(pMove.at(i)) & Constants.CELL_RED) ) 
	            		|| 
	            		(dr == 0 && 0 != (this.get(pMove.at(i)) & Constants.CELL_WHITE) )
	            	) {
	            	
	                this.set(pMove.at(i), this.get(pMove.at(i)) | Constants.CELL_KING);

	            } // End if
	            
	            // Remove the piece being jumped over
	            this.set( 
	            		GameState.rowColToCell((sr+dr)>>1, (sc+dc)>>1), 
	            		Constants.CELL_EMPTY
	            		);

	            // Prepare for next jump
	            sr = dr;
	            sc = dc;
	        } // End for

	        // Reset number of moves left until draw
	        mMovesUntilDraw = cMovesUntilDraw;
	    
		} else if(pMove.isNormal()) {
	    	// Move the piece
	        this.set(pMove.at(1), this.get(pMove.at(0)));
	        this.set(pMove.at(0), Constants.CELL_EMPTY);

	        // Promote to king if we should
	        int lDR = GameState.cellToRow(pMove.at(1));
	        if (
	        		(lDR == 7 && 0 != (this.get(pMove.at(1)) & Constants.CELL_RED)) 
	        		|| 
	        		(lDR == 0 && 0 != (this.get(pMove.at(1)) & Constants.CELL_WHITE))
	        	) {
	        	
	            this.set(pMove.at(1), this.get(pMove.at(1)) | Constants.CELL_KING);
	        
	        } // End if
	        
	        // Decrease number of moves left until draw
	        --mMovesUntilDraw;
	    } // End if

	    // Remember last move
	    mLastMove = pMove;

	    // Swap player
	    mNextPlayer = mNextPlayer ^ (Constants.CELL_RED | Constants.CELL_WHITE);

	} // End DoMove


	/**
	 * Convert the board to a human readable string ready to be printed to System.err
	 *
	 * Useful for debug purposes. Don't call it in the final version.
	 */
	public String toString(int pPlayer) {
		
		// Select preferred printing style by setting cell_text to SIMPLE_TEXT, UNICODE_TEXT or COLOR_TEXT
		
		final String[] cell_text = Constants.SIMPLE_TEXT;
	
		final String board_top = (cell_text == Constants.SIMPLE_TEXT) ? "     -----------------\n" : "    ╭─────────────────╮\n";
		final String board_bottom = (cell_text == Constants.SIMPLE_TEXT) ? "     -----------------\n" : "    ╰─────────────────╯\n";
		final String board_left = (cell_text == Constants.SIMPLE_TEXT) ? "| " : "│ ";
		final String board_right = (cell_text == Constants.SIMPLE_TEXT) ? "|" : "│";
	
		int red_pieces = 0;
		int white_pieces = 0;
		
		// Count pieces
		for (int i = 1; i <= 32; i++) {
			if (0 != (this.get(i) & Constants.CELL_RED))
				++red_pieces;
			else if (0 != (this.get(i) & Constants.CELL_WHITE))
				++white_pieces;
		} // End for
	
		// Use a StringBuffer to compose the string
		StringBuffer ss = new StringBuffer();
	
		// Draw the board with numbers around it indicating cell index and put text to the right of the board
		ss.append(board_top);
		ss.append("  1 " + board_left); 
		for(int c = 0; c < 8; c++)
			ss.append(cell_text[this.get(0, c)]);
		ss.append(board_right + " 4\n");
		
		ss.append("  5 " + board_left); 
		for(int c = 0; c < 8; c++)
			ss.append(cell_text[this.get(1, c)]);
		ss.append(board_right + " 8\n");
		
		ss.append("  9 " + board_left); 
		for(int c = 0; c < 8; c++)
			ss.append(cell_text[this.get(2, c)]);
		ss.append(board_right + " 12     Last move: " + mLastMove.toString());

		if ((pPlayer == Constants.CELL_RED && this.isRedWin()) || 
			(pPlayer == Constants.CELL_WHITE && this.isWhiteWin()) )
			ss.append(" (WOHO! I WON!)\n");
		else if ((pPlayer == Constants.CELL_RED && this.isWhiteWin()) || 
				(pPlayer == Constants.CELL_WHITE && this.isRedWin()) )
			ss.append(" (Bummer! I lost!)\n");
		else
			ss.append("\n");
		
		ss.append(" 13 " + board_left); 
		for(int c = 0; c < 8; c++)
			ss.append(cell_text[this.get(3, c)]);
		ss.append(board_right + " 16     Next player: " + cell_text[mNextPlayer] + ((mNextPlayer == pPlayer) ? " (My turn)\n" : " (Opponents turn)\n"));
		
		ss.append(" 17 " + board_left);
		for(int c = 0; c < 8; c++)
			ss.append(cell_text[this.get(4, c)]);
		ss.append(board_right + " 20     Moves until draw: " + (int) mMovesUntilDraw + "\n");
		
		ss.append(" 21 " + board_left); 
		for(int c = 0; c < 8; c++)
			ss.append(cell_text[this.get(5, c)]);
		ss.append(board_right + " 24     Red pieces:   " + red_pieces + "\n");
		
		ss.append(" 25 " + board_left); 
		for(int c = 0; c < 8; c++)
			ss.append(cell_text[this.get(6,c)]);
		ss.append(board_right + " 28     White pieces: " + white_pieces + "\n");
		
		ss.append(" 29 " + board_left); 
		for(int c = 0; c < 8; c++)
			ss.append(cell_text[this.get(7,c)]);
		ss.append(board_right + " 32\n");
		
		ss.append(board_bottom);
	
		return ss.toString();
	} // End ToString
	
	/**
	 * Convert the board to a machine readable string ready to be printed to System.out
	 *
	 * This is used for passing board states between bots
	 */
	public String toMessage() {
		// Use a StringBuffer to compose the message
		StringBuffer ss = new StringBuffer();

		// The board goes first
		for(int i = 0; i < cSquares; i++) {
			ss.append(Constants.MESSAGE_SYMBOLS[mCell[i]]);
		} // End for
		
		// Then the information about moves
		assert(mNextPlayer == Constants.CELL_WHITE || mNextPlayer == Constants.CELL_RED);
		
		ss.append(" " + mLastMove.toMessage() + " " + Constants.MESSAGE_SYMBOLS[mNextPlayer] + " " + (int) mMovesUntilDraw);

		return ss.toString();
	} // End ToMessage
	
	/**
	 * Get the last move made (the move that lead to this state)
	 */
	public final Move getMove() {
		return this.mLastMove;
	} // End GetMove

	public final int getNextPlayer() {
		return this.mNextPlayer;
	} // End GetNextPlayer

	final int getMovesUntilDraw() {
		return this.mMovesUntilDraw;
	} // End GetMovesUntilDraw

	/**
	 * @return true if the movement marks beginning of game
	 */
	boolean isBOG() {
		return this.mLastMove.isBOG();
	} // End IsBOG
	
	/**
	 * @return true if the movement marks end of game
	 */
	boolean isEOG() {
		return this.mLastMove.isEOG();
	} // End IsEOG
	
	/**
	 * @return true if the game ended in red win
	 */
	boolean isRedWin() {
		return this.mLastMove.isRedWin();
	} // End IsRedWin
	
	/**
	 * @return true if the game ended in white win
	 */
	boolean isWhiteWin() {
		return mLastMove.isWhiteWin();
	} // End IsWhiteWin
	
	/**
	 * @return true if the game ended in draw
	 */
	boolean isDraw() {
		return mLastMove.isDraw();
	} // End IsDraw

} // End Class GameState
