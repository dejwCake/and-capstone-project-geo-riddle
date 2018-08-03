package sk.dejw.android.georiddles.widget.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

import sk.dejw.android.bakingrecipes.GridRemoteViewsFactory;

public class RiddleWidgetService extends RemoteViewsService {

    public static final String TAG = RiddleWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}
