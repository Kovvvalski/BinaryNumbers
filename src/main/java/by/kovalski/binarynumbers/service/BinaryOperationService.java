package by.kovalski.binarynumbers.service;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.pow;

public class BinaryOperationService {

  public static List<Boolean> reverseMainPart(List<Boolean> directCode) {
    List<Boolean> out = new LinkedList<>();
    out.add(directCode.get(0));
    for (int i = 1; i < directCode.size(); i++) {
      out.add(!directCode.get(i));
    }
    return out;
  }

  public static List<Boolean> incrementMainPart(List<Boolean> reverseCode) {
    List<Boolean> out = new LinkedList<>(reverseCode);
    boolean carry = true;
    for (int i = out.size() - 1; i >= 1; i--) {
      if (!out.get(i)) {
        out.remove(i);
        out.add(i, true);
        break;
      }
      out.remove(i);
      out.add(i, false);
    }
    return out;
  }

  public static List<Boolean> shiftToTheLeft(List<Boolean> code) {
    List<Boolean> out = new LinkedList<>();
    for (int i = 1; i < code.size(); i++) {
      out.add(code.get(i));
    }
    out.add(false);
    return out;
  }

  public static List<Boolean> shiftToTheRight(List<Boolean> code) {
    List<Boolean> out = new LinkedList<>();
    out.add(false);
    for (int i = 0; i < code.size() - 1; i++) {
      out.add(code.get(i));
    }
    return out;
  }

  public static List<Boolean> createDirectCode(int integer, int mainPartBitsNumber) {
    List<Boolean> out = createBinaryCode(integer);
    int toAdd = mainPartBitsNumber - out.size();
    for (int i = 0; i < toAdd; i++) {
      out.add(0, false);
    }
    out.add(0, integer < 0);
    return out;
  }

  public static List<Boolean> createBinaryCode(int integer) {
    List<Boolean> out = new LinkedList<>();
    if (integer != 0) {
      while (integer != 0) {
        int rem = integer % 2;
        out.add(0, rem != 0);
        integer /= 2;
      }
    } else {
      out.add(false);
    }
    return out;
  }

  public static List<Boolean> createBinaryCode(int integer, int bits) {
    List<Boolean> out = createBinaryCode(integer);
    int toAdd = bits - out.size();
    for (int i = 0; i < toAdd; i++) {
      out.add(0, false);
    }
    return out;
  }

  public static List<Boolean> binaryProduct(List<Boolean> binary1, List<Boolean> binary2) {
    List<List<Boolean>> products = new LinkedList<>();
    int counter = binary1.size() - 1;
    for (int i = binary2.size() - 1; i >= 0; i--) {
      List<Boolean> product = new LinkedList<>();
      for (int j = binary1.size() - 1; j >= 0; j--) {
        boolean toAdd1 = binary1.get(j);
        boolean toAdd2 = binary2.get(i);
        product.add(0, toAdd1 && toAdd2);
      }
      for (int j = 0; j < counter; j++) {
        product.add(0, false);
      }
      for (int j = 0; j < binary1.size() - 1 - counter; j++) {
        product.add(false);
      }
      counter--;
      products.add(product);
    }
    List<Boolean> out = new LinkedList<>();
    for (int i = 0; i < binary1.size() * 2 - 1; i++) {
      out.add(false);
    }
    for (List<Boolean> product : products) {
      out = binarySum(out, product);
    }
    for (int i = 0; i < binary1.size() - 1; i++) {
      out.remove(0);
    }
    return out;
  }

  public static List<Boolean> binarySum(List<Boolean> binary1, List<Boolean> binary2) {
    List<Boolean> out = new LinkedList<>();
    boolean flag = false;
    for (int i = binary1.size() - 1; i >= 0; i--) {
      if (binary1.get(i) && binary2.get(i)) {
        out.add(0, flag);
        flag = true;
      } else if ((binary1.get(i) && !binary2.get(i)) || (binary2.get(i) && !binary1.get(i))) {
        out.add(0, !flag);
      } else if (!binary1.get(i) && !binary2.get(i)) {
        out.add(0, flag);
        flag = false;
      }
    }
    return out;
  }

  public static int getValueFromBinaryCode(List<Boolean> code) {
    int res = 0;
    for (int i = 0; i < code.size(); i++) {
      res += (code.get(i) ? 1 : 0) * (int) pow(2, code.size() - i - 1);
    }
    return res;
  }

  public static int getValueFromDirectCode(List<Boolean> directCode) {
    List<Boolean> copy = new LinkedList<>(directCode);
    copy.remove(0);
    int res = getValueFromBinaryCode(copy);
    if (directCode.get(0)) {
      res *= -1;
    }
    return res;
  }

  public static List<Boolean> shiftToTheRightComplementCode(List<Boolean> complementCode) {
    complementCode = reverseMainPart(complementCode);
    complementCode = incrementMainPart(complementCode);
    complementCode = shiftToTheRight(complementCode);
    return incrementMainPart(reverseMainPart(complementCode));
  }

  public static List<Boolean> binaryDivision(List<Boolean> code1, List<Boolean> code2) {
    if(code2.stream().noneMatch(o -> o)){
      throw new UnsupportedOperationException("Null division");
    }
    boolean isNegative = (code1.get(0) != code2.get(0));
    code1.remove(0);
    code1.add(0, false);
    List<Boolean> complement2 = new LinkedList<>(code2);
    complement2.remove(0);
    complement2.add(0, true);
    complement2 = reverseMainPart(complement2);
    complement2 = incrementMainPart(complement2);
    List<Boolean> dif = new LinkedList<>(code1);
    List<Boolean> lastPositiveDif = new LinkedList<>(dif);
    int counter = 0;
    while (!dif.get(0) && dif.stream().anyMatch(o -> o)) {
      lastPositiveDif = new LinkedList<>(dif);
      dif = binarySum(dif, complement2);
      counter++;
    }
    boolean fractIsNull = dif.stream().noneMatch(o -> o);
    if (!fractIsNull) {
      counter--;
    }

    List<Boolean> two = createBinaryCode(2, 32);
    dif = lastPositiveDif;

    List<Boolean> fractRes = new LinkedList<>();

    dif = binarySum(dif, complement2);

    for (int i = 0; i < 8; i++) {
      if (dif.get(0)) {
        List<Boolean> abs2 = incrementMainPart(reverseMainPart(complement2));
        abs2.remove(0);
        abs2.add(0, false);
        dif = binarySum(shiftToTheLeft(dif), abs2);
      } else {
        dif = binarySum(shiftToTheLeft(dif), complement2);
      }

      if (dif.get(0)) {
        fractRes.add(false);
      } else {
        fractRes.add(true);
      }
    }
    List<Boolean> out = createBinaryCode(counter, 24);
    out.remove(0);
    out.add(0, isNegative);
    out.addAll(fractRes);
    return out;
  }
}
