import java.util.*;

public class Main {
    //Static and Global Variables
    public static ArrayList<Interval> Intervals = new ArrayList<>();
    public static ArrayList<Interval> Parents = new ArrayList<>();
    public static ArrayList<Pair> Pairs = new ArrayList<>();
    public static ArrayList<Integer> size = new ArrayList<>();
    public static Stack<Interval> Actives = new Stack<>();
    public static Stack<Interval> InActives = new Stack<>();

    public static List<Interval> sortedIntervals = new ArrayList<>();

    //Interval class for each Line property
    public static class Interval {
        int start;
        int end;
        int weight;
        Interval successor;
        Interval masterParent;
        boolean isActive = true;
        int pathSize = -1;

        public Interval(int start, int end, int weight) {
            this.start = start;
            this.end = end;
            this.weight = weight;
        }
    }

    //Pair class for each Point
    public static class Pair {
        int point;
        Interval interval;
        Boolean isStart = true;

        public Pair(int point, Interval interval, Boolean isStart) {
            this.point = point;
            this.interval = interval;
            this.isStart = isStart;
        }


    }

    //UnionFind Function
    public static Interval find(Interval interval) {
        Interval p = Parents.get(sortedIntervals.indexOf(interval));
        if (p == interval)
            return interval;
        Parents.set(sortedIntervals.indexOf(interval), find(p));
        return Parents.get(sortedIntervals.indexOf(interval));
    }

    //Union function
    static boolean union(Interval x, Interval y) {
        Interval rootX = find(x);
        Interval rootY = find(y);
        if (rootY == rootX)
            return false;
        if (size.get(sortedIntervals.indexOf(rootY)) > size.get(sortedIntervals.indexOf(rootX))) {
            Interval temp = rootX;
            rootX = rootY;
            rootY = temp;
        }
        Parents.set(sortedIntervals.indexOf(rootY), rootX);
        int newSize = Integer.sum(size.get(sortedIntervals.indexOf(rootX)), size.get(sortedIntervals.indexOf(rootY)));
        size.set(sortedIntervals.indexOf(rootX), newSize);
        return true;
    }

    //BucketSort function
    public static List<List<Interval>> bucketSort(List<Interval> intervals) {
        // Find the maximum end value
        int maxEndValue = Integer.MIN_VALUE;
        for (Interval interval : intervals) {
            maxEndValue = Math.max(maxEndValue, interval.end);
        }

        //int numBuckets = maxEndValue + 1;
        int numBuckets = 100000;
        List<List<Interval>> buckets = new ArrayList<>(numBuckets);

        // Initialize buckets
        for (int i = 0; i < numBuckets; i++) {
            buckets.add(new ArrayList<>());
        }

        // Distribute intervals into buckets based on their end values
        for (Interval interval : intervals) {
            buckets.get(interval.end).add(interval);
        }

        // Sort each bucket individually with Insertion Sort Algorithm
        for (List<Interval> bucket : buckets) {
            insertionSort1(bucket);
        }

        return buckets;
    }

    //Insertion Sort Function
    public static void insertionSort1(List<Interval> list) {
        for (int i = 1; i < list.size(); i++) {
            Interval key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).start > key.start) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    //Concat Buckets funcion
    public static List<Interval> concatenateBuckets(List<List<Interval>> buckets) {
        List<Interval> result = new ArrayList<>();
        for (List<Interval> bucket : buckets) {
            result.addAll(bucket);
        }
        return result;
    }

    //Set Successor Function
    public static void setSuccessors(List<Interval> sortedIntervals) {
        Interval lastSeen = Pairs.getLast().interval;
        for (int i = Pairs.size() - 1; i >= 0; i--) {
            if (!Pairs.get(i).isStart) {
                lastSeen = Pairs.get(i).interval;
            } else {
                Pairs.get(i).interval.successor = lastSeen;
            }
        }
    }

    //Bucker sort for pair based on their point
    public static void bucketSortWithInsertionSort(List<Pair> pairs) {
        int numBuckets = 100000;  // Adjust the number of buckets based on your data

        List<List<Pair>> buckets = new ArrayList<>(numBuckets);

        // Initialize buckets
        for (int i = 0; i < numBuckets; i++) {
            buckets.add(new LinkedList<>());
        }

        // Distribute pairs into buckets based on their point values
        for (Pair pair : pairs) {
            int bucketIndex = pair.point / numBuckets;
            buckets.get(bucketIndex).add(pair);
        }

        // Sort each bucket using insertion sort
        for (List<Pair> bucket : buckets) {
            insertionSort(bucket);
        }

        // Concatenate the sorted buckets
        pairs.clear();
        for (List<Pair> bucket : buckets) {
            pairs.addAll(bucket);
        }
    }

    //Insertion Sort for Pair class
    public static void insertionSort(List<Pair> list) {
        for (int i = 1; i < list.size(); i++) {
            Pair key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).point > key.point) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //Number of inputs
        int n = scanner.nextInt();
        scanner.nextLine();

        //Get input
        for (int i = 0; i < n; i++) {
            int start = scanner.nextInt();
            int end = scanner.nextInt();
            int weight = scanner.nextInt();
            scanner.nextLine();

            //Create interval for each line and pair end and start for points
            Interval interval = new Interval(start, end, weight);
            Pair pairStart = new Pair(start, interval, true);
            Pair pairEnd = new Pair(end, interval, false);
            Pairs.add(pairStart);
            Pairs.add(pairEnd);
            Intervals.add(interval);
        }

        //sort intervals based on their end and sort pairs based on their point's number
        List<List<Interval>> buckets = bucketSort(Intervals);
        sortedIntervals = concatenateBuckets(buckets);
        bucketSortWithInsertionSort(Pairs);
        setSuccessors(sortedIntervals);

        //create parent arraylist for set parent for each line  and size arraylist size for unions
        for (Interval interval : sortedIntervals) {
            Parents.add(interval);
        }
        for (int i = 0; i < n; i++) {
            size.add(1);
        }

        //push the first line in active stack and set its pathsize & masterParent
        sortedIntervals.get(0).pathSize = sortedIntervals.get(0).weight;
        Actives.push(sortedIntervals.get(0));
        sortedIntervals.get(0).masterParent = sortedIntervals.get(0);

        //iterate each line for check its active and process it with active and inactive stack
        for (int i = 1; i < sortedIntervals.size(); i++) {

            //if this line successor is equal to itself it means this line isn't active
            if (find(sortedIntervals.get(i).successor) == sortedIntervals.get(i)) {
                sortedIntervals.get(i).isActive = false;
                InActives.push(sortedIntervals.get(i));
            }

            //check if successor this line is active or not
            else if (find(sortedIntervals.get(i).successor).isActive) {
                sortedIntervals.get(i).pathSize = find(sortedIntervals.get(i).successor).masterParent.pathSize + sortedIntervals.get(i).weight;
                while (sortedIntervals.get(i).pathSize<Actives.peek().pathSize){
                    union(sortedIntervals.get(i),Actives.peek());
                    Actives.pop();
                    find(sortedIntervals.get(i)).masterParent = sortedIntervals.get(i);

                }

                while (!InActives.isEmpty()) {
                    union(sortedIntervals.get(i), InActives.peek());
                    InActives.pop();
                }
                Actives.push(sortedIntervals.get(i));
            } else if (!find(sortedIntervals.get(i).successor).isActive) {
                union(find(sortedIntervals.get(i).successor), sortedIntervals.get(i));
            }}


            for (Interval interval : sortedIntervals) {
                if (interval.pathSize == -1 && find(interval).masterParent != null) {
                    interval.pathSize = interval.weight + find(interval).masterParent.pathSize;
                }
            }
            int counterPrint = 1;
            for (Interval interval : sortedIntervals) {
                System.out.println(counterPrint + " " + interval.pathSize);
                counterPrint++;
            }
        }
    }
