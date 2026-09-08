package com.mmpantry.app;
import java.util.*;
public class Recipe { public int id; public String name, description, method; public int prepTime; public List<Req> ingredients=new ArrayList<>();
 // Only set for "Almost There" results: the single ingredient this recipe is missing.
 public String missingIngredient = null;
 public Recipe(int id,String name,String description,int prepTime,String method){this.id=id;this.name=name;this.description=description;this.prepTime=prepTime;this.method=method;}
 public static class Req { public String name,unit; public double qty; public Req(String n,double q,String u){name=n;qty=q;unit=u;} }
}