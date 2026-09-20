package src;

import tests.CorrectnessTests;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== STEP 1: RUNNING CORRECTNESS TESTS ===");
        CorrectnessTests.main(args);

        System.out.println("\n=== STEP 2: RUNNING BENCHMARK EXPERIMENTS ===");
        Experiment.main(args);
    }
}