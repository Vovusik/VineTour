package com.andrukhiv.winetour.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.model.WineCardModel;
import com.andrukhiv.winetour.activity.WineCardDetailsActivity;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.ViewPropertyTransition;

import java.util.ArrayList;
import java.util.List;


public class WineCardRecyclerAdapter extends RecyclerView.Adapter {

    private final LayoutInflater mInflater;
    private List<WineCardModel> wineCardModelList;
    private Context mContext;
    private TextView priceSaleCard;
    public WineCardModel wineCardModel;

    private static final int DEFAULT_ITEMS_COUNT = 15;

    public WineCardRecyclerAdapter(Context context, List<WineCardModel> itemList) {
        mInflater = LayoutInflater.from(context);
        wineCardModelList = new ArrayList<>(itemList);
    }

    public static class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
        TextView nameWineCard, priceSaleCard, priceCard;
        ImageView memberPhoto;

        RecyclerViewViewHolder(View itemView) {
            super(itemView);

            nameWineCard = itemView.findViewById(R.id.name_wine_card);
            priceSaleCard = itemView.findViewById(R.id.price_sale_card);
            priceCard = itemView.findViewById(R.id.price_card);
            memberPhoto = itemView.findViewById(R.id.image_bottle_card);
        }
    }

    @NonNull
    @Override
    public RecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.wine_card_item, parent, false);
        return new RecyclerViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final WineCardModel wineCardModel = wineCardModelList.get(position);

        if (holder instanceof RecyclerViewViewHolder) {
            ((RecyclerViewViewHolder) holder).nameWineCard.setText(wineCardModel.getName());
            ((RecyclerViewViewHolder) holder).priceCard.setText(wineCardModel.getPrice());
            ((RecyclerViewViewHolder) holder).priceSaleCard.setText(wineCardModel.getPriceSale());

            Glide
                    .with(holder.itemView.getContext())
                    .load("https://live.staticflickr.com/65535/" + wineCardModel.getImageBottle())
                    .placeholder(R.drawable.placeholder)
                    .thumbnail(0.5f)// зменшив розмір попередного перегляду фото у 2 рази
                    .transition(GenericTransitionOptions.with(animationObject))
                    .into(((RecyclerViewViewHolder) holder).memberPhoto);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(v.getContext(), WineCardDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(WineCardDetailsActivity.EXTRA_ID, wineCardModelList.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

//        if (wineCardModel.getPriceSale() != null) {
//            priceSaleCard.setTextColor(ContextCompat.getColor(mContext, R.color.colorError));
//        }
    }

    @Override
    public int getItemCount() {
        if (wineCardModelList.size() > DEFAULT_ITEMS_COUNT) {
            return DEFAULT_ITEMS_COUNT;
        } else {
            return wineCardModelList.size();
        }
    }

    // Анімація завантаження картинки
    private ViewPropertyTransition.Animator animationObject = view -> {
        view.setAlpha(0f);

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeAnim.setDuration(2500);
        fadeAnim.start();
    };
}
