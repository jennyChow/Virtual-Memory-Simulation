import java.util.Scanner;

/**
 * Created by jingqiuzhou on 6/1/16.
 */
public class PageReplaceSimulator {

    public static void main(String args[]) {

        Scanner inputScanner = new Scanner(System.in);
        System.out.println("Input your algorithm name: fifo/lru/nru");
        String algorithmName = inputScanner.nextLine();

        System.out.println("Input your input file name:");
        String inputFile = inputScanner.nextLine();

        ReplacementAlgorithmBase algorithm = null;
        if (algorithmName.equals("fifo")) {
            for (int i = 80; i <= 240; i += 20) {
                System.out.println("=========Frame number: " + i + " =================");
                algorithm = new FIFO(i);
                algorithm.read(inputFile);
                algorithm.printStats();
            }
        } else if (algorithmName.equals("lru")) {
            for (int i = 120; i <= 400; i += 20) {
                System.out.println("=========Frame number: " + i + " =================");
                algorithm = new LRU(i);
                algorithm.read(inputFile);
                algorithm.printStats();
            }
        } else if (algorithmName.equals("nru")) {
            for (int i = 80; i <= 240; i += 20) {
                System.out.println("=========Frame number: " + i + " =================");
                algorithm = new NRU(i, 1000);
                algorithm.read(inputFile);
                algorithm.printStats();
            }
        } else {
            System.out.println("Arguments not valid! Please try again!");
            System.exit(1);
        }

    }
}
