package com.mcomobile.mercadillos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mcomobile.mercadillos.db.AdminSQLiteOpenHelper;
import com.mcomobile.mercadillos.fragment.BeyondarFragmentSupport;
import com.mcomobile.mercadillos.radar.GenRadarPoint;
import com.mcomobile.mercadillos.screenshot.OnScreenshotListener;
import com.mcomobile.mercadillos.view.BeyondarGLSurfaceView;
import com.mcomobile.mercadillos.view.OnClickBeyondarObjectListener;
import com.mcomobile.mercadillos.view.OnTouchBeyondarViewListener;
import com.mcomobile.mercadillos.world.BeyondarObject;
import com.mcomobile.mercadillos.world.GeoObject;
import com.mcomobile.mercadillos.world.World;

public class RealidadAumentada extends FragmentActivity implements OnTouchBeyondarViewListener,
		OnClickBeyondarObjectListener,LocationListener,OnScreenshotListener, OnSeekBarChangeListener{
	BeyondarFragmentSupport mBeyondarFragment;
	World sharedWorld;
	String marcador = "";
	ImageView iconoObjeto;
	TableRow tableRow;
	Integer icono,iconoInfo,iconoptos,iconomuni;
	Double lat,log,latmuni,logmuni,latptos,logptos,latfotos,logfotos;
	SeekBar mSeekBarRadio,mSeekBarRadioRadar;
	public String info,nombre,tipo,radar_lat,radar_lng,info_fecha,info_tipo,info_municipio,info_horario,info_transporte;
	private SharedPreferences preferenciasSettings;
	private SharedPreferences.Editor preferenciasEditor;
	public GeoObject goFotos,goResta,goResta2,goResta3,goHidri,goNatu,goCultu,goMuni,goFolk,goPtos,goHotel;
	BeyondarObject geoObjectIcon;
	int pulsado = 0;
	float ejeX,ejeY,ejeZ;
	double distgeo,latgeo,longgeo,distgeoRadar1,distgeoRadar2;
	String iconoObject,latitud,longitud;
	public LocationManager locMan; 
	public Location myLocation,objectLocation,objectRadar1,objectRadar2;
	LinearLayout llradar,lradiovision,llstatusGPS;
	private ArrayList<GeoObject> geoAntiguedades = new ArrayList<GeoObject>();
	private ArrayList<GeoObject> geoColeccionismo = new ArrayList<GeoObject>();
	private ArrayList<GeoObject> geoGeneralista = new ArrayList<GeoObject>();
	private ArrayList<GeoObject> geoArtesania = new ArrayList<GeoObject>();
	private ArrayList<GeoObject> geoNavidad = new ArrayList<GeoObject>();
	private ArrayList<GeoObject> geoEco = new ArrayList<GeoObject>();
	private ArrayList<GeoObject> geoOtros = new ArrayList<GeoObject>();
	private ArrayList<GeoObject> geoSegunda = new ArrayList<GeoObject>();
	String info_infomuni,info_webmuni,info_nombremuni,info_nombreptos,info_nombrefotos,info_infoempresas,info_nombre,info_webempresa,info_localidad;
	String radar_nombremuni,radar_latmuni,radar_lngmuni,radar_nombre,radar_latempresa,radar_lngempresa;
	Double radarLatMuni,radarLngMuni,radarLatEmpresa,radarLngEmpresa;
	TextView infoDistancia,tvstatus;
	public Boolean estado=false;
	int pitchValor;
	public int mtsLimite;
	int mtsRadar1,mtsRadar2,mts;
	List<GenRadarPoint> genRadarPoints;
	CheckBox radar;
	int distLim;
	Double latGeopoint,lngGeopoint,radarLat,radarLng;
	Integer iconoGPS;
	ImageView statusGPS;
	String mercado,mapa;
	ProgressDialog dialog;
	public CheckBox checkanti,checkcolecc,checkgene,checkarte,checknavi,checkeco,checkotros,checksegunda;
	boolean guardarRadioVision,guardarradar,guardarantig,guardarcolecc,guardargene,guardararte,guardarnavi,guardareco,guardarotros,guardarsegunda,guardarmapa,guardarterreno,guardarsatelite,guardaralerta;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		comprobarSensores();
		loadViewFromXML();				
		worldRecursos();		
		mBeyondarFragment.setWorld(sharedWorld);
		mBeyondarFragment.showFPS(false);		
		// set listener for the geoObjects
		mBeyondarFragment.setOnTouchBeyondarViewListener(this);
		mBeyondarFragment.setOnClickBeyondarObjectListener(this);
		mBeyondarFragment.setMinFarDistanceSize(40);
		mBeyondarFragment.setMaxFarDistance(5);
		preferenciasSettings = getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);
		mSeekBarRadio.setMax(50);	
		Bundle extras = RealidadAumentada.this.getIntent().getExtras();
	 	if(extras!=null){	    
	    mercado = extras.getString("Mercado");
	    mapa = extras.getString("Mapa");
	 	}
	}

	@Override
	public void onTouchBeyondarView(MotionEvent event, BeyondarGLSurfaceView beyondarView) {

		float x = event.getX();
		float y = event.getY();

		ArrayList<BeyondarObject> geoObjects = new ArrayList<BeyondarObject>();

		// This method call is better to don't do it in the UI thread!
		beyondarView.getBeyondarObjectsOnScreenCoordinates(x, y, geoObjects);

		Iterator<BeyondarObject> iterator = geoObjects.iterator();
		while (iterator.hasNext()) {
			BeyondarObject geoObject = iterator.next();
			marcador = geoObject.getName();
			latgeo = ((GeoObject) geoObject).getLatitude();
			longgeo = ((GeoObject) geoObject).getLongitude();
			objectLocation = new Location("");
			objectLocation.setLatitude(latgeo);
			objectLocation.setLongitude(longgeo);
			distgeo = myLocation.distanceTo(objectLocation);
		}
	}

	private void loadViewFromXML() {
		setContentView(R.layout.realidad_aumentada);		
		mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
				R.id.beyondarFragment);		
		lradiovision =  (LinearLayout) findViewById(R.id.layout_dialog);
		lradiovision.setVisibility(View.GONE);
		statusGPS =  (ImageView) findViewById(R.id.ivGPS);
		llstatusGPS =  (LinearLayout) findViewById(R.id.llGPS);
		llstatusGPS.setVisibility(View.VISIBLE);
		mSeekBarRadio = (SeekBar) findViewById(R.id.seekBarRadio);
		mSeekBarRadio.setOnSeekBarChangeListener(this);
		infoDistancia = (TextView)findViewById(R.id.tvDistancia);
		tvstatus = (TextView)findViewById(R.id.tvstatus);
		myLocation = new Location("");
		myLocation.setLatitude(40.5512861);
		myLocation.setLongitude(-3.7282889);
		latGeopoint = 40.5512861;
		lngGeopoint = -3.7282889; 
		updatePlaces();	
	}
	public void updatePlaces(){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
		//get location manager                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
			locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);                                                                                                                                                                                                                                                                                                                                                                                                                                             
			//get last location                                          
			Criteria criteria = new Criteria();
		    String provider = locMan.getBestProvider(criteria, false);
			Location lastLoc = locMan.getLastKnownLocation(provider);
			
			if ( !locMan.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
				statusGPS.setImageResource(R.drawable.gps_sindatos);
	        	tvstatus.setTextColor(Color.GRAY);
				alertagps();
		    }else{
		    	locMan.requestLocationUpdates(provider, 30000, 50, this);
		    }
			if(lastLoc != null)
			{
			double lat = lastLoc.getLatitude();                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
			double lng = lastLoc.getLongitude(); 
			latGeopoint = lat;
			lngGeopoint = lng;
			//create LatLng                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
			myLocation.setLatitude(lat);
			myLocation.setLongitude(lng);				
			String calidadGPS = String.valueOf(lastLoc.getAccuracy());
			tvstatus.setText(calidadGPS+" mts");
			if (lastLoc.getAccuracy() <= 10.0 ){
				statusGPS.setImageResource(R.drawable.gps_verde);
				tvstatus.setTextColor(Color.GREEN);
	        }
	        if ((lastLoc.getAccuracy() > 10.0) && (lastLoc.getAccuracy() < 50.0)){
	        	statusGPS.setImageResource(R.drawable.gps_amarillo);
	        	tvstatus.setTextColor(Color.YELLOW);
	        }
	        if ((lastLoc.getAccuracy() >50)){
	        	statusGPS.setImageResource(R.drawable.gps_rojo);
	        	tvstatus.setTextColor(Color.RED);
	        }	      
			}else{				
				statusGPS.setImageResource(R.drawable.gps_sindatos);
	        	tvstatus.setTextColor(Color.GRAY);
				latGeopoint = 40.5512861;
				lngGeopoint = -3.7282889; 
			}
						                                                                                                                                                                                                                                                                                                                                                                                                                                   
	}
	public void alertagps(){
		AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
	    dialogo1.setTitle("GPS Desactivado");  
	    dialogo1.setMessage("�Desea activar el GPS para obtener su posicion?\n Recuerde esperar unos instantes una vez activado para recibir se�al");            
	    dialogo1.setCancelable(false);  
	    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {  
	        @Override
			public void onClick(DialogInterface dialogo1, int id) {  
	            aceptar();  
	        }  
	    });  
	    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {  
	        @Override
			public void onClick(DialogInterface dialogo1, int id) {  
	            dialogo1.cancel();
	        }  
	    });            
	    dialogo1.show();        
	}

	public void aceptar() {		
		Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	    startActivity(settingsIntent);
	}
	@Override
	public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {		
		if (beyondarObjects.size() > 0) {
			
            infoGeo();
            beyondarObjects.clear();
			}			
	}
			
	public void worldRecursos(){
		sharedWorld = new World(this);
		sharedWorld.setArViewDistance(5000);
		// The user can set the default bitmap. This is useful if you are
		// loading images form Internet and the connection get lost
		sharedWorld.setDefaultBitmap(R.drawable.marcador);

		// User position (you can change it using the GPS listeners form Android
		// API)
		sharedWorld.setGeoPosition(latGeopoint,lngGeopoint);

		AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
  	    SQLiteDatabase db = MDB.getWritableDatabase();
		Cursor c = db.rawQuery(
	            "select nombre,tipo,latitud,longitud from mercadillos", null);

		if (c!= null &&c.moveToFirst())
    		do {
	                    nombre = c.getString(c.getColumnIndex("nombre"));
	                    tipo = c.getString(c.getColumnIndex("tipo"));
	                    latitud = c.getString(c.getColumnIndex("latitud")); 
	                    longitud = c.getString(c.getColumnIndex("longitud"));
	                    
	                    if (latitud == null || longitud == null){
	                    	Toast toast = Toast.makeText(RealidadAumentada.this, "Existen registros sin coordenadas", Toast.LENGTH_SHORT);
	                        toast.show();
	                    }
	                    lat = Double.parseDouble(latitud);
	    				log = Double.parseDouble(longitud);

	    				if(tipo.equals("Mercadillo de Antig�edades")){
	        	 			icono = R.drawable.markerant;
	        	 			iconoInfo = R.drawable.iconomarkerant;
	        	 			goHotel = new GeoObject(2l);
	        	 			goHotel.setGeoPosition(lat,log);
	        	 			goHotel.setImageResource(icono);
	        	 			goHotel.setName(nombre);
	        	 			goHotel.setVisible(true);
	        	 			geoAntiguedades.add(goHotel);
	        	 			sharedWorld.addBeyondarObject(goHotel);
	        	 		}else if (tipo.equals("Mercado Ecol�gico")){
	        	 			icono = R.drawable.markereco;
	        	 			iconoInfo = R.drawable.iconomarkereco;
	        	 			goFolk = new GeoObject(3l);
	        	 			goFolk.setGeoPosition(lat,log);
	        	 			goFolk.setImageResource(icono);
	        	 			goFolk.setName(nombre);
	        	 			goFolk.setVisible(true);
	        	 			geoEco.add(goFolk);
	        	 			sharedWorld.addBeyondarObject(goFolk);
	        	 		}else if (tipo.equals("Mercadillo Generalista")){
	        	 			icono = R.drawable.markergen;
	        	 			iconoInfo = R.drawable.iconomarkergen;
	        	 			goHidri = new GeoObject(4l);
	        	 			goHidri.setGeoPosition(lat,log);
	        	 			goHidri.setImageResource(icono);
	        	 			goHidri.setName(nombre);
	        	 			goHidri.setVisible(true);
	        	 			geoGeneralista.add(goHidri);
	        	 			sharedWorld.addBeyondarObject(goHidri);
	        	 		}else if(tipo.equals("Mercado de Artesan�a")){
	        	 			icono =R.drawable.markerart;
	        	 			iconoInfo = R.drawable.iconomarkerart;
	        	 			goCultu = new GeoObject(5l);
	        	 			goCultu.setGeoPosition(lat,log);
	        	 			goCultu.setImageResource(icono);
	        	 			goCultu.setName(nombre);
	        	 			goCultu.setVisible(true);
	        	 			geoArtesania.add(goCultu);
	        	 			sharedWorld.addBeyondarObject(goCultu);
	        	 		}else if(tipo.equals("Otro Mercadillo o Feria")){
	        	 			icono = R.drawable.markerotros;
	        	 			iconoInfo = R.drawable.iconomarkerotros;
	        	 			goNatu = new GeoObject(6l);
	        	 			goNatu.setGeoPosition(lat,log);
	        	 			goNatu.setImageResource(icono);
	        	 			goNatu.setName(nombre);
	        	 			goNatu.setVisible(true);
	        	 			geoOtros.add(goNatu);
	        	 			sharedWorld.addBeyondarObject(goNatu);
	        	 		}else if (tipo.equals("Rastro de Segunda Mano")){
	        	 			icono = R.drawable.markersecond;
	        	 			iconoInfo = R.drawable.iconomarkersecond;
	        	 			goResta = new GeoObject(7l);
	        	 			goResta.setGeoPosition(lat,log);
	        	 			goResta.setImageResource(icono);
	        	 			goResta.setName(nombre);
	        	 			goResta.setVisible(true);
	        	 			geoSegunda.add(goResta);
	        	 			sharedWorld.addBeyondarObject(goResta);	
	        	 		}else if (tipo.equals("Mercado de Navidad")){
	        	 			icono = R.drawable.markernav;
	        	 			iconoInfo = R.drawable.iconomarkernav;
	        	 			goResta2 = new GeoObject(7l);
	        	 			goResta2.setGeoPosition(lat,log);
	        	 			goResta2.setImageResource(icono);
	        	 			goResta2.setName(nombre);
	        	 			goResta2.setVisible(true);
	        	 			geoNavidad.add(goResta2);
	        	 			sharedWorld.addBeyondarObject(goResta2);	
	        	 		}else if (tipo.equals("Mercadillo de Coleccionismo")){
	        	 			icono = R.drawable.markercol;
	        	 			iconoInfo = R.drawable.iconomarkercol;
	        	 			goResta3 = new GeoObject(7l);
	        	 			goResta3.setGeoPosition(lat,log);
	        	 			goResta3.setImageResource(icono);
	        	 			goResta3.setName(nombre);
	        	 			goResta3.setVisible(true);
	        	 			geoColeccionismo.add(goResta3);
	        	 			sharedWorld.addBeyondarObject(goResta3);	
	        	 		}else{
	        	 			
	        	 		}
 				
    		} while (c.moveToNext());
	        c.close();	         	
	}
public void settings(View view) {
		Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");		
		final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
		final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View Viewlayout = inflater.inflate(R.layout.popup_settings_realidad,
                (ViewGroup) findViewById(R.id.settings_realidad)); 
		popDialog.setView(Viewlayout);
		guardarRadioVision = preferenciasSettings.getBoolean("guardarRadioVision", guardarRadioVision);
		final CheckBox radioVision=(CheckBox) Viewlayout.findViewById(R.id.ckRadVision);
		radioVision.setChecked(guardarRadioVision);		
		popDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
						radioVision.setTypeface(font);
				          if (radioVision.isChecked()==true){
				        	  guardarRadioVision = true;
				        	  lradiovision.setVisibility(View.VISIBLE);
				            }else{
				            	guardarRadioVision = false;
				            	lradiovision.setVisibility(View.INVISIBLE);
				            } 
				          preferenciasSettings = getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);	  
					         preferenciasEditor = preferenciasSettings.edit();
					      	
					     	 preferenciasEditor.putBoolean("guardarRadioVision", guardarRadioVision);

					     	 preferenciasEditor.commit();
						dialog.dismiss();
					}

				});			
		popDialog.create();
		popDialog.show();

	}
	public void servicios(View view) {
	Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
	final Dialog ventana=new Dialog(this);
	ventana.setContentView(R.layout.popup_servicios1);
	ventana.setTitle("Marque los servicios deseados");
	
	  guardarantig = preferenciasSettings.getBoolean("guardarantig", guardarantig);
	  guardarcolecc = preferenciasSettings.getBoolean("guardarcolecc", guardarcolecc);
	  guardargene = preferenciasSettings.getBoolean("guardargene", guardargene);
	  guardararte = preferenciasSettings.getBoolean("guardararte", guardararte);
	  guardarnavi = preferenciasSettings.getBoolean("guardarnavi", guardarnavi);
	  guardareco = preferenciasSettings.getBoolean("guardareco", guardareco);
	  guardarotros = preferenciasSettings.getBoolean("guardarotros", guardarotros);
	  guardarsegunda = preferenciasSettings.getBoolean("guardarsegunda", guardarsegunda);
	  	  
	
	Button btnCancelar = (Button) ventana.findViewById(R.id.btnCancelar);
	btnCancelar.setTypeface(font);
	Button btnAceptar = (Button) ventana.findViewById(R.id.btnAceptar);
	btnAceptar.setTypeface(font);
	
	final CheckBox coleccionista=(CheckBox) ventana.findViewById(R.id.checkcolecc);
	final CheckBox antiguedades=(CheckBox) ventana.findViewById(R.id.checkanti);
	final CheckBox ecologico=(CheckBox) ventana.findViewById(R.id.checkeco);
	final CheckBox artesania=(CheckBox) ventana.findViewById(R.id.checkarte);
	final CheckBox navidad=(CheckBox) ventana.findViewById(R.id.checknavi);
	final  CheckBox generalista=(CheckBox) ventana.findViewById(R.id.checkgene);
	final CheckBox otros=(CheckBox) ventana.findViewById(R.id.checkotros);
	final  CheckBox segunda=(CheckBox) ventana.findViewById(R.id.checksegunda);
	
	coleccionista.setChecked(guardarcolecc);
	antiguedades.setChecked(guardarantig);
	ecologico.setChecked(guardareco);
	artesania.setChecked(guardararte);
	navidad.setChecked(guardarnavi);
	generalista.setChecked(guardargene);
	otros.setChecked(guardarotros);
	segunda.setChecked(guardarsegunda);
		
		btnCancelar.setOnClickListener(new View.OnClickListener(){
		    @Override
			public void onClick(View v) {
		           ventana.cancel();//Cerrar ventana
		}});
		btnAceptar.setOnClickListener(new View.OnClickListener(){
			   @Override
			public void onClick(View v) {
			          ventana.cancel();//Cerrar ventana
			          //Capturar el estado de uno de los checkbox de Dialog
			          Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");		          
			          
			          coleccionista.setTypeface(font);
			          if (coleccionista.isChecked()==true){
			        	  guardarcolecc = true;
			        	  for (GeoObject goMuni : geoColeccionismo) {
			            	    goMuni.setVisible(true);
			        	  }
			            }else{
			            	guardarcolecc = false;
			            	for (GeoObject goMuni : geoColeccionismo) {
			            	    goMuni.setVisible(false);
			          }
			            }
			          antiguedades.setTypeface(font);
					  if (antiguedades.isChecked()==true){
					        	  guardarantig = true;
					        	  for (GeoObject goResta : geoAntiguedades) {
					            	    goResta.setVisible(true);
					        	  }
					            }else{
					            	guardarantig = false;
					            	for (GeoObject goResta : geoAntiguedades) {
					            	    goResta.setVisible(false);
					          }
					            }
					       ecologico.setTypeface(font);
							          if (ecologico.isChecked()==true){
							        	  guardareco = true;
							        	  for (GeoObject goHotel : geoEco) {
							            	    goHotel.setVisible(true);
							        	  }
							            }else{
							            	guardareco = false;
							            	for (GeoObject goHotel : geoEco) {
							            	    goHotel.setVisible(false);
							          }
							            }
							            	artesania.setTypeface(font);
									          if (artesania.isChecked()==true){
									        	  guardararte = true;
									        	  for (GeoObject goFolk : geoArtesania) {
									            	    goFolk.setVisible(true);
									        	  }
									            }else{
									            	guardararte = false;
									            	for (GeoObject goFolk : geoArtesania) {
									            		goFolk.setVisible(false);
									          }									            			          
			            
			}
           	navidad.setTypeface(font);
		          if (navidad.isChecked()==true){
		        	  guardarnavi = true;
		        	  for (GeoObject goHidri : geoNavidad) {
		        		  goHidri.setVisible(true);
		        	  }
		            }else{
		            	guardarnavi = false;
		            	for (GeoObject goHidri: geoNavidad) {
		            		goHidri.setVisible(false);
		          }									            			          

}
		          generalista.setTypeface(font);
		          if (generalista.isChecked()==true){
		        	  guardargene = true;
		        	  for (GeoObject goCultu : geoGeneralista) {
		        		  goCultu.setVisible(true);
		        	  }
		            }else{
		            	guardargene = false;
		            	for (GeoObject goCultu: geoGeneralista) {
		            		goCultu.setVisible(false);
		          }									            			          

}
		          otros.setTypeface(font);
		          if (otros.isChecked()==true){
		        	  guardarotros = true;
		        	  for (GeoObject goNatu : geoOtros) {
		        		  goNatu.setVisible(true);
		        	  }
		            }else{
		            	guardarotros = false;
		            	for (GeoObject goNatu: geoOtros) {
		            		goNatu.setVisible(false);
		          }									            			          

}
                segunda.setTypeface(font);
		          if (segunda.isChecked()==true){
		        	  guardarsegunda = true;
		        	  for (GeoObject goNatu : geoSegunda) {
		        		  goNatu.setVisible(true);
		        	  }
		            }else{
		            	guardarsegunda = false;
		            	for (GeoObject goNatu: geoSegunda) {
		            		goNatu.setVisible(false);
		          }									            			          

}
									          preferenciasSettings = getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);	  
										         preferenciasEditor = preferenciasSettings.edit();
										      	
					 preferenciasEditor.putBoolean("guardarantig", guardarantig);
			     	 preferenciasEditor.putBoolean("guardarcolecc", guardarcolecc);
			     	 preferenciasEditor.putBoolean("guardargene", guardargene);
			     	 preferenciasEditor.putBoolean("guardararte", guardararte);
			     	 preferenciasEditor.putBoolean("guardarnavi", guardarnavi);
			     	 preferenciasEditor.putBoolean("guardareco", guardareco);
					 preferenciasEditor.putBoolean("guardarotros", guardarotros);
			     	 preferenciasEditor.putBoolean("guardarsegunda", guardarsegunda);
										     	 preferenciasEditor.commit();						          
   
			   }});
		ventana.show();
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {		    
        	if (seekBar == mSeekBarRadio) {
        		mts = progress;
        		mtsLimite = progress*1000;
    			sharedWorld.setArViewDistance(mtsLimite);
    			mBeyondarFragment.setMinFarDistanceSize(50);
    			mBeyondarFragment.setMaxFarDistance(progress);
    			infoDistancia.setText(progress+" kms");   						
    		}		        	
        }

		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onStopTrackingTouch(SeekBar seekBar) {				
				// TODO Auto-generated method stub
			SharedPreferences prefs = getBaseContext().getSharedPreferences("mySharedPrefsFilename", Context.MODE_PRIVATE);
		    // Don't forget to call commit() when changing preferences.
		    prefs.edit().putInt("seekBarValue", seekBar.getProgress()).commit();
		}	
		
		private String unescape(String description) {
		    return description.replaceAll("\\\\n", "\\\n");
		}
	public void infoMunicipios(){
		if(marcador.equals(null)){
			Toast toast = Toast.makeText(RealidadAumentada.this, "No se dispone de sitio web para este mercado", Toast.LENGTH_SHORT);
            toast.show();
		}else{
			AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		    SQLiteDatabase db = MDB.getWritableDatabase();
		Cursor c = db.rawQuery("select nombre,fecha,tipo,municipio,horario,latitud,longitud,transporte from mercadillos where nombre='" + marcador +"'", null);

		if (c!= null &&c.moveToFirst()){
	                    info_nombre = c.getString(c.getColumnIndex("nombre"));
	                	info_fecha = unescape(c.getString(c.getColumnIndex("fecha")));
	                	info_tipo = c.getString(c.getColumnIndex("tipo"));
	                	info_municipio = c.getString(c.getColumnIndex("municipio"));
	    			    info_horario = unescape(c.getString(c.getColumnIndex("horario")));
	    			    info_transporte = c.getString(c.getColumnIndex("transporte"));	    			    
		}	    			    
	        c.close();
		    db.close();	    			    
		}	    			    
		}
   public void infoGeo(){
	    infoMunicipios();
	    String dist;
	    Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
	    final Dialog ventana=new Dialog(this);
	    ventana.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    ventana.setContentView(R.layout.popup_detalle_realidad);
	    if(distgeo>0.0){
		dist = String.valueOf(distgeo/1000);
		}else{
		dist = "---"; 
		}
		final TextView nombre =(TextView) ventana.findViewById(R.id.tvNombre);
		nombre.setTypeface(font);
		nombre.setText(marcador);
		final TextView distancia =(TextView)ventana.findViewById(R.id.tvDistancia);
		distancia.setTypeface(font);
		distancia.setText(dist+" Kms");
		final TextView municipio =(TextView) ventana.findViewById(R.id.tvMunicipios);
		municipio.setTypeface(font);		
		municipio.setText(info_municipio);		
		final TextView dia =(TextView)ventana.findViewById(R.id.tvDia);
		dia.setTypeface(font);		
		dia.setText(info_fecha);
		final TextView llegar =(TextView) ventana.findViewById(R.id.tvLlegar);
		llegar.setTypeface(font);
		llegar.setText("�C�mo llegar?");
		final TextView horario =(TextView) ventana.findViewById(R.id.tvHorario);
		horario.setTypeface(font);		
		horario.setText(info_horario);
		final TextView transporte =(TextView) ventana.findViewById(R.id.tvTransporte);
		transporte.setTypeface(font);		
		transporte.setText(info_transporte);		
		llegar.setOnClickListener(new View.OnClickListener(){
		    @Override
			public void onClick(View v) {
		    		Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("google.navigation:q="+latgeo+","+longgeo));
	        	startActivity(i);
		}});
		Button btnCancelar = (Button) ventana.findViewById(R.id.btnCancelar);
	     btnCancelar.setTypeface(font);
		btnCancelar.setOnClickListener(new View.OnClickListener(){
	    @Override
		public void onClick(View v) {
	           ventana.cancel();//Cerrar ventana
	}});
		ventana.show();
	}

@Override
public void onLocationChanged(Location arg0) {
	// TODO Auto-generated method stub
	updatePlaces();
}

@Override
public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
}

@Override
public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
	
}

@Override
public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
	
}

public void compartir(View view) {
	Intent intent = new Intent(Intent.ACTION_SEND);
	intent.putExtra(Intent.EXTRA_SUBJECT,
			 "Descargue la APP MercAPPdillos");  
	intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.mco.mercappdillos");
	intent.setType("text/plain");
	startActivity(Intent.createChooser(intent, "Compartir link"));
}
public void home(View view) {
	Intent intent = new Intent(RealidadAumentada.this, MenuGral.class);  
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);  
    startActivity(intent);
    finish();
}
public void atras(View view) {
	if(mercado!=null){
		Intent intent = new Intent(RealidadAumentada.this, Listado.class);  
	    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);	
	    intent.putExtra("Mercado", mercado);
	    startActivity(intent);
	}else if(mapa!=null){
		Intent intent = new Intent(RealidadAumentada.this, Mapa.class);  
	    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);	
	    intent.putExtra("Mercado", mercado);
	    startActivity(intent);
	}else{
		Intent intent = new Intent(RealidadAumentada.this, MenuGral.class);  
	    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);	
	    startActivity(intent);
	} 
	finish(); 
 	}
public void mapa(View view) {
 	String realidad = "RealidadPosition";
	Intent i = new Intent(this, Mapa.class );
	i.putExtra("RealidadPosition", realidad);
    startActivity(i);
    finish();
    }
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
        atras(null);
        //moveTaskToBack(false);
        return true;
    }
    return super.onKeyDown(keyCode, event);
}

@Override
protected void onPause() {
	super.onPause();
	// Stop GenRadarManager Processing
	// This is mandatory to unregister the GenRadarManager for batter saving
	if(locMan!=null){
		locMan.removeUpdates(this);  
	}	
}
public void comprobarSensores(){
	PackageManager PM = getPackageManager();
	boolean compass = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
	boolean accelerometer = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
	if (!compass || !accelerometer) {
		Toast toast = Toast.makeText(RealidadAumentada.this, "No dispone de aceler�metro y br�jula para utilizar la Realidad Aumentada.", Toast.LENGTH_SHORT);
	    toast.show();
	}
}

@Override
public void onScreenshot(Bitmap screenshot) {
	// TODO Auto-generated method stub
	
}
}



