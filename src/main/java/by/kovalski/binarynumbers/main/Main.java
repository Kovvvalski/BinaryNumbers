package by.kovalski.binarynumbers.main;

import by.kovalski.binarynumbers.entity.BinaryInteger;
import by.kovalski.binarynumbers.entity.BinaryNumber;
import by.kovalski.binarynumbers.entity.FixedPointReal;
import by.kovalski.binarynumbers.entity.FloatingPoint;
import by.kovalski.binarynumbers.service.BinaryOperationService;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    try {
      String num;
      Scanner scanner = new Scanner(System.in);
      System.out.println("Write 1 int");
      num = scanner.next();
      BinaryInteger aInt = new BinaryInteger(Integer.parseInt(num));
      System.out.println("Write 2 int");
      num = scanner.next();
      BinaryInteger bInt = new BinaryInteger(Integer.parseInt(num));
      BinaryNumber<Integer> sumInt = aInt.sum(bInt);
      System.out.println("Result of sum:" + '\n' + sumInt + '\n' + sumInt.getValue() + "\n---------------");

      BinaryNumber<Integer> difInt = aInt.dif(bInt);
      System.out.println("Result of dif:" + '\n' + difInt + '\n' + difInt.getValue() + "\n---------------");

      BinaryNumber<Integer> productInt = aInt.product(bInt);
      System.out.println("Result of product:" + '\n' + productInt + '\n' + productInt.getValue() + "\n---------------");

      FixedPointReal divInt = new FixedPointReal(BinaryOperationService.binaryDivision(aInt.getDirectCode(), bInt.getDirectCode()));
      System.out.println("Result of division:" + '\n' + divInt + '\n' + divInt.getValue() + "\n---------------");

      System.out.println("Write 1 float");
      num = scanner.next();
      FloatingPoint aFloat = new FloatingPoint(Double.parseDouble(num));
      System.out.println("Write 2 float");
      num = scanner.next();
      FloatingPoint bFloat = new FloatingPoint(Double.parseDouble(num));
      FloatingPoint sumFloat = aFloat.sum(bFloat);
      System.out.println("Result of float sum:" + '\n' + sumFloat + '\n' + sumFloat.getValue() + "\n---------------");
    }catch (RuntimeException e){{
      System.out.println("Learn how to write numbers, asshole");
    }}

  }
}
