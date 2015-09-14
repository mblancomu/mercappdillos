package com.mcomobile.mercadillos.opengl.renderer;

import com.mcomobile.mercadillos.world.BeyondarObject;

import java.util.List;

public interface OnBeyondarObjectRenderedListener {

	public void onBeyondarObjectsRendered(List<BeyondarObject> renderedBeyondarObjects);
}
