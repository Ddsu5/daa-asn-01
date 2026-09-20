package src;

import java.util.Arrays;
import java.util.Comparator;

public class ClosestPairSolver {
    public static class Result {
        private final Point first;
        private final Point second;
        private final double distance;

        public Result(Point first, Point second, double distance) {
            this.first = first;
            this.second = second;
            this.distance = distance;
        }

        public Point getFirst() {
            return first;
        }

        public Point getSecond() {
            return second;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public String toString() {
            return first + " <-> " + second + ", distance = " + distance;
        }
    }

    private static final Comparator<Point> BY_X =
            Comparator.comparingDouble(Point::getX)
                    .thenComparingDouble(Point::getY);

    private static final Comparator<Point> BY_Y =
            Comparator.comparingDouble(Point::getY)
                    .thenComparingDouble(Point::getX);

    private static int maxDepth;

    private ClosestPairSolver() {
    }

    public static Result findClosestPair(Point[] points) {
        resetMetrics();

        if (points == null || points.length < 2) {
            throw new IllegalArgumentException(
                    "At least two points are required."
            );
        }

        Point[] byX = points.clone();
        Point[] byY = points.clone();

        Arrays.sort(byX, BY_X);
        Arrays.sort(byY, BY_Y);

        return solve(byX, byY, 1);
    }

    private static Result solve(Point[] byX, Point[] byY, int depth) {
        if (depth > maxDepth) {
            maxDepth = depth;
        }

        int n = byX.length;

        if (n <= 3) {
            return bruteForce(byX);
        }

        int mid = n / 2;
        Point middle = byX[mid];


        Point[] leftX = Arrays.copyOfRange(byX, 0, mid);
        Point[] rightX = Arrays.copyOfRange(byX, mid, n);

// Последняя точка, попавшая в leftX
        Point midPoint = byX[mid - 1];

// Учитываем возможные дубликаты (точки с одинаковыми X и Y)
        int countLeftEquals = 0;
        for (int i = mid - 1; i >= 0; i--) {
            if (BY_X.compare(byX[i], midPoint) == 0) {
                countLeftEquals++;
            } else {
                break;
            }
        }

        Point[] leftY = new Point[leftX.length];
        Point[] rightY = new Point[rightX.length];

        int leftIndex = 0;
        int rightIndex = 0;

        for (Point point : byY) {
            int cmp = BY_X.compare(point, midPoint);
            if (cmp < 0) {
                leftY[leftIndex++] = point;
            } else if (cmp == 0 && countLeftEquals > 0) {
                leftY[leftIndex++] = point;
                countLeftEquals--;
            } else {
                rightY[rightIndex++] = point;
            }
        }

        Result leftResult = solve(leftX, leftY, depth + 1);
        Result rightResult = solve(rightX, rightY, depth + 1);

        Result best = closer(leftResult, rightResult);
        double deltaSquared = best.getDistance() * best.getDistance();

        Point[] strip = new Point[n];
        int stripSize = 0;

        for (Point point : byY) {
            double dx = point.getX() - middle.getX();

            if (dx * dx < deltaSquared) {
                strip[stripSize++] = point;
            }
        }

        for (int i = 0; i < stripSize; i++) {
            for (int j = i + 1; j < stripSize; j++) {
                double dy = strip[j].getY() - strip[i].getY();

                if (dy * dy >= deltaSquared) {
                    break;
                }

                double distanceSquared =
                        strip[i].distanceSquared(strip[j]);

                if (distanceSquared < deltaSquared) {
                    best = new Result(
                            strip[i],
                            strip[j],
                            Math.sqrt(distanceSquared)
                    );
                    deltaSquared = distanceSquared;
                }
            }
        }

        return best;
    }

    private static Result bruteForce(Point[] points) {
        Result best = null;

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double distanceSquared =
                        points[i].distanceSquared(points[j]);

                if (best == null
                        || distanceSquared
                        < best.getDistance() * best.getDistance()) {

                    best = new Result(
                            points[i],
                            points[j],
                            Math.sqrt(distanceSquared)
                    );
                }
            }
        }

        return best;
    }

    private static Result closer(Result a, Result b) {
        return a.getDistance() <= b.getDistance() ? a : b;
    }

    private static void resetMetrics() {
        maxDepth = 0;
    }

    public static int getMaxDepth() {
        return maxDepth;
    }
}
