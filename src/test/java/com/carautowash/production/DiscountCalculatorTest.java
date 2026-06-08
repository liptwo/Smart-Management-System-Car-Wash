package com.carautowash.production;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DiscountCalculatorTest {

  @Test
  public void testCalculateDiscount() {
    DiscountCalculator calculator = new DiscountCalculator();
    
    // Test case 1: totalAmount < 100
    double discount1 = calculator.calculateDiscount(50);
    // assert discount1 == 0 : "Expected discount for amount < 100 to be 0";
    assertEquals(0, discount1, "Expected discount for amount < 100 to be 0");
    
    System.out.println("Test cases 1 passed!");
  }

  @Test
  public void testCalculateDiscount_BoundaryCases() {
    DiscountCalculator calculator = new DiscountCalculator();
    
    // Test case 2: totalAmount = 100
    double discount2 = calculator.calculateDiscount(100);
    assert discount2 == 10 : "Expected discount for amount = 100 to be 10% of totalAmount";
    
    System.out.println("Test cases 2 passed!");
  }

  @Test
  public void testCalculateDiscount_HigherAmount() {
    DiscountCalculator calculator = new DiscountCalculator();
    
    // Test case 3: totalAmount > 500
    double discount3 = calculator.calculateDiscount(600);
    assert discount3 == 120 : "Expected discount for amount > 500 to be 20% of totalAmount";
    
    System.out.println("Test cases 3 passed!");
  }
}
