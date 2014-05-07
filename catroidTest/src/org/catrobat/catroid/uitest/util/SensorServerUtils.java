/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.util.Log;

import junit.framework.AssertionFailedError;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

public final class SensorServerUtils {
	private static final String LOG_TEST = "SensorServerUtils::";

	// fields to provide ethernet connection to the arduino server
	private static Socket clientSocket = null;
	private static DataOutputStream sendToServer;
	private static BufferedReader receiveFromServer;

	// Enter the right IP address and port number to connect and request sensor values.
	private static final String SERVER_IP = "10.0.0.3";//"129.27.202.103";
	private static final int SERVER_PORT = 6789;
	private static final int GET_VIBRATION_VALUE_ID = 1;
	private static final int GET_LIGHT_VALUE_ID = 2;
	private static final int GET_VIBRATION_VALUE_ID = 1;
	private static final int CALIBRATE_VIBRATION_SENSOR_ID = 3;

	public static final int SET_LED_ON_VALUE = 1;
	public static final int SET_LED_OFF_VALUE = 0;

	public static final int SET_VIBRATION_ON_VALUE = 1;
	public static final int SET_VIBRATION_OFF_VALUE = 0;

	public static final int NETWORK_DELAY_MS = 500;

	private SensorServerUtils() {
	}

	public static void connectToArduinoServer() throws IOException {
		Log.d(LOG_TEST, "Trying to connect to server...");

		clientSocket = new Socket(SERVER_IP, SERVER_PORT);
		clientSocket.setKeepAlive(true);

		Log.d(LOG_TEST, "Connected to: " + SERVER_IP + " on port " + SERVER_PORT);
		sendToServer = new DataOutputStream( clientSocket.getOutputStream() );
		receiveFromServer = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
	}

	public static void closeConnection() throws IOException {
		if (clientSocket != null) {
			clientSocket.close();
		}
		clientSocket = null;
		sendToServer = null;
		receiveFromServer = null;
	}

	public static void checkLightSensorValue( int expected ) {

		char expectedChar;
		String assertString;
		String response;
		if ( expected == SET_LED_ON_VALUE ) {
			expectedChar = '1';
			assertString = "Error: LED is turned off!";
		} else {
			expectedChar = '0';
			assertString = "Error: LED is turned on!";
		}
		try {
			clientSocket.close();
			Thread.sleep(NETWORK_DELAY_MS);
			connectToArduinoServer();
			Log.d(LOG_TEST, "requesting sensor value: ");
			sendToServer.writeByte(Integer.toHexString(GET_LIGHT_VALUE_ID).charAt(0));
			sendToServer.flush();
			Thread.sleep(NETWORK_DELAY_MS);
			response = receiveFromServer.readLine();
			Log.d(LOG_TEST, "response received! " + response);

			assertFalse("Wrong Command!", response.contains("ERROR"));
			assertTrue( "Wrong data received!", response.contains( "LIGHT_END" ) );
			assertTrue( assertString, response.charAt(0) == expectedChar );

		} catch ( IOException ioException ) {
			throw new AssertionFailedError( "Data exchange failed! Check server connection!" );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
  
	public static void checkVibrationSensorValue( int expected ) {

		char expectedChar;
		String assertString;
		String response;
		if ( expected == SET_VIBRATION_ON_VALUE ) {
			expectedChar = '1';
			assertString = "Error: Vibrator is turned off!";
		} else {
			expectedChar = '0';
			assertString = "Error: Vibrator is turned on!";
		}
		try {
			clientSocket.close();
			Thread.sleep(NETWORK_DELAY_MS);
			connectToArduinoServer();
			Log.d(LOG_TEST, "requesting sensor value: ");
			sendToServer.writeByte(Integer.toHexString(GET_VIBRATION_VALUE_ID).charAt(0));
			sendToServer.flush();
			Thread.sleep(NETWORK_DELAY_MS);
			response = receiveFromServer.readLine();
			Log.d(LOG_TEST, "response received! " + response);

			assertFalse("Wrong Command!", response.contains("ERROR"));
			assertTrue( "Wrong data received!", response.contains( "VIBRATION_END" ) );
			assertTrue( assertString, response.charAt(0) == expectedChar );

		} catch ( IOException ioException ) {
			throw new AssertionFailedError( "Data exchange failed! Check server connection!" );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void calibrateVibrationSensor() {
		String response;
		try {
			clientSocket.close();
			Thread.sleep(NETWORK_DELAY_MS);
			connectToArduinoServer();
			Log.d(LOG_TEST, "requesting sensor value: ");
			sendToServer.writeByte(Integer.toHexString(CALIBRATE_VIBRATION_SENSOR_ID).charAt(0));
			sendToServer.flush();
			Thread.sleep(NETWORK_DELAY_MS);
			response = receiveFromServer.readLine();
			Log.d(LOG_TEST, "response received! " + response);

		} catch ( IOException ioException ) {
			throw new AssertionFailedError( "Data exchange failed! Check server connection!" );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
