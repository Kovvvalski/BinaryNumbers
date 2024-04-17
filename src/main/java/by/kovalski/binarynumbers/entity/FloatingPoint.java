package by.kovalski.binarynumbers.entity;


import static by.kovalski.binarynumbers.service.BinaryOperationService.*;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.pow;

public class FloatingPoint implements BinaryNumber<Double> {
  private static final int MANTIS_BITS = 23;
  private static final int EXPONENT_BITS = 8;
  private static final int BIAS = 127;
  private static final int FRACT_PART_LIMIT = 23;


  private boolean isNull;
  private boolean isNegative;
  private List<Boolean> mantis;
  private List<Boolean> exponent;


  private FloatingPoint() {

  }

  public FloatingPoint(double number) {

    if (number == 0) {
      mantis = new LinkedList<>();
      exponent = new LinkedList<>();
      isNull = true;
      for (int i = 0; i < EXPONENT_BITS; i++) {
        exponent.add(false);
      }
      for (int i = 0; i < MANTIS_BITS; i++) {
        mantis.add(false);
      }
      return;
    }

    isNegative = number < 0;
    int intPart = (int) number;
    double fractionalPart = number - intPart;

    List<Boolean> intBin = createBinaryCode(intPart);
    List<Boolean> fractBin = new LinkedList<>();
    int point = intBin.size();

    while (fractionalPart != 0 && fractBin.size() < FRACT_PART_LIMIT) {
      fractionalPart *= 2;
      int bit = (int) fractionalPart;
      fractBin.add(bit != 0);
      fractionalPart -= bit;
    }

    List<Boolean> mantis = new LinkedList<>(intBin);
    mantis.addAll(fractBin);

    if (mantis.size() > MANTIS_BITS + 1) {
      while (mantis.size() != MANTIS_BITS + 1) {
        mantis.remove(mantis.size() - 1);
      }
    }

    if (mantis.size() < MANTIS_BITS + 1) {
      while (mantis.size() != MANTIS_BITS + 1) {
        mantis.add(false);
      }
    }

    int exponent = 0;
    if (point == 1) {
      while (!mantis.get(0)) {
        mantis.remove(0);
        exponent--;
      }
    } else {
      exponent += point - 1;
    }

    if (mantis.size() > MANTIS_BITS + 1) {
      while (mantis.size() != MANTIS_BITS + 1) {
        mantis.remove(mantis.size() - 1);
      }
    }

    if (mantis.size() < MANTIS_BITS + 1) {
      while (mantis.size() != MANTIS_BITS + 1) {
        mantis.add(false);
      }
    }

    mantis.remove(0);


    this.mantis = mantis;
    this.exponent = createBinaryCode(BIAS + exponent, EXPONENT_BITS);
  }

  private FloatingPoint(boolean isNull, boolean isNegative, List<Boolean> mantis, List<Boolean> exponent) {
    this.isNull = isNull;
    this.isNegative = isNegative;
    this.mantis = mantis;
    this.exponent = exponent;
  }

  public Double getValue() {
    double out = 0;
    List<Boolean> mantis = new LinkedList<>(this.mantis);
    mantis.add(0, true);
    int exponent = getValueFromBinaryCode(this.exponent);
    exponent -= BIAS;
    int point;
    if (exponent > 0) {
      point = exponent + 1;
    } else {
      point = 1;
      for (int i = 0; i < -1 * exponent; i++) {
        mantis.add(0, false);
      }
    }

    if (point > mantis.size()) {
      int toAdd = point - mantis.size();
      for (int i = 0; i < toAdd; i++) {
        mantis.add(false);
      }
    }
    for (int i = 0; i < point; i++) {
      out += (mantis.get(i) ? 1 : 0) * (int) pow(2, point - i - 1);
    }

    for (int i = point; i < mantis.size(); i++) {
      out += (mantis.get(i) ? 1 : 0) * pow(2, -(i + 1 - point));
    }
    if (isNegative) {
      out *= -1;
    }
    return out;
  }

  public FloatingPoint sum(BinaryNumber<Double> other) {
    if (other.getClass() != FloatingPoint.class) {
      throw new NumberFormatException("Not correct other object");
    }

    FloatingPoint floatingPoint = (FloatingPoint) other;
    if (isNull) {
      return new FloatingPoint(floatingPoint.isNull, floatingPoint.isNegative, floatingPoint.mantis, floatingPoint.exponent);
    }

    FloatingPoint out = new FloatingPoint();
    int otherExponent = getValueFromBinaryCode(floatingPoint.exponent);
    otherExponent -= BIAS;
    int thisExponent = getValueFromBinaryCode(this.exponent);
    thisExponent -= BIAS;

    int expDif = Math.abs(thisExponent - otherExponent);

    int thisPoint = 1;
    int otherPoint = 1;

    List<Boolean> thisCode = new LinkedList<>(mantis);
    thisCode.add(0, true);
    List<Boolean> otherCode = new LinkedList<>(floatingPoint.mantis);
    otherCode.add(0, true);

    if (thisExponent < otherExponent) {
      otherPoint += expDif;
      otherExponent -= expDif;
    } else {
      thisPoint += expDif;
      thisExponent -= expDif;
    }

    int pointDif = Math.abs(thisPoint - otherPoint);

    if (thisPoint < otherPoint) {
      for (int i = 0; i < pointDif; i++) {
        thisCode = shiftToTheRight(thisCode);
        thisPoint++;
      }
    } else {
      for (int i = 0; i < pointDif; i++) {
        otherCode = shiftToTheRight(otherCode);
        otherPoint++;
      }
    }

    thisCode.add(0, false);// adding buffer bits
    otherCode.add(0, false);

    int sumPoint = thisPoint + 1;
    int sumExponent = thisExponent;

    if (isNegative) {//adding sign bits
      thisCode.add(0, true);
      thisCode = reverseMainPart(thisCode);
      thisCode = incrementMainPart(thisCode);
    } else {
      thisCode.add(0, false);
    }

    if (floatingPoint.isNegative) {
      otherCode.add(0, true);
      otherCode = reverseMainPart(otherCode);
      otherCode = incrementMainPart(otherCode);
    } else {
      otherCode.add(0, false);
    }

    List<Boolean> sum = binarySum(thisCode, otherCode);

    if (sum.get(0)) {
      out.isNegative = true;
      sum = reverseMainPart(sum);
      sum = incrementMainPart(sum);
    } else {
      out.isNegative = false;
    }

    sum.remove(0);

    if (sum.get(0)) {
      sum.remove(sum.size() - 1);
    } else {
      sum.remove(0);
      sumPoint--;
    }

    if (sum.stream().noneMatch(o -> o)) {
      out.isNull = true;
      out.isNegative = false;
      out.mantis = new LinkedList<>();
      out.exponent = new LinkedList<>();
      for (int i = 0; i < MANTIS_BITS; i++) {
        out.mantis.add(false);
      }
      for (int i = 0; i < EXPONENT_BITS; i++) {
        out.exponent.add(false);
      }
      return out;
    }

    while (!sum.get(0) && sumPoint > 1) {
      sum.remove(0);
      sumPoint--;
    }

    if (sum.get(0)) {
      sumExponent += sumPoint - 1;
    } else {
      while (!sum.get(0)) {
        sum.remove(0);
        sumExponent--;
      }
    }

    sum.remove(0);

    while (sum.size() != MANTIS_BITS) {
      sum.add(false);
    }

    out.exponent = createBinaryCode(127 + sumExponent, EXPONENT_BITS);
    out.mantis = sum;
    return out;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(isNegative ? 1 : 0);
    stringBuilder.append('_');
    for (Boolean b : exponent) {
      stringBuilder.append(b ? 1 : 0);
    }
    stringBuilder.append('_');
    for (Boolean b : mantis) {
      stringBuilder.append(b ? 1 : 0);
    }
    return stringBuilder.toString();
  }

  @Override
  public BinaryNumber<Double> dif(BinaryNumber<Double> other) {
    throw new UnsupportedOperationException("This operation is unsupported yet");
  }

  @Override
  public BinaryNumber<Double> product(BinaryNumber<Double> other) {
    throw new UnsupportedOperationException("This operation is unsupported yet");
  }

  @Override
  public BinaryNumber<Double> division(BinaryNumber<Double> other) {
    throw new UnsupportedOperationException("This operation is unsupported yet");
  }
}
