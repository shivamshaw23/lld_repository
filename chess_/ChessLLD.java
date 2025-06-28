public class Chess{

	Board board;
	Player[] player;
	Player currentPlayyer;
	List<Move> movesList;
	GameStatus gameStatus;

	public Boolean endgame();
	private void changeTurn();


}

class player{
	public String Name;
	Account account;
	Time timeLeft;
}

public class Account {

	String username;
	String password;

	String name;
	String email;
	String phone;
}

public enum GameStatus {

	ACTIVE, PAUSED, FORTFEIGHT, BLACK_WIN, WHITE_WIN;
}


public class Time {

	int mins;
	int secs;

}


abstract class Piece{


	Board currentBoard;
	Position currentPosition;

	void move(Position destination){
		if(isValid(currentPosition,destination)){
			currentPosition=destination;
		}
		Piece piece = currentBoard.get(destination);
		if(piece!=null&&!piece.getColor().equals(color)){

		//capture condition
		}
	}
	void getAllPossibleMoves();
	abstract Boolean isValid(currentPosition,destination){

		Board boardTemp = new Board();
		boardTemp.makeMove(destination);

		if(currentKingInCheck(boardTemp)){
			return false;
		}
		else {
			Piece destinationPlace = currentBoard.get(destination);
			if(destinationPlace!=null&&destinationPlace.getColor().equals(color)){
			return false;
			}

			isValidMove(destination);
		}
	}

	Boolean currentKingInCheck(Board boardTemp){

	}

}

enum Color{

	WHITE,BLACK
}

Class Board{
	Map<Position,Piece>pieces = new HashMap<>()

	public void makeMove(Piece piece, Position destination){
		pieces.remove(piece.currentPostion);
		pieces.put(destination,piece);
	}
	
	public Piece get(Position destination){
		return pieces.get(destinaiton);
	}
	
}

class Position{

	
}

class Pawn extends Piece{
	public void enPassent(){

	}
	
	public void promotion(){

	}
	
}


class King extends Piece{

	public void cassle(){

	}

}

class Knight extends Piece{

}

class Bishop extends Piece{

}

class Queen  extends Piece{

}







