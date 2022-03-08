import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Graphics implements GraphicsInterface {
    CanvasInterface canvas;
    List<Position> visited = new ArrayList<>();
    Color color;

    static class Pos implements Position {
        int row;
        int col;

        public Pos(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public int getRow() {
            return row;
        }

        @Override
        public int getCol() {
            return col;
        }

        @Override
        public String toString() {
            return "Position [row=" + getRow() + ", col=" + getCol() + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pos pos = (Pos) o;
            return row == pos.row && col == pos.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    @Override
    public void setCanvas(CanvasInterface canvas) {
        this.canvas = canvas;
    }

    void getPossibleMoves(Position position) {

        Position up = new Pos(position.getRow(), position.getCol() + 1);
        Position down = new Pos(position.getRow(), position.getCol() - 1);
        Position right = new Pos(position.getRow() + 1, position.getCol());
        Position left = new Pos(position.getRow() - 1, position.getCol());

        if (!visited.contains(up)) {
            fillWithRecursion(up);
        }
        if (!visited.contains(down)) {
            fillWithRecursion(down);
        }
        if (!visited.contains(right)) {
            fillWithRecursion(right);
        }
        if (!visited.contains(left)) {
            fillWithRecursion(left);
        }
    }


    boolean tryToFillPosition(Position position) {
        boolean check = false;
        try {
            canvas.setColor(position, color);
            check = true;
            getPossibleMoves(position);
        } catch (CanvasInterface.CanvasBorderException ignored) {
        } catch (CanvasInterface.BorderColorException e) {
            try {
                canvas.setColor(position, e.previousColor);
            } catch (CanvasInterface.BorderColorException | CanvasInterface.CanvasBorderException ignored) {
            }
        }
        return check;
    }

    void tryToFillStartingPosition(Position startingPosition) throws WrongStartingPosition {
        visited.add(startingPosition);
        boolean check = tryToFillPosition(startingPosition);
        if (!check) {
            throw new WrongStartingPosition();
        }
    }

    void fillWithRecursion(Position position) {
        visited.add(position);
        tryToFillPosition(position);
    }

    @Override
    public void fillWithColor(Position startingPosition, Color color) throws WrongStartingPosition, NoCanvasException {
        if (canvas == null) {
            throw new NoCanvasException();
        }
        this.color = color;
        visited.clear();
        tryToFillStartingPosition(startingPosition);
    }
}
