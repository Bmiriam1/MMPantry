package com.mmpantry.app;

import android.view.*;import android.widget.*;import androidx.recyclerview.widget.RecyclerView;import java.util.*;
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder>{
 public interface Listener{void open(Recipe r);} private final List<Recipe> items;private final Listener listener;
 public RecipeAdapter(List<Recipe> items,Listener listener){this.items=items;this.listener=listener;}
 public Holder onCreateViewHolder(ViewGroup p,int type){Button b=new Button(p.getContext());b.setAllCaps(false);b.setTextSize(16);b.setPadding(20,18,20,18);return new Holder(b);}
 public void onBindViewHolder(Holder h,int pos){Recipe r=items.get(pos);h.button.setText(r.name+"\n"+r.description+"\n"+r.ingredients.size()+"/"+r.ingredients.size()+" pantry ingredients  •  "+r.prepTime+" min");h.button.setOnClickListener(v->listener.open(r));}
 public int getItemCount(){return items.size();}static class Holder extends RecyclerView.ViewHolder{Button button;Holder(Button b){super(b);button=b;}}
}
