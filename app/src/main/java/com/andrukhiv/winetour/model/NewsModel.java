/*
 *   Copyright 2016 Marco Gomiero
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.andrukhiv.winetour.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prof.rssparser.Channel;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import java.util.ArrayList;

import static com.andrukhiv.winetour.constant.Constants.urlString;


public class NewsModel extends ViewModel {

    private MutableLiveData<Channel> articleListLive = null;

    private final MutableLiveData<String> snackbar = new MutableLiveData<>();

    public MutableLiveData<Channel> getChannel() {
        if (articleListLive == null) {
            articleListLive = new MutableLiveData<>();
        }
        return articleListLive;
    }

    private void setChannel(Channel channel) {
        this.articleListLive.postValue(channel);
    }

    public LiveData<String> getSnackbar() {
        return snackbar;
    }

    public void onSnackbarShowed() {
        snackbar.setValue(null);
    }

    public void fetchFeed() {

        Parser parser = new Parser.Builder()
                // Если вы хотите предоставить настраиваемую кодировку (по умолчанию utf-8):
                // .charset(Charset.forName("ISO-8859-7"))
                // .cacheExpirationMillis() and .context() не вызывается, потому что на стороне Java кеширование НЕ поддерживается
                .build();

        parser.onFinish(new OnTaskCompleted() {

            //что делать, когда разбор закончен
            @Override
            public void onTaskCompleted(@NonNull Channel channel) {
                setChannel(channel);
            }

            //что делать в случае ошибки
            @Override
            public void onError(@NonNull Exception e) {
                setChannel(new Channel(null, null, null, null, null, null, new ArrayList<>()));
                e.printStackTrace();
                snackbar.postValue("Виникла помилка. Будь-ласка, спробуйте ще раз");
            }
        });
        parser.execute(urlString);
    }
}
