package by.kovalski.binarynumbers.entity;

public interface BinaryNumber<T extends Number> {
  BinaryNumber<T> sum(BinaryNumber<T> other);

  BinaryNumber<T> dif(BinaryNumber<T> other);

  BinaryNumber<T> product(BinaryNumber<T> other);

  BinaryNumber<T> division(BinaryNumber<T> other);

  T getValue();
}
