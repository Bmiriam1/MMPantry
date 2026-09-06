package com.mmpantry.app;

import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.Holder> {
    public interface Listener { void edit(Ingredient x); void delete(Ingredient x); }
    private final List<Ingredient> items; private final Listener listener;
    public PantryAdapter(List<Ingredient> items, Listener listener){this.items=items;this.listener=listener;}
    public Holder onCreateViewHolder(ViewGroup p,int type){LinearLayout row=new LinearLayout(p.getContext());row.setOrientation(LinearLayout.HORIZONTAL);row.setPadding(20,16,12,16);row.setBackgroundColor(Ui.card(p.getContext()));TextView label=Ui.text(p.getContext(),"",16);row.addView(label,new LinearLayout.LayoutParams(0,-2,1));Button edit=new Button(p.getContext());edit.setText("EDIT");row.addView(edit);Button del=new Button(p.getContext());del.setText("DELETE");row.addView(del);return new Holder(row,label,edit,del);}
    public void onBindViewHolder(Holder h,int pos){Ingredient x=items.get(pos);h.label.setText(x.name+"\n"+x.quantity+" "+x.unit+(x.expiry.isEmpty()?"":"   • expires "+x.expiry));h.edit.setOnClickListener(v->listener.edit(x));h.del.setOnClickListener(v->listener.delete(x));}
    public int getItemCount(){return items.size();}
    static class Holder extends RecyclerView.ViewHolder{TextView label;Button edit,del;Holder(View v,TextView l,Button e,Button d){super(v);label=l;edit=e;del=d;}}
}
