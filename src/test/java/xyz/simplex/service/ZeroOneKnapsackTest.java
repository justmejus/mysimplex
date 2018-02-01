package xyz.simplex.service;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZeroOneKnapsackTest {
    @Test
    public void calcSolution() throws Exception {

        ZeroOneKnapsack zeroOneKnapsack= new ZeroOneKnapsack(15,Stream.of(3,9,5,6).collect(Collectors.toList()),Stream.of(6,4,5,10).collect(Collectors.toList()) );
        List<Integer> sol=zeroOneKnapsack.calcSolution();
System.out.println(sol);
    }

}