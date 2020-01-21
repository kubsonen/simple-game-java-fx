package pl.com.game.component;

public class Board extends Wrapper {

    private BoardModel boardModel;

    public Board() {
        super("game-board");
        boardModel = getLoader().getController();
    }

    public BoardModel getBoardModel() {
        return boardModel;
    }
}
