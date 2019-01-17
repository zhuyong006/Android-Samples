/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	static String enable_gadge_fd = "/sys/class/android_usb/android0/enable";
	static String acm_transports_fd = "/sys/class/android_usb/android0/f_acm/acm_transports";
	static String gadge_acm_functions_fd = "/sys/class/android_usb/android0/functions";
	static FileOutputStream fw = null;
	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				SetGadgeAcm();
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}

		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	static void WriteFile(String name,String value) throws IOException{

		BufferedWriter bufWriter = null;
		try {
			bufWriter = new BufferedWriter(new FileWriter(name));
			bufWriter.write(value);
			bufWriter.close();
		}catch (IOException e) {
			throw new IOException();
		}

	}

	static void CheckFilesPermission() throws IOException{

		boolean access = true;
		access = new File(enable_gadge_fd).canWrite();
		if(!access) {
			Log.e(TAG, enable_gadge_fd + " can't access");
			throw new IOException();
		}
		access = new File(acm_transports_fd).canWrite();
		if(!access) {
			Log.e(TAG, acm_transports_fd + " can't access");
			throw new IOException();
		}
		access = new File(gadge_acm_functions_fd).canWrite();
		if(!access) {
			Log.e(TAG, gadge_acm_functions_fd + " can't access");
			throw new IOException();
		}

	}

	static public void SetGadgeAcm() throws IOException{

		try {
			CheckFilesPermission();
			Log.e(TAG, "SetGadgeAcm");
			WriteFile(enable_gadge_fd,"0");
			WriteFile(acm_transports_fd,"TTY");
			WriteFile(gadge_acm_functions_fd,"acm");
			WriteFile(enable_gadge_fd,"1");
		}catch (IOException e) {
			throw new IOException();
		}

	}

	 static public void SetGadgeAdb()throws IOException{
		try {
			Log.e(TAG, "SetGadgeAdb");
			WriteFile(enable_gadge_fd,"0");
			WriteFile(gadge_acm_functions_fd,"adb");
			WriteFile(enable_gadge_fd,"1");
		}catch (IOException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}
	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags);
	private native void close();
	static {
		System.loadLibrary("serial_port");
	}
}
