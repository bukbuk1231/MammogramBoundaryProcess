import java.util.*;

public class Mammograph {

    private int[][] image;
    private final int THRESHOLD = 40, RANGE = 4;
    private final int[][] dirs = {{0, 1}, {1, 0}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private final int[][] coeff = {{-1, 3, -3, 1}, {3, -6, 3, 0}, {-3, 0, 3, 0}, {1, 4, 1, 0}};

    public Mammograph(int[][] image) {
        this.image = image;
    }

    public void findBoundary() {
        int h = image.length, w = image[0].length;
        List<int[]> boundaries = new ArrayList<>();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (image[i][j] == THRESHOLD) {     // could tolerate some errors
                    List<int[]> bound = nonLinearExtrapolation(i, j);
                    boundaries.add(bound.get(bound.size() - 1));
                }
            }
        }
        bspline(boundaries);
    }

    private List<int[]> nonLinearExtrapolation(int si, int sj) {
        List<int[]> bound = new ArrayList<>();
        List<Double> slopes = new ArrayList<>();
        bound.add(new int[]{si, sj});
        while (bound.size() < 4) {
            int[] prev = bound.get(bound.size() - 1);
            List<int[]> cand = new ArrayList<>();
            Queue<int[]> queue = new LinkedList<>();
            Set<String> visited = new HashSet<>();
            queue.offer(prev);
            visited.add(prev[0] + "," + prev[1]);

            while (!queue.isEmpty()) {
                int size = queue.size();
                while (size-- > 0) {
                    int[] cur = queue.poll();
                    for (int[] dir : dirs) {
                        int x = cur[0] + dir[0], y = cur[1] + dir[1];
                        if (x < 0 || x >= image.length || y < 0 || y >= image.length || !visited.add(x + "," + y))
                            continue;
                        int[] nei = new int[]{x, y};
                        queue.offer(nei);
                        int dist = dist(nei, prev);
                        if (dist == RANGE && image[nei[0]][nei[1]] < image[prev[0]][prev[1]]) {
                            cand.add(new int[] {nei[0], nei[1], image[nei[0]][nei[1]]});
                        }
                    }
                }
                if (!cand.isEmpty()) {
                    cand.sort((a, b) -> a[2] - b[2]);
                    int[] median = cand.get(cand.size() / 2);
                    bound.add(new int[]{median[0], median[1]});
                    slopes.add((median[1] - prev[1]) * 1.0 / (median[0] - prev[0]));
                    break;
                }
            }
        }

        double slope = avgSlope(slopes);
        int[] est = extrapolate(bound.get(bound.size() - 1), slope, RANGE);
        bound.add(est);
        return bound;
    }

    private void bspline(List<int[]> boundaries) {
        for (int i = 0; i < boundaries.size(); i++) {
            int[] a = i - 1 < 0 ? new int[] {0, 0} : boundaries.get(i - 1);
            int[] b = boundaries.get(i);
            int[] c = i + 1 >= boundaries.size() ? new int[] {0 ,0} : boundaries.get(i + 1);
            int[] d = i + 2 >= boundaries.size() ? new int[] {0, 0} : boundaries.get(i + 2);
            int[] xs = new int[] {a[0], b[0], c[0], d[0]};
            int[] ys = new int[] {a[1], b[1], c[1], d[1]};

            int[] xcoeff = multiply(coeff, xs);
            int[] ycoeff = multiply(coeff, ys);

            int x = (int)(xcoeff[0] * Math.pow(i - 1, 3) + xcoeff[1] * Math.pow(i, 2) + xcoeff[2] * i + xcoeff[3]);
            int y = (int)(ycoeff[0] * Math.pow(i - 1, 3) + ycoeff[1] * Math.pow(i, 2) + ycoeff[2] * i + ycoeff[3]);
            image[x][y] = 255;
        }
    }

    private int dist(int[] p1, int[] p2) {
        return (int)(Math.sqrt((p1[0] - p2[0]) * (p1[0] - p2[0]) + (p1[1] - p2[1]) * (p1[1] - p2[1])));
    }

    private double avgSlope(List<Double> slopes) {
        double sum = 0;
        for (double s : slopes)
            sum += s;
        return sum / slopes.size();
    }

    private int[] multiply(int[][] coeff, int[] s) {
        int[] res = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s.length; j++) {
                res[i] += coeff[i][j] * s[j];
            }
        }
        return res;
    }

    private int[] extrapolate(int[] D, double slope, int range) {
        
        return null;
    }

    public int[][] getImage() {
        return image;
    }
}
