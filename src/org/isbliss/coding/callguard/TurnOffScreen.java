/**
 * @author Chris Portway
 * This file is part of Call Guard.
 * 
 * Copyright (C) 2014  Chris Portway
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package org.isbliss.coding.callguard;

import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

/**
 * Activity to disable the screen either by locking or setting brightness to 0. 
 * Both techniques do not work on all phone models. 
 */
public class TurnOffScreen extends Activity {

	//TODO Incomplete: Lock method works, but there's no way for the user to choose between
	// locking method and brightness method yet. 
	
	protected Handler mHandler = new Handler();	
	private static boolean alreadyTurnedOffOnce = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO Optional: Also call this when screen is powered off manually or screen times out during a call (EXTRA_STATE_OFFHOOK)
		//make the activity invisible to the user
		getWindow().addFlags(LayoutParams.FLAG_NOT_TOUCHABLE);
		getWindow().addFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL);
	}
	
	/*
	 * TelephonyManager.EXTRA_STATE_IDLE    - no call
	 * TelephonyManager.EXTRA_STATE_OFFHOOK - currently in an active call
	 * TelephonyManager.EXTRA_STATE_RINGING - sending or receiving a call
	 */
	
	@Override	
	protected void onResume(){
		super.onResume();
		Bundle extras = getIntent().getExtras();
        DevicePolicyManager dpm = (DevicePolicyManager)this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        
		if(MainActivity.settingUseLock && !alreadyTurnedOffOnce 
				&& dpm.isAdminActive(MainActivity.deviceAdminReceiverCName)){
			Log.d("callguard","Admin enabled, locking");
			dpm.lockNow();
			if (TelephonyManager.EXTRA_STATE_IDLE.equals(extras.getString("BUNDLED_TELEPHONY_STATE"))){
				//Phone has hung up, we can safely finish().
				finish();
			}
		}else{
			alreadyTurnedOffOnce = true;
	    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	        if (pm.isScreenOn() == false) {
	        	restoreBrightness();
	        }else{ 
		        if(extras != null && TelephonyManager.EXTRA_STATE_IDLE.equals(extras.getString("BUNDLED_TELEPHONY_STATE"))){
		        	restoreBrightness();
		        	finish();
		        }else if(extras != null && TelephonyManager.EXTRA_STATE_RINGING.equals(extras.getString("BUNDLED_TELEPHONY_STATE"))){
		        	restoreBrightness();
		        }else{
		        	if (alreadyTurnedOffOnce)
		        		restoreBrightness();
		        	else
		        		turnOffByBrightness();
		        }    
	        }
		}		
        //TODO Optional: Add alternate method using FLAG_DISMISS_KEYGUARD too?
		//TODO Optional: Add alternate method using screen timeout times too? 
		//     Has limitation screen timout can't be less than 5 second on most models.
	}
	
	@Override
	protected void onPause(){
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (MainActivity.settingUseLock && pm.isScreenOn() == false) {
        	restoreBrightness();
        	finish();
        }
    	super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if (MainActivity.settingUseLock && getWindow().getAttributes().screenBrightness == 0.0f)
			restoreBrightness();
    	alreadyTurnedOffOnce = false;
		super.onDestroy();
	} 
	
	/**
	 * Disable the screen by setting brightness to 0. Does not work on all phone models.
	 * On some models, pressing the power button will only enable backlight when brightness == 0,
	 * and the screen will remain unusable. Therefore ensure {@link callguard.TurnOffScreen#restoreBrightness} 
	 * is always called after {@link callguard.TurnOffScreen#turnOffByBrightness} before this activity is destroyed.
	 */
	public void turnOffByBrightness(){
		//TODO retrieve the actual brightness, store it, use it.
    	Log.d("callguard","brightness 0.0 (turnOffByDimming)");
    	alreadyTurnedOffOnce = true;
    	Window window = getWindow();
		LayoutParams params = window.getAttributes();
    	params.screenBrightness = 0.0f;
    	window.setAttributes(params);
	}

	/**
	 * Enable the screen by setting brightness to non-0. Does not work on all phone models.
	 * On some models, pressing the power button will only enable backlight when brightness == 0,
	 * and the screen will remain unusable. Therefore ensure {@link callguard.TurnOffScreen#restoreBrightness} 
	 * is always called after {@link callguard.TurnOffScreen#turnOffByBrightness} before this activity is destroyed.
	 */
	public void restoreBrightness(){
		//TODO retrieve the actual brightness, store it, use it.
		Log.d("callguard","brightness -1.0 (resetDimming)");
		Window window = getWindow();
		LayoutParams params = window.getAttributes();
		params.screenBrightness = -1.0f;
		window.setAttributes(params);
	}	
}
