package com.emmbi.mobile.datacache.cache;

import android.app.Activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.emmbi.mobile.datacache.json.RequestCallback;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by mbagliojr on 4/14/15.
 */
public abstract class CacheRequestCallback<T> extends RequestCallback<T> {

    private List<String> ignoreMethods = new ArrayList<String>();
    private Activity activity;
    private CacheFetcher<T> cacheFetcher;

    public CacheRequestCallback(CacheFetcher<T> cacheFetcher, Activity activity) {
        super();

        this.activity = activity;
        this.cacheFetcher = cacheFetcher;
        ignoreMethods.add("isSugarEntity");
    }

    public void updateUI(T response) {
        super.updateUI(response);

    }

    public void fetchFromCacheAndUpdateUI() {
        if(cacheFetcher != null) {
            try {
                cacheFetcher.fetchFromCache(this, activity);
            } catch (Exception e) {
            }
        }
    }

    public void onServerFailure(VolleyError error) {
        super.onServerFailure(error);
    }

    @Override
    public Response.Listener<T> getSuccessListener() {
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {

                if(response != null) {
                    if (SugarRecord.isSugarEntity(response.getClass())) {
                        ((SugarRecord) response).save();
                    } else if (Collection.class.isAssignableFrom(response.getClass())) {

                        Collection collection = (Collection) response;

                        for (Object object : collection) {
                            if (SugarRecord.isSugarEntity(object.getClass())) {
                                ((SugarRecord) object).save();
                            }
                        }
                    }
                }
                //CacheCascader.saveCascadeChildren(response, ignoreMethods);

                fetchFromCacheAndUpdateUI();
//        else if(SugarRecordList.class.isAssignableFrom(response.getClass())) {
//            SugarRecordList sugarRecordList = (SugarRecordList) response;
//
//            if(sugarRecordList.getRecords() != null) {
//                for (Object object : sugarRecordList.getRecords()) {
//                    if(SugarRecord.class.isAssignableFrom(object.getClass())) {
//                        ((SugarRecord) object).save();
//                    }
//                }
//            }
//        }
            }
        };
    }

}
