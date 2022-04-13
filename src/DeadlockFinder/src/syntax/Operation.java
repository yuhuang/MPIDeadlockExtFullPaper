package syntax;

import constant.Constants;
import constant.OPTypeEnum;
import constant.Pair;
import constant.Triple;
//import javafx.util.Pair;

/**
 *
 */
public class Operation implements Comparable,Cloneable{
    public int index;//the rank is index of list which actions with same endpoint
    public int indx;//index in program, total number, which contains bot and barrier
    public int rank;//the rank of the process, Line number
    public int proc;//the rank of process
    public int src;//source
    public int dst;//destination
    public int group;//group
    public int reqID;//the req action's idx, if this op is collective operation, the req is number of element in buffer
    public int root;//collective's root
    public Operation req;//for a wait ;the req is an operation which is witnessed by the wait
    public OPTypeEnum type;//the type is num : "send", "recv", "wait", "barrier", "bot"
    public Operation Nearstwait;//for a recv or a send, this wait is NearestWait

    public Operation(OPTypeEnum type, int rank, int proc, int src, int dst, int group, int reqID) {
        this.type = type;
        this.rank = rank;
        this.proc = proc;
        this.src = src;
        this.dst = dst;
        this.group = group;
        this.reqID = reqID;
        Nearstwait = null;
    }

    public Operation(OPTypeEnum type, int rank, int root, int proc, int group) {//collective operation;
        this.type = type;
        this.rank = rank;
        this.root = root;
        this.proc = proc;
        this.group = group;
    }

    public Operation(OPTypeEnum type, int rank, int indx, int proc) {
        this.type = type;
        this.rank = rank;
        this.indx = indx;
        this.proc = proc;
    }

    public Operation(OPTypeEnum type,  int rank, int proc, Operation req){
        if(type==OPTypeEnum.WAIT){
            this.type = type;
            this.rank = rank;
            this.proc = proc;
            this.req = req;
        }
    }

    public boolean isSend() {
        return (this.type == OPTypeEnum.SEND
                );
    }

    public boolean isRecv() {
        return (this.type == OPTypeEnum.RECV);
    }
    public boolean isIRecv() {
        return (this.type == OPTypeEnum.B_RECV);
    }

    public boolean isWait() { return (this.type == OPTypeEnum.WAIT); }

    public boolean isBarrier() { return (this.type == OPTypeEnum.BARRIER); }

    public boolean isBlocking() {return this.isWait(); }

    public boolean isCollective() { return isBarrier(); }

    public boolean isBot() { return (this.type == OPTypeEnum.BOT); }

    public String getStrInfo(){
        return this.type+" "+this.proc+"_"+this.indx;
    }

    public Object getEndpoint(){
            Pair<Integer, Integer> pair = new Pair<Integer, Integer>(this.dst, this.src);
            return pair;
    }

    @Override
    public int compareTo(Object op) {
        int compareRank = ((Operation) op).indx;
        return this.indx-compareRank;
//        return compareRank-this.indx;
    }

    @Override
    public String toString() {
        if (this.isRecv())
            return this.type+" "+this.indx+" "+this.proc+" "+this.src+" "+" "+this.rank;
        else if(this.isSend())
            return this.type+" "+this.indx+" "+this.proc+" "+this.dst+" "+" "+this.rank;
        else if(this.isWait())
            return this.type+" "+this.indx+" "+this.proc+" "+this.req.rank;
        else if(this.isBarrier())
            return this.type+" "+this.indx+" "+this.proc+" "+this.group+" "+this.rank;
        else
            return this.type+" "+this.indx+" "+this.proc+"_"+this.root+" "+this.group+" "+this.rank;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static void main(String[] args) {
        Operation a1 = new Operation(OPTypeEnum.SEND,0,0,0,1,0,0);
        Operation a2 = new Operation(OPTypeEnum.SEND,1,0,0,1,0,0);
        System.out.println(a1.getEndpoint());
        System.out.println(a2.getEndpoint());
        System.out.println(a1.getEndpoint().equals(a2.getEndpoint()));
    }
}
