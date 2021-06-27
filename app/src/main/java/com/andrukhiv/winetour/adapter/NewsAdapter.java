package com.andrukhiv.winetour.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.prof.rssparser.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final List<Article> articles;
    Article currentArticle;
    private final Context mContext;
    @SuppressLint("StaticFieldLeak")
    static ProgressBar progressBar;

    public NewsAdapter(List<Article> list, Context context) {
        this.articles = list;
        this.mContext = context;
    }

    public List<Article> getArticleList() {
        return articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        currentArticle = articles.get(position);

        String pubDateString = currentArticle.getPubDate();

        try {
            String sourceDateString = currentArticle.getPubDate();

            if (sourceDateString != null) {
                SimpleDateFormat sourceSdf = new SimpleDateFormat("EEE, d MMMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                Date date = sourceSdf.parse(sourceDateString);
                if (date != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
                    pubDateString = sdf.format(date);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

       // Перетворюю строку HTML в Android TextView
        Spanned htmlAsSpanned = Html.fromHtml(currentArticle.getDescription()); // used by TextView
        viewHolder.title.setText(currentArticle.getTitle());
        viewHolder.description.setText(htmlAsSpanned);

        try {
            // Загрузите изображение асинхронно с удаленного URL, настройте общий доступ после завершения
            Glide.with(mContext)
                    .load(currentArticle.getImage())
                    .placeholder(R.drawable.placeholder)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                           // progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(viewHolder.image);

        } catch (Exception ignored) {

        }

        viewHolder.pubDate.setText(pubDateString);

        viewHolder.itemView.setOnClickListener(view -> openCustomTab(position));
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView pubDate;
        ImageView image;
       TextView description;

        public ViewHolder(View itemView) {

            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.descripion);
            pubDate = itemView.findViewById(R.id.date);
        }
    }

    private void openCustomTab(int position) {
        String url = articles.get(position).getLink();

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        // установить цвета панели инструментов
        builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        builder.setSecondaryToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        // поширити додаток
        builder.addDefaultShareMenuItem();
        // показувати заголовок
        builder.setShowTitle(true);
        // змінюю хрестик на стрілочку повернення додому
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.toolbar_ic_bottom_arrow));

        // настроить анимацию начала и выхода
        //builder.setStartAnimations(mContext, R.anim.slide_in_bottom, R.anim.slide_out_top);
        //builder.setExitAnimations(mContext, R.anim.slide_in_top, R.anim.slide_out_bottom);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(mContext, Uri.parse(url));
    }
}