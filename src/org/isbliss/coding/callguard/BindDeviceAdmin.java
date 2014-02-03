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

import android.app.admin.DeviceAdminReceiver;

/**
* Dummy class needed to use device admin.
* This needs to not be a nested class in order to avoid system error messages from setComponentEnabledSetting(). 
*/
public class BindDeviceAdmin extends DeviceAdminReceiver {

	//Dummy class needed to use admin
	//Since all we do is lock the screen, we don't need anything more than a comment here.
}