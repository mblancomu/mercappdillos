package com.mcomobile.mercadillos.view;

import com.mcomobile.mercadillos.world.BeyondarObject;

import java.util.ArrayList;


public interface OnClickBeyondarObjectListener {

	static final String __ON_CLICK_BEYONDAR_OBJECT_METHOD_NAME__ = "onClickBeyondarObject";

	/**
	 * This method is called when the user click on a {@link BeyondarObject}
	 * 
	 * @param beyondarObjects
	 *            All the {@link BeyondarObject} that collide with the ray
	 *            generated by the user click. If no object have been clicked
	 *            the {@link ArrayList} will be empty
	 */
	public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects);

}