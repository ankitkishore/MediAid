package com.example.ankit.mediaid;

import com.bumptech.glide.Glide;
import com.example.ankit.mediaid.Models.Meds;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ankit.mediaid.Models.Meds;
import com.example.ankit.mediaid.Models.Quantity;
import com.example.ankit.mediaid.UI.Edit_Meds;
import com.example.ankit.mediaid.UI.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by ankit on 3/13/2018.
*/

public class MyLIstViewAdapter extends ArrayAdapter implements Filterable {

    Context _context;
    ArrayList<Meds> med;
    ArrayList<Meds>filteredData = null;
    int resource;
    private ItemFilter mFilter = new ItemFilter();


    public MyLIstViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        this._context = context;
        this.med = objects;
        this.filteredData = objects;
        this.resource = resource;
    }

    public int getCount() {
        return filteredData.size();
    }

    public Meds getItem(int i) {
        return filteredData.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int i, View v, @NonNull ViewGroup viewGroup) {
        final Meds meds = filteredData.get(i);
        View view;
        LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = infalInflater.inflate( this.resource,null,true);
        TextView name = view.findViewById(R.id.name);
        final TextView price = view.findViewById(R.id.price);
        final TextView quantity = view.findViewById(R.id.quantity);
        final TextView cart_quantity = view.findViewById(R.id.cart_quantity);
        Button add =view.findViewById(R.id.add);
        Button sub = view.findViewById(R.id.sub);

        ImageView image = view.findViewById(R.id.list_image);
        Glide.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/mediaid-be62a.appspot.com/o/Image%2Fmedia_xll_7501226.png?alt=media&token=e50781ca-c5d1-4c16-89d3-a70228d1b378").into(image);
        name.setText(meds.getName());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String user_id = auth.getCurrentUser().getUid();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("inventory").child(meds.getMed_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("yoankit"," "+dataSnapshot);
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("yoankit"," "+dataSnapshot);
                    Quantity q = ds.getValue(Quantity.class);
                    if (q != null) {
                        quantity.setText(q.getQuantity());
                        price.setText(q.getPrice());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        db.child("cart").child(user_id).child(meds.getMed_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Quantity a = dataSnapshot.getValue(Quantity.class);
                    if(a != null)
                    {
                        if(Integer.parseInt(a.getQuantity()) == 0)
                            dataSnapshot.getRef().removeValue();
                        cart_quantity.setText(a.getQuantity());
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer a = Integer.parseInt(cart_quantity.getText().toString());
                a++;
                cart_quantity.setText(a.toString());
                Quantity qw = new Quantity();
                qw.setQuantity(String.valueOf(a));
                qw.setPrice(price.getText().toString());
                db.child("cart").child(user_id).child(meds.getMed_id()).setValue(qw);
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer a = Integer.parseInt(cart_quantity.getText().toString());
                a--;/*
                cart_quantity.setText(a.toString());*/
                Quantity qw = new Quantity();
                qw.setQuantity(String.valueOf(a));
                qw.setPrice(price.getText().toString());
                db.child("cart").child(user_id).child(meds.getMed_id()).setValue(qw);
            }
        });

        return view;
    }
    public Filter getFilter() {
        return mFilter;
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Meds> list = med;

            int count = list.size();
            final ArrayList<Meds> nlist = new ArrayList<>(count);

            Meds filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Meds>) results.values;
            notifyDataSetChanged();
        }

    }



}
