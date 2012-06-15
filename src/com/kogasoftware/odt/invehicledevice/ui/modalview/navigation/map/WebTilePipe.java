package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import java.io.File;
import java.io.IOException;

import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.EmptyDataSource;

public class WebTilePipe extends TilePipe<Void, File> {
	private DataSource dataSource = new EmptyDataSource();
	
	public WebTilePipe(PipeQueue<TilePair<Void>> fromPipeQueue, PipeQueue<TilePair<File>> toPipeQueue) {
		super(fromPipeQueue, toPipeQueue);
	}

	@Override
	protected TilePair<File> load(TilePair<Void> from) throws IOException {
		return null;
	}
}
