package methods;

import com.microsoft.z3.Model;
import constant.Status;
import smt.SMTSolver;
import syntax.*;
import syntax.Process;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;

public class Check {

    public static Status checkPattern(Program program, Pattern pattern) {
//        pattern.printPattern();
//        System.out.println("begin the abstract machine :");
        AbstractMachine abstractMachine = new AbstractMachine(program, pattern);
        if (abstractMachine.execute() == Status.REACHABLE) {

//            System.out.println("after abstract machine, the tracker：" + Arrays.toString(pattern.tracker));
//            System.out.println("[ABSTRACT MACHINE] GOOD! CHECK THIS CYCLE IS DEADLOCK CANDIDATE!\n");
            long t1 = System.currentTimeMillis();
            Model model = null;

            SMTSolver solver = new SMTSolver(program, pattern);
            solver.encode();
            model = solver.check();
//                solver.printAllExprs();
//                solver.displayExprs();

            long t2 = System.currentTimeMillis();
//            System.out.println("SMT Solver executes " + ((double)(t2-t1))/(double)1000 + "seconds");
            if (model != null) {
//                System.out.println(" SAT model is :"+model);
                System.out.println("[FINDER]: SAT! Deadlock detected for " + pattern.patternTable.values());
//                pattern.printPattern();

                pattern.DeadlockCandidate = true;
                return Status.SATISFIABLE;
            } else {
                System.out.println("[FINDER]: UNSAT! No deadlock is found for pattern:" + pattern.patternTable.values());
//                pattern.printPattern();
                pattern.DeadlockCandidate = false;
                return Status.UNSATISFIABLE;
            }
        } else {
//            System.out.print(".");
//            System.out.println("[ABSTRACT MACHINE]: filter : " + pattern.patternTable.values());
        }
        return Status.UNREACHABLE;
    }



}
