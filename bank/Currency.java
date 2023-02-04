package bank;

public enum Currency {
    SGD(1), USD(2);

    public static final Currency default_val = Currency.SGD;
    private int currency_id = 0;

    Currency(int code) {
        this.currency_id = code;
    }

    public int getId(){
        return this.currency_id;
    }

    public static Currency fromId(int code){
        for (Currency cur: Currency.values()){
            if(cur.currency_id == code)
                return cur;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
    

