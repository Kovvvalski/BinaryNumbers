package by.kovalski.binarynumbers.entity;

import by.kovalski.binarynumbers.exception.OutOfRangeException;
import by.kovalski.binarynumbers.service.BinaryOperationService;

import java.util.LinkedList;
import java.util.List;

import static by.kovalski.binarynumbers.service.BinaryOperationService.*;

public class BinaryInteger implements BinaryNumber<Integer> {
  private static final int MAIN_PART_BITS = 31;
  private static final int MIN_VALUE = -2_147_483_647;
  private static final int MAX_VALUE = 2_147_483_647;

  private final List<Boolean> directCode;
  private final List<Boolean> reverseCode;
  private final List<Boolean> complementCode;

  public BinaryInteger(int integer) {
    if (integer < MIN_VALUE) {
      throw new OutOfRangeException("The value " + integer + " is out of range of 32-bit integer representation");
    }
    directCode = BinaryOperationService.createDirectCode(integer, MAIN_PART_BITS);

    if (integer >= 0) {
      reverseCode = directCode;
      complementCode = directCode;
    } else {
      reverseCode = reverseMainPart(directCode);
      complementCode = incrementMainPart(reverseCode);
    }
  }

  private BinaryInteger(List<Boolean> directCode, List<Boolean> reverseCode, List<Boolean> complementCode) {
    this.directCode = directCode;
    this.reverseCode = reverseCode;
    this.complementCode = complementCode;
  }

  public List<Boolean> getDirectCode() {
    return new LinkedList<>(directCode);
  }

  public List<Boolean> getReverseCode() {
    return new LinkedList<>(reverseCode);
  }

  public List<Boolean> getComplementCode() {
    return new LinkedList<>(complementCode);
  }

  public Integer getValue() {
    return BinaryOperationService.getValueFromDirectCode(directCode);
  }


  @Override
  public BinaryNumber<Integer> sum(BinaryNumber<Integer> other) {
    if (other.getClass() != BinaryInteger.class) {
      throw new UnsupportedOperationException("Not supported operation");
    }
    BinaryInteger integer = (BinaryInteger) other;
    int first = getValue();
    int second = integer.getValue();
    if (first + second < MIN_VALUE) {
      throw new OutOfRangeException("The value of " + this + " + " + integer + " is out of range of 32-bit integer representation");
    }
    List<Boolean> complement = binarySum(complementCode, integer.complementCode);
    List<Boolean> direct;
    List<Boolean> reverse;
    List<Boolean> temp;
    if (complement.get(0)) {
      temp = reverseMainPart(complement);
      direct = incrementMainPart(temp);
      reverse = reverseMainPart(direct);
    } else {
      direct = complement;
      reverse = complement;
    }
    return new BinaryInteger(direct, reverse, complement);
  }

  @Override
  public BinaryNumber<Integer> dif(BinaryNumber<Integer> other) {
    if (other.getClass() != BinaryInteger.class) {
      throw new UnsupportedOperationException("Not supported operation");
    }
    BinaryInteger integer = (BinaryInteger) other;
    BinaryNumber<Integer> out;
    if (integer.complementCode.get(0)) {
      List<Boolean> code = incrementMainPart(reverseMainPart(integer.complementCode));
      code.remove(0);
      code.add(0, false);
      out = sum(new BinaryInteger(code, code, code));
    } else {
      List<Boolean> direct = new LinkedList<>(integer.directCode);
      direct.remove(0);
      direct.add(0, true);
      List<Boolean> reverse = reverseMainPart(direct);
      List<Boolean> complement = incrementMainPart(reverse);
      BinaryInteger secondComponent = new BinaryInteger(direct, reverse, complement);
      out = sum(secondComponent);
    }
    return out;
  }

  @Override
  public BinaryNumber<Integer> product(BinaryNumber<Integer> other) {
    if (other.getClass() != BinaryInteger.class) {
      throw new UnsupportedOperationException("Not supported operation");
    }
    BinaryInteger integer = (BinaryInteger) other;
    int first = getValue();
    int second = integer.getValue();
    if (first * second < MIN_VALUE) {
      throw new OutOfRangeException("The value of " + this + " * " + integer + " is out of range of 32-bit integer representation");
    }
    List<Boolean> moduleOfDirect1 = getDirectCode();
    List<Boolean> moduleOfDirect2 = integer.getDirectCode();
    boolean isNotNegative1 = moduleOfDirect1.get(0);
    boolean isNotNegative2 = moduleOfDirect2.get(0);
    moduleOfDirect1.remove(0);
    moduleOfDirect2.remove(0);
    List<Boolean> direct = binaryProduct(moduleOfDirect1, moduleOfDirect2);
    direct.add(0, isNotNegative1 != isNotNegative2);
    List<Boolean> reverse = reverseMainPart(direct);
    List<Boolean> complement = incrementMainPart(reverse);
    return new BinaryInteger(direct, reverse, complement);
  }

  @Override
  public BinaryNumber<Integer> division(BinaryNumber<Integer> other) {
    throw new UnsupportedOperationException("This operation is unsupported yet");
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Boolean b : directCode) {
      stringBuilder.append(b ? 1 : 0);
    }
    return stringBuilder.toString();
  }

  public String getReverse() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Boolean b : reverseCode) {
      stringBuilder.append(b ? 1 : 0);
    }
    return stringBuilder.toString();
  }

  public String getComplement() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Boolean b : complementCode) {
      stringBuilder.append(b ? 1 : 0);
    }
    return stringBuilder.toString();
  }

}
