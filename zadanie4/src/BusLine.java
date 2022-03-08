import java.util.*;

class BusLine implements BusLineInterface {


    Map<String, List<Position>> lines = new HashMap<>();
    Map<String, Set<LineSegment>> segments = new HashMap<>();
    Map<String, List<Position>> map_getLines = new HashMap<>();
    Map<String, List<Position>> map_getIntersectionPosition = new HashMap<>();
    Map<String, List<String>> map_getIntersectionWithLines = new HashMap<>();
    Map<BusLineInterface.LinesPair, Set<Position>> map_getIntersectionLinesPair = new HashMap<>();

    static class LinesPair implements BusLineInterface.LinesPair {

        private String firstName;
        private String secondName;

        public LinesPair(String firstName, String secondName) {
            this.firstName = firstName;
            this.secondName = secondName;
        }

        @Override
        public String getFirstLineName() {
            return firstName;
        }

        @Override
        public String getSecondLineName() {
            return secondName;
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, secondName);
        }

        @Override
        public String toString() {
            return "LinesPair [firstName=" + getFirstLineName() + ", secondName=" + getSecondLineName() + "]";
        }
    }

    private Map<String, List<Position>> findLines() {
        Map<String, List<Position>> lines_map = new HashMap<>();

        for (String name : lines.keySet()) {
            Set<LineSegment> seg = segments.get(name);
            List<Position> pos = new ArrayList<>(this.lines.get(name));
            int map_size = seg.size();
            pos.remove(1);
            while (map_size != 0) {
                for (LineSegment x : seg) {
                    var start = x.getFirstPosition();
                    var end = x.getLastPosition();
                    if (pos.get(pos.size() - 1).equals(start)) {
                        pos.remove(pos.size() - 1);
                        if (start.equals(end)) {
                            pos.add(start);
                        }
                        if (start.getRow() == end.getRow() && start.getCol() < end.getCol()) {
                            for (int i = 0; i <= end.getCol() - start.getCol(); ++i) {
                                pos.add(new Position2D(start.getCol() + i, start.getRow()));
                            }
                        }
                        if (start.getRow() == end.getRow() && start.getCol() > end.getCol()) {
                            for (int i = 0; i <= start.getCol() - end.getCol(); ++i) {
                                pos.add(new Position2D(start.getCol() - i, start.getRow()));
                            }
                        }
                        if (start.getRow() > end.getRow() && start.getCol() == end.getCol()) {
                            for (int i = 0; i <= start.getRow() - end.getRow(); ++i) {
                                pos.add(new Position2D(start.getCol(), start.getRow() - i));
                            }
                        }
                        if (start.getRow() < end.getRow() && start.getCol() == end.getCol()) {
                            for (int i = 0; i <= end.getRow() - start.getRow(); ++i) {
                                pos.add(new Position2D(start.getCol(), start.getRow() + i));
                            }
                        }
                        if (start.getRow() < end.getRow() && start.getCol() > end.getCol()) {
                            for (int i = 0; i <= start.getCol() - end.getCol(); ++i) {
                                pos.add(new Position2D(start.getCol() - i, start.getRow() + i));
                            }
                        }
                        if (start.getRow() < end.getRow() && start.getCol() < end.getCol()) {
                            for (int i = 0; i <= end.getCol() - start.getCol(); ++i) {
                                pos.add(new Position2D(start.getCol() + i, start.getRow() + i));
                            }
                        }
                        if (start.getRow() > end.getRow() && start.getCol() > end.getCol()) {
                            for (int i = 0; i <= start.getCol() - end.getCol(); ++i) {
                                pos.add(new Position2D(start.getCol() - i, start.getRow() - i));
                            }
                        }
                        if (start.getRow() > end.getRow() && start.getCol() < end.getCol()) {
                            for (int i = 0; i <= end.getCol() - start.getCol(); ++i) {
                                pos.add(new Position2D(start.getCol() + i, start.getRow() - i));
                            }
                        }
                        --map_size;
                    }
                }
            }
            lines_map.put(name, pos);
        }
        return lines_map;
    }

    private void containsCheck(String name, Position previous, Position current, Position next) {
        Set<String> names = map_getLines.keySet();
        for (String n : names) {
            List<Position> other_line = map_getLines.get(n);
            if (other_line.contains(previous) && other_line.contains(current) && other_line.contains(next)) {
                if (map_getIntersectionPosition.containsKey(name)) {
                    map_getIntersectionPosition.get(name).add(current);
                    map_getIntersectionWithLines.get(name).add(n);
                } else {
                    List<String> matched_lines = new ArrayList<>();
                    matched_lines.add(n);
                    map_getIntersectionWithLines.put(name, matched_lines);

                    List<Position> matched_position = new ArrayList<>();
                    matched_position.add(current);
                    map_getIntersectionPosition.put(name, matched_position);
                }
            }
        }
    }

    private void getLinesPairs() {
        Set<String> names = map_getLines.keySet();
        Set<Position> empty = new HashSet<>();
        for (String name : names) {
            if (map_getIntersectionPosition.containsKey(name)) {
                var positions = map_getIntersectionPosition.get(name);
                for (String n : names) {
                    Set<Position> pair = new HashSet<>();
                    if (map_getIntersectionPosition.containsKey(n)) {
                        var pos = map_getIntersectionPosition.get(n);
                        for (Position p : positions) {
                            if (pos.contains(p)) {
                                pair.add(p);
                            }
                        }
                        map_getIntersectionLinesPair.put(new LinesPair(name, n), pair);
                    } else {
                        map_getIntersectionLinesPair.put(new LinesPair(name, n), empty);
                        map_getIntersectionLinesPair.put(new LinesPair(n, name), empty);
                    }
                }
            } else {
                map_getIntersectionLinesPair.put(new LinesPair(name, name), empty);
            }
        }
    }

    private Map<String, List<Position>> getRoad(Map<String, List<Position>> lines, Map<String, List<Position>> positions) {
        Map<String, List<Position>> road = new HashMap<>();
        Set<String> names = lines.keySet();
        for (String name : names) {
            if (positions.containsKey(name)) {
                road.put(name, lines.get(name));
            }
        }
        return road;
    }

    @Override
    public void addBusLine(String busLineName, Position firstPoint, Position lastPoint) {
        List<Position> pos = new ArrayList<>();
        pos.add(firstPoint);
        pos.add(lastPoint);
        lines.put(busLineName, pos);
    }

    @Override
    public void addLineSegment(String busLineName, LineSegment lineSegment) {
        Set<LineSegment> seg;
        if (segments.containsKey(busLineName)) {
            seg = segments.get(busLineName);
        } else {
            seg = new HashSet<>();
        }
        seg.add(lineSegment);
        segments.put(busLineName, seg);
    }

    @Override
    public void findIntersections() {
        map_getLines = findLines();
        Set<String> names = map_getLines.keySet();
        for (String name : names) {
            List<Position> positions = map_getLines.get(name);
            for (int i = 1; i < positions.size() - 1; ++i) {
                if (positions.get(i).getRow() == positions.get(i - 1).getRow() && positions.get(i).getRow() == positions.get(i + 1).getRow()) {
                    var previous = new Position2D(positions.get(i).getCol(), positions.get(i).getRow() - 1);
                    var current = new Position2D(positions.get(i).getCol(), positions.get(i).getRow());
                    var next = new Position2D(positions.get(i).getCol(), positions.get(i).getRow() + 1);

                    containsCheck(name, previous, current, next);
                }
                if (positions.get(i).getCol() == positions.get(i - 1).getCol() && positions.get(i).getCol() == positions.get(i + 1).getCol()) {
                    var previous = new Position2D(positions.get(i).getCol() - 1, positions.get(i).getRow());
                    var current = new Position2D(positions.get(i).getCol(), positions.get(i).getRow());
                    var next = new Position2D(positions.get(i).getCol() + 1, positions.get(i).getRow());

                    containsCheck(name, previous, current, next);
                }
                if (positions.get(i).getCol() == positions.get(i - 1).getCol() + 1 && positions.get(i).getCol() == positions.get(i + 1).getCol() - 1
                        && positions.get(i).getRow() == positions.get(i - 1).getRow() - 1 && positions.get(i).getRow() == positions.get(i + 1).getRow() + 1) {
                    var previous = new Position2D(positions.get(i).getCol() - 1, positions.get(i).getRow() - 1);
                    var current = new Position2D(positions.get(i).getCol(), positions.get(i).getRow());
                    var next = new Position2D(positions.get(i).getCol() + 1, positions.get(i).getRow() + 1);

                    containsCheck(name, previous, current, next);
                }
                if (positions.get(i).getCol() == positions.get(i - 1).getCol() + 1 && positions.get(i).getCol() == positions.get(i + 1).getCol() - 1
                        && positions.get(i).getRow() == positions.get(i - 1).getRow() + 1 && positions.get(i).getRow() == positions.get(i + 1).getRow() - 1) {
                    var previous = new Position2D(positions.get(i).getCol() - 1, positions.get(i).getRow() + 1);
                    var current = new Position2D(positions.get(i).getCol(), positions.get(i).getRow());
                    var next = new Position2D(positions.get(i).getCol() + 1, positions.get(i).getRow() - 1);

                    containsCheck(name, previous, current, next);
                }
                if (positions.get(i).getCol() == positions.get(i - 1).getCol() - 1 && positions.get(i).getCol() == positions.get(i + 1).getCol() + 1
                        && positions.get(i).getRow() == positions.get(i - 1).getRow() + 1 && positions.get(i).getRow() == positions.get(i + 1).getRow() - 1) {
                    var previous = new Position2D(positions.get(i).getCol() - 1, positions.get(i).getRow() - 1);
                    var current = new Position2D(positions.get(i).getCol(), positions.get(i).getRow());
                    var next = new Position2D(positions.get(i).getCol() + 1, positions.get(i).getRow() + 1);

                    containsCheck(name, previous, current, next);
                }
                if (positions.get(i).getCol() == positions.get(i - 1).getCol() - 1 && positions.get(i).getCol() == positions.get(i + 1).getCol() + 1
                        && positions.get(i).getRow() == positions.get(i - 1).getRow() - 1 && positions.get(i).getRow() == positions.get(i + 1).getRow() + 1) {
                    var previous = new Position2D(positions.get(i).getCol() - 1, positions.get(i).getRow() + 1);
                    var current = new Position2D(positions.get(i).getCol(), positions.get(i).getRow());
                    var next = new Position2D(positions.get(i).getCol() + 1, positions.get(i).getRow() - 1);

                    containsCheck(name, previous, current, next);
                }
            }
        }
        getLinesPairs();
        map_getLines = getRoad(map_getLines, map_getIntersectionPosition);
    }

    @Override
    public Map<String, List<Position>> getLines() {
        return map_getLines;
    }

    @Override
    public Map<String, List<Position>> getIntersectionPositions() {
        return map_getIntersectionPosition;
    }

    @Override
    public Map<String, List<String>> getIntersectionsWithLines() {
        return map_getIntersectionWithLines;
    }

    @Override
    public Map<BusLineInterface.LinesPair, Set<Position>> getIntersectionOfLinesPair() {
        return map_getIntersectionLinesPair;
    }
}
