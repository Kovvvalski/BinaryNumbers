package by.kovalski.binarynumbers.entity;

import by.kovalski.binarynumbers.exception.OutOfRangeException;

import java.util.LinkedList;
import java.util.List;

import static by.kovalski.binarynumbers.service.BinaryOperationService.*;
import static java.lang.Math.pow;

public class FixedPointReal {

  private static final double MAX_VALUE = 16_384.99999F;
  private static final double MIN_VALUE = -16_384.99999F;
  private static final int FRACTIONAL_PART_BITS = 8;
  private static final int INTEGER_PART_BITS = 23;

  private final List<Boolean> directCode;
  private final List<Boolean> reverseCode;
  private final List<Boolean> complementCode;

  public FixedPointReal(double number) {
    if (number > MAX_VALUE || number < MIN_VALUE) {
      throw new OutOfRangeException("The value " + number + " is out of range of 32-bit representation");
    }
    directCode = createDirectCode(number);
    if (number < 0) {
      reverseCode = reverseMainPart(directCode);
      complementCode = incrementMainPart(reverseCode);
    } else {
      reverseCode = directCode;
      complementCode = directCode;
    }
  }

  public FixedPointReal(List<Boolean> directCode, List<Boolean> reverseCode, List<Boolean> complementCode) {
    this.directCode = directCode;
    this.reverseCode = reverseCode;
    this.complementCode = complementCode;
  }

  public FixedPointReal(List<Boolean> directCode) {
    this.directCode = new LinkedList<>(directCode);
    this.reverseCode = reverseMainPart(directCode);
    this.complementCode = incrementMainPart(reverseCode);
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

  public double getValue() {
    double out = 0;
    for (int i = 1; i <= INTEGER_PART_BITS; i++) {
      out += (directCode.get(i) ? 1 : 0) * (int) pow(2, INTEGER_PART_BITS - i);
    }
    for (int i = 1; i <=FRACTIONAL_PART_BITS; i++) {
      out += (directCode.get(INTEGER_PART_BITS + i) ? 1 : 0) * pow(2, -(i));
    }
    if (directCode.get(0)) {
      out *= -1;
    }
    return out;
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


  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i <= INTEGER_PART_BITS; i++) {
      stringBuilder.append(directCode.get(i) ? 1 : 0);
    }
    stringBuilder.append('.');
    for (int i = INTEGER_PART_BITS + 1; i < directCode.size(); i++) {
      stringBuilder.append(directCode.get(i) ? 1 : 0);
    }
    return stringBuilder.toString();
  }

  static List<Boolean> createDirectCode(double number) {
    boolean isNegative = number < 0;
    int intPart = (int) number;
    double fractionalPart = number - intPart;
    List<Boolean> out = new LinkedList<>();
    int counter = 0;
    if (intPart != 0) {
      while (intPart != 0 && counter < INTEGER_PART_BITS) {
        int rem = intPart % 2;
        out.add(0, rem != 0);
        intPart /= 2;
        counter++;
      }
    }
    for (int i = 0; i < INTEGER_PART_BITS - counter; i++) {
      out.add(0, false);
    }

    List<Boolean> fract = new LinkedList<>();
    for (int i = 0; i < FRACTIONAL_PART_BITS; i++) {
      fractionalPart *= 2;
      int bit = (int) fractionalPart;
      fract.add(bit != 0);
      fractionalPart -= bit;
    }
    out.addAll(fract);
    out.add(0, isNegative);
    return out;
  }
}
