import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class RandomGen {

    private Scanner rand_generator;

    private static RandomGen newgen;

    private RandomGen() {
        try {
            rand_generator = new Scanner(new File("random-numbers.txt"));
        
        } catch (FileNotFoundException e) {         
        	e.printStackTrace();
        }
    }
    static int nextInt() {
        if(newgen == null) 
        	newgen = new RandomGen();
        return newgen.rand_generator.nextInt();
    }

}
