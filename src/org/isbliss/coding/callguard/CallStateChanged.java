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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Receives a change in the phone state to turn of the screen (and possibly lock) when a call begins. 
 */
public class CallStateChanged extends BroadcastReceiver { 
	
    protected static final int REQUEST_CODE_ENABLE_ADMIN=1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Received", intent.getAction() + intent.getDataString() + intent.getType() + "\n" + intent );
		
		/*
		 * TelephonyManager.EXTRA_STATE_IDLE    - no call
		 * TelephonyManager.EXTRA_STATE_OFFHOOK - currently in an active call
		 * TelephonyManager.EXTRA_STATE_RINGING - sending or receiving a call
		 */
		
		//Start activity to turn off screen with brightness hack. Can't be done directly in BroadcastReceiver.
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		//Ignore change to the ringing state for now.
		if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)
				|| TelephonyManager.EXTRA_STATE_IDLE.equals(state)){
			Intent i = new Intent();
			i.setClassName("org.isbliss.coding.callguard", "org.isbliss.coding.callguard.TurnOffScreen");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);		
			i.putExtra("BUNDLED_TELEPHONY_STATE", state);
			context.startActivity(i);
		}
		
	}
}
