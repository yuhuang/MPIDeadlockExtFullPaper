package methods;

import constant.OPTypeEnum;
import constant.Status;
import constant.Pair;
import syntax.*;
import syntax.Process;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

public class AbstractMachine {

    public Hashtable<Integer, Operation> pattern;
    public Pattern candidate;
    public Program program;

    int tracker[];
    int indicator[];
    Hashtable<Object, Integer> recvInShape;
    Hashtable<Object, Integer> sendInShape;

    boolean deadlockFound;

    public AbstractMachine(Program program, Pattern pattern) {
        this.program = program;
        this.candidate = pattern;
        this.pattern = pattern.patternTable;
        deadlockFound = false;

    }

    public Status execute() {
        //init
        tracker = new int[program.getSize()];
        indicator = new int[program.getSize()];
        recvInShape = new Hashtable<>();
        sendInShape = new Hashtable<>();
        //initialize each indicator of process
        for (int i = 0; i < program.getSize(); i++) {
            if (!this.pattern.containsKey(i)) {
                indicator[i] = -1;
            } else {
                int ind = program.get(i).ops.indexOf(this.pattern.get(i));
                if (ind==-1) indicator[i] = program.get(i).ops.size();
                else indicator[i] = ind;
//                indicator[i] = program.processes.get(i).ToPoint(pattern.get(i));
            }
            tracker[i] = 0;
        }
        //start
        while (true) {
            int old_tracker[] = tracker.clone();

            /*if (reachedControlPoint() == Status.REACHABLE) {
                candidate.tracker = tracker.clone();
                candidate.DeadlockCandidate = true;
                return Status.REACHABLE;
            }*/

            for (Process process : program.getAllProcesses()) {
                while (true) {
//                    tracker reach the end of the process
                    if (tracker[process.rank] == process.Size()) break;

                    Operation operation = process.getOP(tracker[process.rank]);
//                    System.out.println("[ABSTRACT MACHINE]: NOW CHECKING "+operation+" ");
//
                    if (tracker[process.rank] == indicator[process.rank]) {
//                        System.out.print(" -> reach! \n");
//                        assert operation.isWait() || operation.isBarrier();
//                        System.out.println("[ABSTRACT MACHINE]: * NOW PROCESS "+process.rank+ " STOP AT "+operation.getStrInfo());
                        break;//stop at this operation which is recorded by the indicator in this process
                    }

                    if (operation.isSend()) {
                        appendOpInShape(operation);
                        tracker[process.rank]++;
//                        System.out.print(" -> succe! \n");

                    } else if (operation.isRecv()) {
                        appendOpInShape(operation);
                        tracker[process.rank]++;
//                        System.out.print(" -> succe! \n");

                    } else if (operation.isWait()) {
                        Operation req = operation.req;
                        assert req.isRecv() || req.isSend();
                        if (req.isRecv()) {
                            if (!checkAvailable(req)){
//                                System.out.print(" -> failed \n");
                                break;
                            }

                        } else if (req.isSend()) {

                        }
                        tracker[process.rank]++;
//                        System.out.print(" -> succe! \n");
//                        break;

                    } else if (operation.isBarrier()) {
                        boolean allisBarrier = true;
                        for (Process pro : program.getAllProcesses()) {
                            if (!pro.getOP(tracker[pro.rank]).isBarrier()) allisBarrier = false;
                        }
                        if (!allisBarrier) {
                            break;//if there is operation which is not barrier, then break; stop;
                        } else {
                            for (int i = 0; i < program.getSize(); i++) {
                                if (tracker[1] < program.get(i).Size() && tracker[1] < indicator[i])
                                    tracker[i]++;
                            }
                        }
                    } else if (operation.isBot()) {
                        System.out.println("[ABSTRACT MACHINE]: ERROR! THERE IS A BOT!");
                        tracker[process.rank]++;
                    }

//                    if (indicator[process.rank] == -1) break;
                }
//                find the deadlock or reach all the control points;
            }
            boolean hasChange = false;
            for (int i = 0; i < program.getSize(); i++) {
                if (old_tracker[i] != tracker[i]) {
                    hasChange = true;
                    break;
                }
            }
            if (!hasChange) {
                return reachedControlPoint();
            }
        }
    }

    private void appendOpInShape(Operation operation) {
        if (!recvInShape.containsKey(operation.getEndpoint())) recvInShape.put(operation.getEndpoint(), 0);
        if (!sendInShape.containsKey(operation.getEndpoint())) sendInShape.put(operation.getEndpoint(), 0);
        Pair<Integer, Integer> pair = new Pair<>(operation.dst, -1);
        if (!sendInShape.containsKey(pair)) sendInShape.put(pair, 0);
        if (!recvInShape.containsKey(pair)) recvInShape.put(pair, 0);

        if (operation.isSend()) {
            sendInShape.put(operation.getEndpoint(), sendInShape.get(operation.getEndpoint()) + 1);
            sendInShape.put(pair, sendInShape.get(pair) + 1);


        } else if (operation.isRecv()) {
            recvInShape.put(operation.getEndpoint(), recvInShape.get(operation.getEndpoint()) + 1);

        }
    }

    boolean checkAvailable(Operation operation){
        Pair<Integer,Integer> pair = new Pair<>(operation.dst,-1);
        if (operation.isRecv()){
            if (operation.src!=-1){
                return sendInShape.get(operation.getEndpoint())>recvInShape.get(operation.getEndpoint())
                        && sendInShape.get(pair) > recvInShape.get(pair)+recvInShape.get(operation.getEndpoint());
            }else {
                int totalRecvNum = 0;
                for (int i = -1; i < program.getSize();i++){
                    Pair<Integer,Integer> pair1 = new Pair<>(operation.dst,i);
                    if (recvInShape.containsKey(pair1)) totalRecvNum = totalRecvNum+recvInShape.get(pair1);
                }
                return sendInShape.get(operation.getEndpoint()) > totalRecvNum;
            }
        }else if (operation.isSend()){
            int totalRecvNum = 0;
            for (int i = -1; i < program.getSize();i++){
                Pair<Integer,Integer> pair1 = new Pair<>(operation.dst,i);
                if (recvInShape.containsKey(pair1)) totalRecvNum = totalRecvNum+recvInShape.get(pair1);
            }
            return sendInShape.get(operation.getEndpoint()) < totalRecvNum;
        }

        return false;
    }



    Status reachedControlPoint() {
        for (int i = 0; i < program.getSize(); i++) {
//            System.out.println("process_"+i+" indicator = "+indicator[i]+" tracker = "+tracker[i]);
            if (indicator[i] != -1 && indicator[i] != tracker[i]) {
//                System.out.println("[ABSTRACT MACHINE]: SORRY! CANNOT REACH THE CONTROL POINT!");
                return Status.UNREACHABLE;
            }
        }
        for (int i = 0; i < program.getSize(); i++){
            if(tracker[i]<program.get(i).Size()) {
                Operation operation = program.get(i).getOP(tracker[i]).req;
                if (pattern.keySet().contains(i)) {
                    if (!mayDeadlock(operation)) {
                        return Status.UNREACHABLE;
                    }
                    continue;
                }
            }
        }

//        System.out.println("[ABSTRACT MACHINE]: REACH ALL CONTROL POINTS!");
        candidate.setTracker(tracker);
        return Status.REACHABLE;
    }

    boolean mayDeadlock(Operation operation){
        if (operation.isRecv()){
            Pair<Integer,Integer> pair = new Pair<>(operation.dst, -1);
            int sendNum = sendInShape.get(operation.getEndpoint());
            int recvNum = recvInShape.get(operation.getEndpoint());
            if (operation.src!=-1) recvNum += recvInShape.get(pair);
            return sendNum<=recvNum;
        }else if (operation.isSend()){
            Pair<Integer,Integer> pair = new Pair<>(operation.dst, -1);
            int sendNum = 0;
            for (int i = -1; i < program.getSize();i++){
                Pair<Integer,Integer> pair1 = new Pair<>(operation.dst,i);
                if (sendInShape.containsKey(pair1)) sendNum = sendNum+sendInShape.get(pair1);
            }
            int recvNum = recvInShape.get(operation.getEndpoint());
            if (operation.src!=-1) recvNum += recvInShape.get(pair);
            return recvNum <= sendNum;
        }
        return false;
    }

}
