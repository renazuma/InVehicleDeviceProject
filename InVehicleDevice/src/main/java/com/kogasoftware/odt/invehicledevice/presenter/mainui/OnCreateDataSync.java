package com.kogasoftware.odt.invehicledevice.presenter.mainui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.GetOperationSchedulesTask;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.GetServiceProviderTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * アプリ起動時の単発データ同期
 */

public class OnCreateDataSync {

    Context context;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    SQLiteDatabase database;

    public OnCreateDataSync(Context context) {
        this.context = context;
        this.database = new DatabaseHelper(context).getWritableDatabase();
    }

    public void execute() {
        executorService.execute(new GetServiceProviderTask(context, database, executorService));
        executorService.execute(new GetOperationSchedulesTask(context, database, executorService));
    }
}
