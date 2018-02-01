package xyz.simplex.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ZeroOneKnapsack {

    protected List<Integer> weights  = new ArrayList<Integer>();
    protected List<Integer> values  = new ArrayList<Integer>();

    protected int maxWeight        = 0;
    protected int solutionWeight   = 0;
    protected int profit           = 0;
    protected boolean calculated   = false;

    public ZeroOneKnapsack() {}

    public ZeroOneKnapsack(int capacity, List<Integer> _values, List<Integer> _weights) {
        values = _values;
        weights = _weights;
        maxWeight = capacity;
    }



    // calculte the solution of 0-1 knapsack problem with dynamic method:
    public List<Integer> calcSolution() {
        List<Integer> solution=new ArrayList<>();
        Integer[] wt = weights.toArray(new Integer[weights.size()]);
        Integer[] val = weights.toArray(new Integer[weights.size()]);
        int N = weights.size(); // Get the total number of items. Could be wt.length or val.length. Doesn't matter
        int[][] V = new int[N + 1][maxWeight + 1]; //Create a matrix. Items are in rows and weight at in columns +1 on each side
        //What if the knapsack's capacity is 0 - Set all columns at row 0 to be 0
        for (int col = 0; col <= maxWeight; col++) {
            V[0][col] = 0;
        }
        //What if there are no items at home.  Fill the first row with 0
        for (int row = 0; row <= N; row++) {
            V[row][0] = 0;
        }
        for (int item=1;item<=N;item++){
            //Let's fill the values row by row
            for (int weight=1;weight<=maxWeight;weight++){
                //Is the current items weight less than or equal to running weight
                if (wt[item-1]<=weight){
                    //Given a weight, check if the value of the current item + value of the item that we could afford with the remaining weight
                    //is greater than the value without the current item itself
                    V[item][weight]=Math.max (val[item-1]+V[item-1][weight-wt[item-1]], V[item-1][weight]);
                    if(!solution.contains(item-1))
                        solution.add(item-1);
                }
                else {
                    //If the current item's weight is more than the running weight, just carry forward the value without the current item
                    V[item][weight]=V[item-1][weight];
                }
            }
        }
        //Printing the matrix
//        for (int[] rows : V) {
//            for (int col : rows) {
//                System.out.format("%5d", col);
//            }
//            System.out.println();
//        }


        return solution;
    }







} // class
