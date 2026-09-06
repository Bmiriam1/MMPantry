package com.mmpantry.app;

import android.app.*;import android.os.*;import android.content.*;import android.view.*;import android.widget.*;import androidx.recyclerview.widget.*;import java.util.*;
public class RecipesActivity extends Activity{
 DatabaseHelper db;RecyclerView list;TextView empty;
 public void onCreate(Bundle b){super.onCreate(b);db=new DatabaseHelper(this);render();}
 protected void onResume(){super.onResume();if(db!=null)load();}
 void render(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(28,28,28,18);root.setBackgroundColor(Ui.background(this));TextView h=Ui.text(this,"Suggested Recipes",30);root.addView(h);TextView sub=Ui.text(this,"Made with what you have. Strict matches only.",16);sub.setTextColor(Ui.secondary(this));root.addView(sub);empty=Ui.text(this,"No recipes match your pantry yet — add more ingredients.",17);empty.setTextColor(Ui.secondary(this));root.addView(empty);list=new RecyclerView(this);list.setLayoutManager(new LinearLayoutManager(this));root.addView(list,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);load();}
 void load(){List<Recipe> data=db.getMatchingRecipes();empty.setVisibility(data.isEmpty()?View.VISIBLE:View.GONE);list.setAdapter(new RecipeAdapter(data,r->{Intent i=new Intent(this,RecipeDetailActivity.class);i.putExtra("id",r.id);startActivity(i);}));}
}
