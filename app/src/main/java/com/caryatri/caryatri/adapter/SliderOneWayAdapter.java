package com.caryatri.caryatri.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.caryatri.caryatri.Database.OneWay.CabOneWay;
import com.caryatri.caryatri.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderOneWayAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<CabOneWay> cabs;
    TextView txt_brand, model, txt_sitting;
    ImageView img_car;
    List<String> img;


    public SliderOneWayAdapter(Context context, List<CabOneWay> cabs) {
        this.context = context;
        this.cabs = cabs;
    }

    @Override
    public int getCount() {
        return cabs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_book_cab, container, false);

        txt_brand = itemView.findViewById(R.id.txt_brand);
        model = itemView.findViewById(R.id.model);
        txt_sitting = itemView.findViewById(R.id.txt_sitting);
        img_car = itemView.findViewById(R.id.img_car);
        txt_brand.setText(cabs.get(position).cabBrand);
        model.setText(" | " + cabs.get(position).cabModel);
        txt_sitting.setText("Seating: " + cabs.get(position).cabSitting);
        img = new Gson().fromJson(cabs.get(position).cabImage, new TypeToken<List<String>>() {
        }.getType());
        Picasso.get().load(img.get(0)).into(img_car);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
