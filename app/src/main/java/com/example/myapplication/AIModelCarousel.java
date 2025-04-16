package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

// Custom carousel view for AI models
public class AIModelCarousel extends LinearLayout {
    private ViewPager viewPager;
    private List<AIModel> models = new ArrayList<>();
    private AIModelAdapter adapter;
    private OnModelSelectedListener listener;

    public interface OnModelSelectedListener {
        void onModelSelected(AIModel model);
    }

    public AIModelCarousel(Context context) {
        super(context);
        init(context);
    }

    public AIModelCarousel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AIModelCarousel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_ai_model_carousel, this, true);
        viewPager = findViewById(R.id.viewPager);
        adapter = new AIModelAdapter();
        viewPager.setAdapter(adapter);
        // Add page margin and transformation for carousel effect
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.carousel_page_margin));
        viewPager.setOffscreenPageLimit(3);
    }

    public void setModels(List<AIModel> models) {
        this.models.clear();
        this.models.addAll(models);
        adapter.notifyDataSetChanged();
    }

    public void setOnModelSelectedListener(OnModelSelectedListener listener) {
        this.listener = listener;
    }

    private class AIModelAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return models.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_ai_model, container, false);

            ImageView ivModel = view.findViewById(R.id.ivModel);
            TextView tvModelName = view.findViewById(R.id.tvModelName);
            TextView tvModelDesc = view.findViewById(R.id.tvModelDesc);

            final AIModel model = models.get(position);
            ivModel.setImageResource(model.getImageResourceId());
            tvModelName.setText(model.getName());
            tvModelDesc.setText(model.getDescription());

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onModelSelected(model);
                    }
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
