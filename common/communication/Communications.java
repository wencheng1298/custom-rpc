package common.communication;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.io.*;

/*
  Communication class is used by both client as well as server. This
  class focuses on converting messages to and from their byte forms.
*/
public class Communications {
  public static final int INT_CODE = 1;
  public static final int DOUBLE_CODE = 2;
  public static final int STRING_CODE = 3;
  public static final int BOOL_CODE = 4;

  // This method converts primitive data type into bytes and returns a byte[]
  private static byte[] convertToBytes(Object attr) {
    byte[] byteAttr = ByteBuffer.allocate(0).array();
    if (attr instanceof Integer) {
      byteAttr = ByteBuffer
          .allocate(Integer.BYTES + Integer.BYTES)
          .putInt(INT_CODE)
          .putInt((int) attr)
          .array();

    } else if (attr instanceof Boolean) {
      // Convert bool to int before converting to byte[]
      int boolAttr = ((Boolean) attr ? 1 : 0);
      byteAttr = ByteBuffer
          .allocate(Integer.BYTES + Integer.BYTES)
          .putInt(BOOL_CODE)
          .putInt(boolAttr)
          .array();

    } else if (attr instanceof String) {
      byte[] str = ((String) attr).getBytes(StandardCharsets.UTF_8);
      byteAttr = ByteBuffer.allocate(Integer.BYTES + Integer.BYTES + str.length)
          .putInt(STRING_CODE)
          .putInt(str.length)
          .put(str)
          .array();

    } else if (attr instanceof Double) {
      byteAttr = ByteBuffer.allocate(Integer.BYTES + Double.BYTES)
          .putInt(DOUBLE_CODE)
          .putDouble((double) attr)
          .array();
    }
    return byteAttr;
  }

  /*
   * marshal takes in a marshallable object and returns the byte[] form of the
   * object
   */
  public byte[] marshal(Marshallable m) {
    ArrayList<byte[]> byteContents = new ArrayList<byte[]>();

    String[] attributes = m.getAllAttributes();
    for (int i = 0; i < attributes.length; i++) {
      Object attr = m.getAttribute(attributes[i]);
      if (attr instanceof Marshallable) {
        byteContents.add(marshal((Marshallable) attr));
      } else {
        byteContents.add(convertToBytes(attr));
      }
    }

    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    for (byte[] content : byteContents) {
      try {
        byteStream.write(content);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    byte[] marshalledMsg = byteStream.toByteArray();

    return marshalledMsg;
  }

  /*
   * unmarshal takes in a marshalled object in the form of a bytebuffer as well as
   * an object of the return type.
   * returnObject is recursively filled in using the information obtained from
   * marshalledMsg.
   */
  public Marshallable unmarshal(ByteBuffer marshalledMsg, Marshallable returnObj) {
    String[] attributes = returnObj.getAllAttributes();
    for (String attribute : attributes) {
      Object attr = returnObj.getAttribute(attribute);
      if (attr instanceof Marshallable) {
        Object val = unmarshal(marshalledMsg, (Marshallable) attr);
        returnObj.setAttribute(attribute, val);
      } else {
        Object val = (Object) getNextValue(marshalledMsg);
        returnObj.setAttribute(attribute, val);
      }
    }

    return returnObj;
  }

  /*
   * Given a bytebuffer, getNextValue retrieves the next value that is stored
   * using our marshaling format.
   * It first reads an integer which represents the data code(eg: int_code). Based
   * on this value, it reads
   * the next section of the buffer and returns the specified object.
   */
  private Object getNextValue(ByteBuffer wrapper) {
    int dataCode;

    dataCode = wrapper.getInt();
    if (dataCode == INT_CODE) {
      return wrapper.getInt();

    } else if (dataCode == DOUBLE_CODE) {
      return wrapper.getDouble();

    } else if (dataCode == STRING_CODE) {
      int len = wrapper.getInt();
      byte[] str_utf16 = new byte[len];
      wrapper.get(str_utf16);
      return new String(str_utf16, StandardCharsets.UTF_8);

    }
    if (dataCode == BOOL_CODE) {
      return wrapper.getInt() == 1 ? true : false;

    }
    return null;
  }

}
