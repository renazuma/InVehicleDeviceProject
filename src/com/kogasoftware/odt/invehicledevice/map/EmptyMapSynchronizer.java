package com.kogasoftware.odt.invehicledevice.map;

public class EmptyMapSynchronizer extends MapSynchronizer {

	@Override
	public Boolean isDirty() {
		return false;
	}

	@Override
	public void read(Accessor accessor) {
	}

	@Override
	public void write(Accessor accessor) {
	}

}
