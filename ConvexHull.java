import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;

public class ConvexHull {

	// global variables
	static ArrayList<Point> convexhull = new ArrayList<Point>(); // convex hull set
	static ArrayList<Point> randomSet = new ArrayList<>(); // random set of points
	static ArrayList<Point> plotPoints = new ArrayList<>(); // plots the randomly generated points to the graph

	public static void main(String[] args) {
		// random object
		Random rand = new Random();
		int randomValue = rand.nextInt(3, 50); // range for number of points

		// initialization of random set points to Point array
		for (int i = 0; i < randomValue; i++) {
			Point p = new Point(rand.nextInt(100, 700), rand.nextInt(100, 700));
			if (!randomSet.contains(p)) {
				randomSet.add(p);
			}
		}
		// prints all points in the randomly generated set of points
		System.out.println("list of points");
		for (int i = 0; i < randomSet.size(); i++) {
			if (i % 5 == 0) {
				System.out.println();
				System.out.print("(" + randomSet.get(i).x + "," + randomSet.get(i).y + ")\t");
			} else
				System.out.print("(" + randomSet.get(i).x + "," + randomSet.get(i).y + ")\t");
		}
		plotPoints.addAll(randomSet);

		bruteForce(randomSet);
		sortCC();
		// prints brute force convex hull
		System.out.println("\nBrute Force Convex Hull:");
		for (int i = 0; i < convexhull.size(); i++) {
			if (i % 5 == 0) {
				System.out.println();
				System.out.print("(" + convexhull.get(i).x + "," + convexhull.get(i).y + ")\t");
			} else
				System.out.print("(" + convexhull.get(i).x + "," + convexhull.get(i).y + ")\t");
		}

		quickHull(randomSet);
		sortCC();
		// prints quick hull convex hull
		System.out.println("\nquickhull:");
		for (int i = 0; i < convexhull.size(); i++) {
			if (i % 5 == 0) {
				System.out.println();
				System.out.print("(" + convexhull.get(i).x + "," + convexhull.get(i).y + ")\t");
			} else
				System.out.print("(" + convexhull.get(i).x + "," + convexhull.get(i).y + ")\t");
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				convexDisplay();
			}
		});
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// BRUTE FORCE METHOD
	// input: ArrayList of <Point> s
	// output: the convex hull of <Point> in s
	public static void bruteForce(ArrayList<Point> s) {

		double a, b, c; // set the points to use in the equation ax + by =
		for (int i = 0; i < s.size(); i++) {
			for (int j = 0; j < s.size(); j++) {
				if (i == j)
					continue;
				boolean onConvex = true;
				Point pointI = s.get(i);
				Point pointJ = s.get(j);
				a = pointJ.getY() - pointI.getY();
				b = pointI.getX() - pointJ.getX();
				c = pointI.getX() * pointJ.getY() - pointJ.getX() * pointI.getY();
				for (int k = 0; k < s.size(); k++) {
					// check if every k is on one side of the line segment
					if (k == i || k == j)
						continue;
					Point pointK = s.get(k);
					if ((a * pointK.getX()) + (b * pointK.getY()) - c <= 0) {
						onConvex = false;
						break;
					}
				}
				if (onConvex) {
					if (!convexhull.contains(pointI))
						convexhull.add(pointI); // assign i to convex hull
					if (!convexhull.contains(pointJ))
						convexhull.add(pointJ); // assign j to convex hull
				}
			}
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// QUICKHULL METHOD
	// input: ArrayList of <Point> points
	// output: the convex hull of <Point> points
	public static void quickHull(ArrayList<Point> points) {

		int smallestPoint = -1, largestPoint = -1;
		// MAX_VALUE and MIN_VALUE are the largest/smallest possible integers
		int minimumX = Integer.MAX_VALUE;
		int maximumX = Integer.MIN_VALUE;
		if (points.size() < 3) {
			convexhull.addAll(points);
			return;
		}
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).x < minimumX) {
				minimumX = points.get(i).x;
				smallestPoint = i;
			}
			if (points.get(i).x > maximumX) {
				maximumX = points.get(i).x;
				largestPoint = i;
			}
		}
		Point A = points.get(smallestPoint);
		Point B = points.get(largestPoint);
		if (!convexhull.contains(A))
			convexhull.add(A);
		if (!convexhull.contains(A))
			convexhull.add(B);
		points.remove(A);
		points.remove(B);

		ArrayList<Point> leftSet = new ArrayList<Point>(), rightSet = new ArrayList<Point>();

		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			if (pointLocation(A, B, p) == -1)
				leftSet.add(p);
			else if (pointLocation(A, B, p) == 1)
				rightSet.add(p);
		}
		hullSet(A, B, rightSet);
		hullSet(B, A, leftSet);
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// FINDHULL METHOD
	// input: ArrayList of <Point> points
	// output: the convex hull of <Point> points
	public static void hullSet(Point a, Point b, ArrayList<Point> set) {
		int insertPosition = convexhull.indexOf(b);
		if (set.size() == 0)
			return;
		if (set.size() == 1) {
			Point p = set.get(0);
			set.remove(p);
			if (!convexhull.contains(p))
				convexhull.add(insertPosition, p);
			return;
		}
		int dist = Integer.MIN_VALUE;
		int furthestPoint = -1;
		for (int i = 0; i < set.size(); i++) {
			Point p = set.get(i);
			int distance = distance(a, b, p);
			if (distance > dist) {
				dist = distance;
				furthestPoint = i;
			}
		}
		Point P = set.get(furthestPoint);
		set.remove(furthestPoint);
		if (!convexhull.contains(P))
			convexhull.add(insertPosition, P);

		// Determine who's to the left of AP
		ArrayList<Point> leftSetAP = new ArrayList<Point>();
		for (int i = 0; i < set.size(); i++) {
			Point M = set.get(i);
			if (pointLocation(a, P, M) == 1) {
				leftSetAP.add(M);
			}
		}

		// Determine who's to the left of PB
		ArrayList<Point> leftSetPB = new ArrayList<Point>();
		for (int i = 0; i < set.size(); i++) {
			Point M = set.get(i);
			if (pointLocation(P, b, M) == 1) {
				leftSetPB.add(M);
			}
		}
		hullSet(a, P, leftSetAP);
		hullSet(P, b, leftSetPB);

	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// DISTANCE METHOD
	// input: 3 <Points>
	// output: integer value representing the
	public static int distance(Point A, Point B, Point C) {
		int ABx = B.x - A.x;
		int ABy = B.y - A.y;
		int num = ABx * (A.y - C.y) - ABy * (A.x - C.x);
		if (num < 0)
			num = -num;
		return num;
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// FINDHULL METHOD
	// input: ArrayList of <Point> points
	// output: the convex hull of <Point> points
	public static int pointLocation(Point A, Point B, Point P) {
		int cp1 = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
		if (cp1 > 0)
			return 1;
		else if (cp1 == 0)
			return 0;
		else
			return -1;
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// SORTCC METHOD
	// input: ArrayList of <Point> convex hull
	// output: sorts the points in the convex hull set in counter clockwise
	///////////////////////////////////////////////////////////////////////////////////////// direction
	public static void sortCC() {
		Point temp;
		Point c = null;
		double avgX = 0, avgY = 0;
		// finds an arbitrary point inside the convex hull to use as the center
		for (Point i : convexhull) {
			avgX += i.getX();
			avgY += i.getY();
		}
		avgX = avgX / convexhull.size();
		avgY = avgY / convexhull.size();
		c = new Point((int) avgX, (int) avgY);

		for (int i = 0; i < convexhull.size() - 1; i++) {
			int smallestValue = i;// stores index number of the smallest value
			for (int j = i + 1; j < convexhull.size(); j++) {// j is the index being considered
				// the condition finds the arc tan of point i and compares it to the arc tan of
				// point j
				if (Math.atan2((convexhull.get(smallestValue).getY() - c.getY()),
						(convexhull.get(smallestValue).getX() - c.getX())) > Math
								.atan2((convexhull.get(j).getY() - c.getY()), (convexhull.get(j).getX() - c.getX()))) {
					smallestValue = j; // takes considered index and makes it the smallest index
					temp = convexhull.get(j); // moves element value of j into temporary variable
					// switches the elements of the considered slots
					convexhull.set(j, convexhull.get(i));
					convexhull.set(i, temp);
				}
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// SHOW CONVEX METHOD
	// displays the front end for the program
	public static void convexDisplay() {
		// builds the skeleton for the window
		JFrame frame = new JFrame("Brute Force Convex Hull and Quickhull");
		frame.setMinimumSize(new Dimension(800, 800));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.black);
		frame.add(new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setBackground(Color.black);

				// draw points from <Point> randomSet
				g2d.setColor(Color.white);
				for (int p = 0; p < plotPoints.size(); p++) {
					g2d.fillOval(plotPoints.get(p).x - 5, plotPoints.get(p).y - 5, 10, 10);
				}
				// draw lines from <Point> convex hull
				for (int i = 0; i < convexhull.size(); i++) {
					Point p1, p2;
					if (i == convexhull.size() - 1) {
						p1 = convexhull.get(0);
						p2 = convexhull.get(i);
					} else {
						p1 = convexhull.get(i);
						p2 = convexhull.get(i + 1);
					}
					g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}
		});

		frame.pack();
		frame.setVisible(true);
	}

}
