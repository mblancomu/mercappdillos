package com.mcomobile.mercadillos;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemComentarioAdapter extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<ItemComentario> items;	
	
	public ItemComentarioAdapter(Activity activity, ArrayList<ItemComentario> itemsComentario) {
		this.activity = activity;
		this.items = itemsComentario;
	}


	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		
        if(convertView == null) {
        	LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	vi = inflater.inflate(R.layout.list_item_comentario, null);
        }
            
        ItemComentario item = items.get(position);
               
        TextView nombre = (TextView) vi.findViewById(R.id.nombreComentario);
        nombre.setText(item.getNombre());
        
        TextView tipo = (TextView) vi.findViewById(R.id.tipoPuntuacion);
        tipo.setText(item.getTipo());

        return vi;
	}


	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}
