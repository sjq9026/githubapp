package com.sjq.githubapp.presenters;
import android.util.Log;


import com.sjq.githubapp.base.BasePresenter;
import com.sjq.githubapp.javabean.OwnerEntity;
import com.sjq.githubapp.javabean.PopularEntity;
import com.sjq.githubapp.javabean.PopularItemEntity;
import com.sjq.githubapp.javabean.PopularResponse;

import com.sjq.githubapp.javabean.PopularStateEntity;
import com.sjq.githubapp.models.LanguageContentModelImpl;
import com.sjq.githubapp.views.LanguageContentView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class LanguageContentPresenter implements BasePresenter {


    private LanguageContentModelImpl model;
    private LanguageContentView mView;
    public LanguageContentPresenter(LanguageContentView view) {
        model = new LanguageContentModelImpl();
        mView = view;
    }




    public  void  getPopularItemList(String languageName){
//        model.getPopularList(languageName,"start")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//
//                .subscribe(new Consumer<PopularResponse>() {
//                    @Override
//                    public void accept(PopularResponse listResponse)  {
//                        Log.i("AAAAAA","getPopularItemList()");
//
//
//                        view.updateRecycleViewData(listResponse.getItemEntities());
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.i("AAAAAA","getPopularItemList()--->"+throwable.getMessage());
//
//                    }
//                });

        model.getPopularList(languageName,"start")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PopularResponse>() {
                    @Override
                    public void accept(PopularResponse listResponse)  {
                        Log.i("AAAAAA","getPopularItemList()");
                      ArrayList<PopularEntity> list = model.getFavoritePopular();
                        for (PopularItemEntity itemEntity : listResponse.getItemEntities()) {
                            for (PopularEntity favoriteEntity : list) {
                                if(itemEntity.getId() == favoriteEntity.getPopularId()){
                                    itemEntity.setFavorite(true);
                                }
                            }
                        }
                        mView.updateRecycleViewData(listResponse.getItemEntities());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("AAAAAA","getPopularItemList()--->"+throwable.getMessage());
                               if(mView!=null){
                                   mView.refreshError();
                               }

                    }});
    }


    public  void getFavoritePopularItemList(){

        ArrayList<PopularEntity> list = model.getFavoritePopular();
        ArrayList<PopularItemEntity> result_list = new ArrayList<>();
        for (PopularEntity favoriteEntity : list) {
            PopularItemEntity itemEntity = new PopularItemEntity();
            itemEntity.setId(favoriteEntity.getPopularId());
            OwnerEntity ownerEntity = new OwnerEntity();
            ownerEntity.setAvatar_url(itemEntity.getArchive_url());
            itemEntity.setOwner(ownerEntity);
            itemEntity.setDescription(favoriteEntity.getDescription());
            itemEntity.setStargazers_count(favoriteEntity.getStargazers_count());
            itemEntity.setFull_name(favoriteEntity.getFull_name());
            itemEntity.setHtml_url(favoriteEntity.getHtml_url());
            itemEntity.setFavorite(true);
            result_list.add(itemEntity);

        }
        mView.updateRecycleViewData(result_list);
    }





    public void onFavoriteClick(int position, PopularItemEntity popularItemEntity) {
        PopularEntity favoriteEntity = new PopularEntity();
        favoriteEntity.setAvatar_url(popularItemEntity.getOwner().getAvatar_url());
        favoriteEntity.setDescription(popularItemEntity.getDescription());
        favoriteEntity.setPopularId(popularItemEntity.getId());
        favoriteEntity.setStargazers_count(popularItemEntity.getStargazers_count());
        favoriteEntity.setFull_name(popularItemEntity.getFull_name());
        favoriteEntity.setHtml_url(popularItemEntity.getHtml_url());
        //因为涉及到多页面刷新，每个页面的操作和position位置不一样，所以用eventbus
        PopularStateEntity popularStateEntity = new PopularStateEntity();
        if(!popularItemEntity.isFavorite()){
            model.addFavoritePopularData(favoriteEntity);
            //mView.onItemFavoriteStatusChange(position,true,popularItemEntity);
            popularStateEntity.setFavorite(true);
        }else{
            model.removeFavoritePopularData(favoriteEntity);
            popularStateEntity.setFavorite(false);
            //mView.onItemFavoriteStatusChange(position,false,popularItemEntity);
        }
        popularStateEntity.setPosition(position);
        popularStateEntity.setPopular_id(popularItemEntity.getId());
        EventBus.getDefault().post(popularStateEntity);
    }
    @Override
    public void onDestroy() {
        mView = null;
    }

}
