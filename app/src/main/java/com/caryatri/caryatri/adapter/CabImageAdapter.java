package com.caryatri.caryatri.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.R;
import com.caryatri.caryatri.SelectCabActivity;
import com.caryatri.caryatri.ShowFullImage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CabImageAdapter extends PagerAdapter {

    Context context;
    List<String> imageList;
    LayoutInflater layoutInflater;
    ImageView cab_image;

    public CabImageAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.cab_image_viewer, container, false);

        cab_image = itemView.findViewById(R.id.cab_image);
        Picasso.get().load(imageList.get(position)).into(cab_image);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageList != null) {
                    Common.cabImg = imageList;
                    context.startActivity(new Intent(context, ShowFullImage.class));
                }
            }
        });
        container.addView(itemView);
        return itemView;
    }
}
