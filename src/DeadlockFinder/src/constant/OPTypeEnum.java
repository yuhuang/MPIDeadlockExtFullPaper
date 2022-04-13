package constant;

public enum OPTypeEnum {
    SEND("s"),//non-blocking send, all send is non-blocking send
    B_SEND("bs"),//blocking send
    //non-blocking send

    RECV("r"),//non-blocking recv
    B_RECV("ir"),//blocking recv// there is no blocking receive

    WAIT("w"),
    BOT("bot"),
//
    // all collective operations are blocking
    BARRIER("b");

    public final String optype;

    OPTypeEnum(String optype){
        this.optype = optype;
    }


    public String getOptype(){
        return optype;
    }
}
