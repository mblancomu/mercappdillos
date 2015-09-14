package com.mcomobile.mercadillos;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mcomobile.mercadillos.db.AdminSQLiteOpenHelper;

public class Listado extends ListActivity{
	
	TextView titulo;
	ListView lv;
	CharSequence mercado;
	String iconMarcador;
	Location myLocation,mercadoloc;
	 LocationManager locationManager;
	double distancia;
	String distText,municipios,tipo,fecha;	 
	String consulta, completo;
	
	@Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.listado);
        
        TextView tipoe = (TextView)findViewById(R.id.textView2);
        TextView dia = (TextView)findViewById(R.id.textView3);
        TextView munici = (TextView)findViewById(R.id.textView6);
        
        completo = "Todos";
        Bundle extras = Listado.this.getIntent().getExtras();
     	if(extras!=null){       
        municipios = extras.getString("EXTRA_MUNI");
        fecha = extras.getString("EXTRA_DIA");
        tipo = extras.getString("EXTRA_TIPO");
        
        tipoe.setText(tipo);
        dia.setText(fecha);
        munici.setText(municipios);
     	}
        
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        myLocation = locationManager.getLastKnownLocation(provider);
        
        initBBDD();
}
	
private void initBBDD(){
	
	ArrayList<ItemCompra> results = new ArrayList<ItemCompra>();
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
    SQLiteDatabase db = MDB.getWritableDatabase();
    
   if(municipios.equals(completo)&&fecha.equals(completo)&&tipo.equals(completo)){
      consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos ORDER BY nombre ASC";
    }else if(municipios.equals(completo)&&(tipo!=completo)&&(fecha!=completo)){
    	consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos where tipo='" + tipo +"' AND fecha='" + fecha +"' ORDER BY nombre ASC";
    }else if(tipo.equals(completo)&&(municipios!=completo)&&(fecha!=completo)){
    	consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos where municipio='" + municipios +"' AND fecha='" + fecha +"' ORDER BY nombre ASC";
    }else if((tipo!=completo)&&(municipios!=completo)&&(fecha.equals(completo))){
    	consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos where municipio='" + municipios +"' AND tipo='" + tipo +"' ORDER BY nombre ASC";
    }else{
    	if(tipo.equals(completo)&&(fecha.equals(completo))&&(municipios!=completo)){
    	consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos where municipio='" + municipios +"'ORDER BY nombre ASC";
    }else if(tipo.equals(completo)&&municipios.equals(completo)&&(fecha!=completo)){
    	consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos where fecha='" + fecha +"'ORDER BY nombre ASC";
    }else if((tipo!=completo)&&(municipios.equals(completo))&&fecha.equals(completo)){
    	consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos where tipo='" + tipo +"'ORDER BY nombre ASC";
    }else if((tipo!=completo)&&(municipios!=completo)&&(fecha!=completo)){
    	consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos where tipo='" + tipo +"'AND fecha='" + fecha +"' AND municipio='" + municipios +"'ORDER BY nombre ASC";
    }
}
   //consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos where municipio='" + municipios +"'ORDER BY nombre ASC";
    //consulta = "select id,nombre,fecha,tipo,municipio,latitud,longitud from mercadillos ORDER BY nombre ASC";
    Cursor c = db.rawQuery(consulta, null);
        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    String nombre = c.getString(c.getColumnIndex("nombre"));
                    String id = c.getString(c.getColumnIndex("id"));
                    String latitud = c.getString(c.getColumnIndex("latitud"));
                    String longitud = c.getString(c.getColumnIndex("longitud"));
                    String tipote = c.getString(c.getColumnIndex("tipo"));                        
                    double lat = Double.parseDouble(latitud);
                    double lon = Double.parseDouble(longitud);
                    mercadoloc = new Location("");
                    mercadoloc.setLatitude(lat);
                    mercadoloc.setLongitude(lon);
                    if(myLocation==null){
                    	distText = "sin datos";
                    }else{
                    	distancia = (double) mercadoloc.distanceTo(myLocation);
		        	    distText = String.valueOf(distancia/1000);	
                    }
	
                    if(tipote.equals("Mercadillo de Antig�edades")){
	                		iconMarcador = "drawable/markerant";
	                	}else if (tipote.equals("Mercado Ecol�gico")){
	                		iconMarcador = "drawable/markereco";
	                	}else if (tipote.equals("Mercadillo Generalista")){
	                		iconMarcador = "drawable/markergen";
	                	}else if(tipote.equals("Mercado de Artesan�a")){
	                		iconMarcador = "drawable/markerart";
	                	}else if(tipote.equals("Otro Mercadillo o Feria")){
	                		iconMarcador = "drawable/markerotros";
	                	}else if (tipote.equals("Rastro de Segunda Mano")){
	                		iconMarcador = "drawable/markersecond";
	                	}else if (tipote.equals("Mercado de Navidad")){
	                		iconMarcador = "drawable/markernav";
	                	}else if (tipote.equals("Mercadillo de Coleccionismo")){
	                		iconMarcador = "drawable/markercol";
	                	} else{
	                		iconMarcador = "drawable/marcador";
	                	}
                    results.add(new ItemCompra(id, nombre, distText+" kms", iconMarcador));
                }while (c.moveToNext());
            } 
        }	
        c.close();
        db.close();
        ItemCompraAdapter adaptador = new ItemCompraAdapter(
               Listado.this, results);
         setListAdapter(adaptador);
}

	@Override
	public void onListItemClick(ListView lista, View view, int posicion, long id) {
		// Hacer algo cuando un elemento de la lista es seleccionado
		TextView textoTitulo = (TextView) view.findViewById(R.id.nombre);

		mercado = textoTitulo.getText(); 
		
		Intent i = new Intent (this, InfoMercado.class);
        i.putExtra("Mercado", mercado);
        startActivity(i);
        finish();
	}
	
	public class ItemCompraAdapter extends BaseAdapter {
		protected Activity activity;
		protected ArrayList<ItemCompra> items;
		
		public ItemCompraAdapter(Activity activity, ArrayList<ItemCompra> items) {
			this.activity = activity;
			this.items = items;
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
	        	vi = inflater.inflate(R.layout.list_item_layout, null);
	        }
	        	        
	        ItemCompra item = items.get(position);
	        
	        ImageView image = (ImageView) vi.findViewById(R.id.imagen);
	        int imageResource = activity.getResources().getIdentifier(item.getRutaImagen(), null, activity.getPackageName());
	        image.setImageDrawable(activity.getResources().getDrawable(imageResource));
	        
	        TextView nombre = (TextView) vi.findViewById(R.id.nombre);
	        nombre.setText(item.getNombre());
	        
	        TextView tipo = (TextView) vi.findViewById(R.id.tipo);
	        tipo.setText(item.getTipo());
            
	        Animation animation = null;
	        
	        animation = AnimationUtils.loadAnimation(activity, R.anim.hyperspace_out);
	        
	        vi.startAnimation(animation);
			animation = null;
	        
	        return vi;
		}


		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
public void home(View view) {
	Intent intent = new Intent(Listado.this, MenuGral.class);  
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);  
    startActivity(intent);
    finish();
	}
public void atras(View view) {
	Intent intent = new Intent(Listado.this, MenuGral.class);  
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);  
    startActivity(intent);
    finish();
}
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
    	Intent intent = new Intent(Listado.this, MenuGral.class);  
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);  
        startActivity(intent);
        finish();
        return true;
    }
    return super.onKeyDown(keyCode, event);
}	
}
