package com.mmpantry.app;
public class Ingredient { public int id; public String name, unit, expiry; public double quantity;
 public Ingredient(int id,String name,double quantity,String unit,String expiry){this.id=id;this.name=name;this.quantity=quantity;this.unit=unit;this.expiry=expiry;}
}