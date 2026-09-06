package com.mmpantry.app;

import android.app.*;import android.os.*;import android.content.*;import android.view.*;import android.widget.*;import androidx.recyclerview.widget.*;import java.util.*;
public class PantryActivity extends Activity{
 DatabaseHelper db; RecyclerView list; TextView empty;
 public void onCreate(Bundle b){super.onCreate(b);db=new DatabaseHelper(this);render();}
 protected void onResume(){super.onResume();if(db!=null)load();}
 void render(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(28,28,28,18);root.setBackgroundColor(Ui.background(this));
  TextView title=Ui.text(this,"Your Pantry",30);root.addView(title);TextView sub=Ui.text(this,"What is already at home?",16);sub.setTextColor(Ui.secondary(this));root.addView(sub);
  Button add=new Button(this);add.setText("+  ADD INGREDIENT");add.setTextColor(android.graphics.Color.WHITE);add.setBackgroundColor(Ui.GOLD);add.setOnClickListener(v->startActivity(new Intent(this,AddEditIngredientActivity.class)));root.addView(add);
  empty=Ui.text(this,"Your pantry is ready for its first ingredient.",17);empty.setTextColor(Ui.secondary(this));root.addView(empty);
  list=new RecyclerView(this);list.setLayoutManager(new LinearLayoutManager(this));root.addView(list,new LinearLayout.LayoutParams(-1,0,1));root.addView(NavBar.build(this,"pantry"));setContentView(root);load();}
 void load(){List<Ingredient> data=db.getIngredients();if(empty!=null)empty.setVisibility(data.isEmpty()?View.VISIBLE:View.GONE);if(list!=null)list.setAdapter(new PantryAdapter(data,new PantryAdapter.Listener(){public void edit(Ingredient x){Intent i=new Intent(PantryActivity.this,AddEditIngredientActivity.class);i.putExtra("id",x.id);startActivity(i);}public void delete(Ingredient x){new AlertDialog.Builder(PantryActivity.this).setTitle("Remove ingredient?").setMessage(x.name+" will be removed from your pantry.").setNegativeButton("CANCEL",null).setPositiveButton("REMOVE",(d,w)->{db.deleteIngredient(x.id);load();}).show();}}));}
}