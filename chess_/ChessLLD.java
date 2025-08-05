import java.util.*;
import java.util.concurrent.TimeUnit;

public class Chess {
    private Board board;
    private Player[] players;
    private Player currentPlayer;
    private List<Move> movesList;
    private GameStatus gameStatus;
    private Scanner scanner;

    public Chess() {
        this.board = new Board();
        this.players = new Player[2];
        this.players[0] = new Player("White Player", Color.WHITE, new Time(10, 0));
        this.players[1] = new Player("Black Player", Color.BLACK, new Time(10, 0));
        this.currentPlayer = players[0]; // White starts
        this.movesList = new ArrayList<>();
        this.gameStatus = GameStatus.ACTIVE;
        this.scanner = new Scanner(System.in);
        initializeBoard();
    }

    private void initializeBoard() {
        board.initialize();
    }

    public void playGame() {
        System.out.println("=== Welcome to Chess ===");
        System.out.println("Commands: 'move e2 e4', 'castle', 'resign', 'draw'");
        
        while (gameStatus == GameStatus.ACTIVE) {
            board.display();
            System.out.println("\n" + currentPlayer.getName() + "'s turn (" + 
                             currentPlayer.getColor() + ")");
            System.out.println("Time left: " + currentPlayer.getTimeLeft());
            System.out.print("Enter your move: ");
            
            try {
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("resign")) {
                    resign();
                    break;
                } else if (input.equals("draw")) {
                    offerDraw();
                } else if (input.startsWith("move ")) {
                    processMove(input.substring(5));
                } else if (input.equals("castle")) {
                    processCastle();
                } else {
                    System.out.println("Invalid command. Try 'move e2 e4'");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        
        displayGameResult();
    }

    private void processMove(String moveStr) {
        String[] parts = moveStr.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid move format. Use: e2 e4");
        }
        
        Position from = Position.fromString(parts[0]);
        Position to = Position.fromString(parts[1]);
        
        if (makeMove(from, to)) {
            changeTurn();
        }
    }

    private boolean makeMove(Position from, Position to) {
        Piece piece = board.getPiece(from);
        if (piece == null) {
            throw new IllegalArgumentException("No piece at " + from);
        }
        
        if (!piece.getColor().equals(currentPlayer.getColor())) {
            throw new IllegalArgumentException("That's not your piece!");
        }
        
        if (piece.isValidMove(from, to, board)) {
            Move move = new Move(from, to, piece, board.getPiece(to));
            board.makeMove(move);
            movesList.add(move);
            
            // Check for game ending conditions
            checkGameStatus();
            return true;
        } else {
            throw new IllegalArgumentException("Invalid move for " + piece.getClass().getSimpleName());
        }
    }

    private void processCastle() {
        // Simplified castling - would need more complex logic in full implementation
        System.out.println("Castling not fully implemented in this demo");
    }

    private void checkGameStatus() {
        Player opponent = getOpponent();
        if (isInCheckmate(opponent)) {
            gameStatus = currentPlayer.getColor() == Color.WHITE ? 
                        GameStatus.WHITE_WIN : GameStatus.BLACK_WIN;
        } else if (isInStalemate(opponent)) {
            gameStatus = GameStatus.DRAW;
        }
    }

    private boolean isInCheckmate(Player player) {
        // Simplified checkmate detection
        return isInCheck(player) && !hasValidMoves(player);
    }

    private boolean isInStalemate(Player player) {
        return !isInCheck(player) && !hasValidMoves(player);
    }

    private boolean isInCheck(Player player) {
        Position kingPos = board.findKing(player.getColor());
        return kingPos != null && board.isUnderAttack(kingPos, player.getColor());
    }

    private boolean hasValidMoves(Player player) {
        // Simplified - would need to check all pieces and all possible moves
        return true; // Placeholder
    }

    private void changeTurn() {
        currentPlayer = (currentPlayer == players[0]) ? players[1] : players[0];
    }

    private Player getOpponent() {
        return (currentPlayer == players[0]) ? players[1] : players[0];
    }

    private void resign() {
        gameStatus = currentPlayer.getColor() == Color.WHITE ? 
                    GameStatus.BLACK_WIN : GameStatus.WHITE_WIN;
        System.out.println(currentPlayer.getName() + " resigned!");
    }

    private void offerDraw() {
        System.out.println("Draw offered. This would require opponent acceptance in full game.");
        gameStatus = GameStatus.DRAW;
    }

    private void displayGameResult() {
        System.out.println("\n=== Game Over ===");
        switch (gameStatus) {
            case WHITE_WIN:
                System.out.println("White wins!");
                break;
            case BLACK_WIN:
                System.out.println("Black wins!");
                break;
            case DRAW:
                System.out.println("Game drawn!");
                break;
            default:
                System.out.println("Game ended");
        }
    }

    public boolean endGame() {
        return gameStatus != GameStatus.ACTIVE;
    }

    public static void main(String[] args) {
        Chess game = new Chess();
        game.playGame();
    }
}

class Player {
    private String name;
    private Account account;
    private Time timeLeft;
    private Color color;

    public Player(String name, Color color, Time timeLeft) {
        this.name = name;
        this.color = color;
        this.timeLeft = timeLeft;
        this.account = new Account();
    }

    public String getName() { return name; }
    public Color getColor() { return color; }
    public Time getTimeLeft() { return timeLeft; }
    public Account getAccount() { return account; }
}

class Account {
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;

    public Account() {
        this.username = "guest";
        this.name = "Guest Player";
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

enum GameStatus {
    ACTIVE, PAUSED, FORFEIT, BLACK_WIN, WHITE_WIN, DRAW
}

enum Color {
    WHITE, BLACK
}

class Time {
    private int mins;
    private int secs;

    public Time(int mins, int secs) {
        this.mins = mins;
        this.secs = secs;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", mins, secs);
    }

    public int getMins() { return mins; }
    public int getSecs() { return secs; }
}

class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static Position fromString(String pos) {
        if (pos.length() != 2) {
            throw new IllegalArgumentException("Invalid position format");
        }
        char file = pos.charAt(0);
        char rank = pos.charAt(1);
        
        int col = file - 'a';
        int row = 8 - (rank - '0');
        
        return new Position(row, col);
    }

    @Override
    public String toString() {
        char file = (char) ('a' + col);
        char rank = (char) ('8' - row + '0');
        return "" + file + rank;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

class Board {
    private Piece[][] board;
    private static final int SIZE = 8;

    public Board() {
        board = new Piece[SIZE][SIZE];
    }

    public void initialize() {
        // Initialize pawns
        for (int i = 0; i < SIZE; i++) {
            board[1][i] = new Pawn(Color.BLACK);
            board[6][i] = new Pawn(Color.WHITE);
        }

        // Initialize other pieces
        board[0][0] = new Rook(Color.BLACK);
        board[0][7] = new Rook(Color.BLACK);
        board[7][0] = new Rook(Color.WHITE);
        board[7][7] = new Rook(Color.WHITE);

        board[0][1] = new Knight(Color.BLACK);
        board[0][6] = new Knight(Color.BLACK);
        board[7][1] = new Knight(Color.WHITE);
        board[7][6] = new Knight(Color.WHITE);

        board[0][2] = new Bishop(Color.BLACK);
        board[0][5] = new Bishop(Color.BLACK);
        board[7][2] = new Bishop(Color.WHITE);
        board[7][5] = new Bishop(Color.WHITE);

        board[0][3] = new Queen(Color.BLACK);
        board[7][3] = new Queen(Color.WHITE);

        board[0][4] = new King(Color.BLACK);
        board[7][4] = new King(Color.WHITE);
    }

    public void display() {
        System.out.println("\n  a b c d e f g h");
        for (int i = 0; i < SIZE; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < SIZE; j++) {
                Piece piece = board[i][j];
                if (piece == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(piece.getSymbol() + " ");
                }
            }
            System.out.println((8 - i));
        }
        System.out.println("  a b c d e f g h\n");
    }

    public Piece getPiece(Position pos) {
        return board[pos.getRow()][pos.getCol()];
    }

    public void setPiece(Position pos, Piece piece) {
        board[pos.getRow()][pos.getCol()] = piece;
    }

    public void makeMove(Move move) {
        setPiece(move.getTo(), move.getPiece());
        setPiece(move.getFrom(), null);
    }

    public Position findKing(Color color) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Piece piece = board[i][j];
                if (piece instanceof King && piece.getColor() == color) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    public boolean isUnderAttack(Position pos, Color kingColor) {
        // Simplified attack detection
        return false; // Placeholder
    }

    public boolean isValidPosition(Position pos) {
        return pos.getRow() >= 0 && pos.getRow() < SIZE && 
               pos.getCol() >= 0 && pos.getCol() < SIZE;
    }
}

class Move {
    private Position from;
    private Position to;
    private Piece piece;
    private Piece capturedPiece;

    public Move(Position from, Position to, Piece piece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
    }

    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public Piece getPiece() { return piece; }
    public Piece getCapturedPiece() { return capturedPiece; }
}

abstract class Piece {
    protected Color color;

    public Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract boolean isValidMove(Position from, Position to, Board board);
    public abstract char getSymbol();
}

class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int direction = (color == Color.WHITE) ? -1 : 1;
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = Math.abs(to.getCol() - from.getCol());

        // Forward move
        if (colDiff == 0 && rowDiff == direction && board.getPiece(to) == null) {
            return true;
        }
        
        // Initial two-square move
        if (colDiff == 0 && rowDiff == 2 * direction && 
            ((color == Color.WHITE && from.getRow() == 6) || 
             (color == Color.BLACK && from.getRow() == 1)) &&
            board.getPiece(to) == null) {
            return true;
        }
        
        // Capture
        if (colDiff == 1 && rowDiff == direction && 
            board.getPiece(to) != null && 
            board.getPiece(to).getColor() != color) {
            return true;
        }

        return false;
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'P' : 'p';
    }
}

class Rook extends Piece {
    public Rook(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        // Rook moves horizontally or vertically
        if (from.getRow() != to.getRow() && from.getCol() != to.getCol()) {
            return false;
        }
        
        // Check if path is clear (simplified)
        return board.getPiece(to) == null || 
               board.getPiece(to).getColor() != color;
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'R' : 'r';
    }
}

class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());
        
        boolean validKnightMove = (rowDiff == 2 && colDiff == 1) || 
                                 (rowDiff == 1 && colDiff == 2);
        
        return validKnightMove && 
               (board.getPiece(to) == null || 
                board.getPiece(to).getColor() != color);
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'N' : 'n';
    }
}

class Bishop extends Piece {
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());
        
        // Bishop moves diagonally
        return rowDiff == colDiff && rowDiff > 0 &&
               (board.getPiece(to) == null || 
                board.getPiece(to).getColor() != color);
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'B' : 'b';
    }
}

class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        // Queen combines rook and bishop moves
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());
        
        boolean validMove = (rowDiff == 0 || colDiff == 0 || rowDiff == colDiff) && 
                           (rowDiff > 0 || colDiff > 0);
        
        return validMove && 
               (board.getPiece(to) == null || 
                board.getPiece(to).getColor() != color);
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'Q' : 'q';
    }
}

class King extends Piece {
    public King(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());
        
        // King moves one square in any direction
        return rowDiff <= 1 && colDiff <= 1 && (rowDiff > 0 || colDiff > 0) &&
               (board.getPiece(to) == null || 
                board.getPiece(to).getColor() != color);
    }

    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'K' : 'k';
    }
}







