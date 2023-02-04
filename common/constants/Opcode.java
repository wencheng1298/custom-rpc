package common.constants;

public enum Opcode {
    NEW_ACCOUNT(1), CLOSE_ACCOUNT(2), WITHDRAW(3), DEPOSIT(4), TRANSFER(5), CHECK_BALANCE(6), CHECK_LOGS(7),
    MONITOR(8);

    private int operation_code = 0;

    Opcode(int code) {
        this.operation_code = code;
    }

    public int getId() {
        return this.operation_code;
    }

    public static Opcode fromCode(int code) {
        for (Opcode opc : Opcode.values()) {
            if (opc.operation_code == code)
                return opc;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
