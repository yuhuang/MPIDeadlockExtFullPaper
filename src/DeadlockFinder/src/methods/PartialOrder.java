package methods;

import constant.OPTypeEnum;
import syntax.Operation;
import syntax.Process;
import syntax.Program;

import java.util.HashMap;
import java.util.LinkedList;

public class PartialOrder {

    public HashMap<Operation, LinkedList<Operation>> partialOrderMap = new HashMap<>();

    public PartialOrder(Program program){

    }

    public void generatePOMap(Program program){
        for (Process process : program.processes){
            Operation lastBlockOp = null;
            for (Operation op : process.ops){
                if (lastBlockOp == null){
                    if(op.isBlocking()){
                        lastBlockOp = op;
                    }
                }else{
                    
                }
            }
        }
    }

}
