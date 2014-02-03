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
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

/**
 * Toggle the application whenever the launcher is
 */
public class MainActivity extends Activity {
    static final ComponentName deviceAdminReceiverCName = 
    		new ComponentName("org.isbliss.coding.callguard",
    						  "org.isbliss.coding.callguard.BindDeviceAdmin");
    static final ComponentName broadcastReceiverCName = 
    		new ComponentName("org.isbliss.coding.callguard",
    						  "org.isbliss.coding.callguard.CallStateChanged");    
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
	
    //TODO make settingUseLock an actual user-changeable setting.
	static boolean settingUseLock = true;
	
    private PackageManager pm;
    private int enabledState;
    //TODO remove this workaround that prevents onResume() loops.
//    private boolean firstAttemptToEnableDeviceManager = true;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d("create","main oncreate called.");
        super.onCreate(savedInstanceState);
        pm = getPackageManager();
        enabledState = pm.getComponentEnabledSetting(broadcastReceiverCName);
        
//        firstAttemptToEnableDeviceManager = true;
        
        //make the activity invisible to the user
		getWindow().addFlags(LayoutParams.FLAG_NOT_TOUCHABLE);
		getWindow().addFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL);		

		//First toggle Enable/Disabled state. Then if Enabled, ensure user has enabled Device Admin.
		DevicePolicyManager dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
//		super.onResume();
//		if(firstAttemptToEnableDeviceManager)
			if (enabledState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
			|| enabledState == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
			{
				Toast.makeText(this, getString(R.string.annouce_general_enable), Toast.LENGTH_SHORT).show();				
				//Enable device admin if not already enabled, call finish() in response. Else directly finish().
				if(settingUseLock && !dpm.isAdminActive(MainActivity.deviceAdminReceiverCName)){
//					firstAttemptToEnableDeviceManager = false;
					Intent i =new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminReceiverCName);
					i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Need new Admin");
					Log.d("callguard","Set admin on app launch");
					startActivityForResult(i,REQUEST_CODE_ENABLE_ADMIN);					
				}else{ //When not using a lock, or Device Admin is already enabled, just enable the broadcast receiver
				    // Wait two seconds so the user has a chance to see the message, then 
					// enable the broadcast receiver and kill this App (last param), while the broadcastReceiver remains listening
				    Handler handler = new Handler(); 
				    handler.postDelayed(new Runnable() { 
				         public void run() { 
				        	 pm.setComponentEnabledSetting(broadcastReceiverCName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,0); 
				         } 
				    }, 2000);
					
				}
			}else{
				Toast.makeText(this, getString(R.string.annouce_general_disable), Toast.LENGTH_SHORT).show();
			    // Wait two seconds so the user has a chance to see the message, then 
				// enable the broadcast receiver and kill this App (last param), while the broadcastReceiver remains listening
			    Handler handler = new Handler(); 
			    handler.postDelayed(new Runnable() { 
			         public void run() { 
			        	 pm.setComponentEnabledSetting(broadcastReceiverCName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,0); 
			         } 
			    }, 2000);
			}
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Warn the user the app isn't fully functional
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			if(resultCode == RESULT_OK){
				if (enabledState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
					|| enabledState == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT){
					Toast.makeText(this, getString(R.string.annouce_device_admin_enabled), Toast.LENGTH_LONG).show();
					Log.d("callguard","device admin enabled");
				    // Wait two seconds so the user has a chance to see the message, then 
					// enable the broadcast receiver and kill this App (last param), while the broadcastReceiver remains listening
				    Handler handler = new Handler(); 
				    handler.postDelayed(new Runnable() { 
				         public void run() { 
				        	 pm.setComponentEnabledSetting(broadcastReceiverCName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,0); 
				         } 
				    }, 2000);
				}          
			}
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, getString(R.string.warn_device_admin_canceled), Toast.LENGTH_LONG).show();
				Log.d("callguard","device admin canceled");
			}
		}
		finish();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}