package common.constants;

public enum Status {
  BAD_REQUEST(1),
  MALFORMED(2),
  INTERNAL_ERR(3),
  OK(4);
  

  private int statusCode;

  Status(int statusCode) {
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return this.statusCode;
  }

  public static Status fromCode(int code) {
    for (Status status : Status.values()) {
      if (status.statusCode == code)
        return status;
    }
    return null;
  }

  @Override
  public String toString() {
    return String.valueOf(statusCode);
  }
}
