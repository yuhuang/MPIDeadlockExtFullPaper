package methods;

import com.microsoft.z3.Model;
import constant.Status;
import javafx.util.Pair;
import smt.SMTSolver;
import syntax.*;

import javax.swing.*;
import java.io.File;
import java.nio.file.OpenOption;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.logging.FileHandler;


public class Finder {
    String filename;
    boolean infiniteBuffer;
    boolean checkAll;

    public Finder(String filename, boolean infiniteBuffer, boolean checkAll) {
        this.filename = filename;
        this.infiniteBuffer = infiniteBuffer;
        this.checkAll = checkAll;
    }

    public void find(){
        String directoryName = "./src/test/fixtures";
        File Dire = new File(directoryName);
        Program program;
        for (File file : Dire.listFiles()) {
            program = null;
            if (!file.isDirectory()) {
                String regex = filename + ".txt";
                if (!file.getName().matches(regex)) continue;
                System.out.println("-----------------------" + file.getName() + "----------------------");
//                long t1 = System.currentTimeMillis();
                program = new Program(file.getPath(), infiniteBuffer);
                Graph graph = new Graph(program);
                long t1 = System.currentTimeMillis();
                System.out.println("in Graph has " + graph.getVCount() + " Vectors and " + graph.getECount() + " Edges");
                Johnson johnson = new Johnson(graph);
                for (Pattern pattern : johnson.patterns) {
                    pattern.check();
//                    if (pattern.status == Status.SATISFIABLE) {
//                        if (!checkAll) break;
//                    } else if (pattern.status == Status.UNSATISFIABLE) {
//                        johnson.filterSMTNum++;
//                    } else {
//                        johnson.filterNum++;
//                    }
                }
                long t2 = System.currentTimeMillis();
                System.out.println("Program executes " + ((double) (t2 - t1)) / (double) 1000 + "seconds");
                System.out.println(" the patterns number is : " + johnson.patterns.size());
                System.out.println(" filter pattern has : " + johnson.filterNum);
                System.out.println(" filter by smt solver has : " + johnson.filterSMTNum);
                System.out.println("====================================\n");
            }
        }
    }



    public static void main(String[] args) {
        String filename = "((diffusion2d(4|8))|(monte(8|16))|(heat(8|16))|(floyd(8|16))|(ge(8|16))|(integrate(8|10|16))|(is(256|64|128)))";
        Finder finder = new Finder(filename, true, false);
        finder.find();

    }
}
