package com.mcomobile.mercadillos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

public class Inicio extends Activity {
		 private long ms = 0;
		 private long splashDuration = 5000;
		 private boolean splashActive = true;
		 private boolean paused = false;

		 @Override
		 protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.splashscreen);
		  		  
		  final ImageView myAnimation2 = (ImageView)findViewById(R.id.animText);
		  final AnimationDrawable myAnimationDrawable2
		  = (AnimationDrawable)myAnimation2.getDrawable();

		  Thread mythread = new Thread() {
		   @Override
		public void run() {
		    try {
		     while (splashActive && ms < splashDuration) {
		      if (!paused)
		       ms = ms + 100;
		      sleep(100);		      
		      myAnimation2.post(
		    		  new Runnable(){

		    		    @Override
		    		    public void run() {
		    		     myAnimationDrawable2.start();
		    		    }
		    		  });
		     }
		    } catch (Exception e) {
		    } finally {
		     Intent intent = new Intent(Inicio.this,
		       MenuGral.class);
		     startActivity(intent);
		     finish();
		    }
		   }
		  };
		  mythread.start();
		 }
		}
