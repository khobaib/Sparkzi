package com.sparkzi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.makeramen.RoundedDrawable;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Base64;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

import eu.janmuller.android.simplecropimage.CropImage;

public class UploadPicActivity extends Activity {

	private Uri mImageCaptureUri;
	private File picFile;
	private static final int CAMERA_REQ_CODE = 901;
	private static final int CROP_REQ_CODE = 1001;
	private static final int GALLERY_REQ_CODE = 902;

	private static final String TAG = UploadPicActivity.class.getSimpleName();

	private ProgressDialog pDialog;
	JsonParser jsonParser;

	String token;

	private static SparkziApplication appInstance;

	ImageView ivProfilePic;
	Button btnUpdate;

	Bitmap scaledBmp;

	int fromActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.upload_pic);

		fromActivity = getIntent().getExtras().getInt(Constants.FROM_ACTIVITY);
		Log.e(TAG, "fromActivity = " + fromActivity);

		appInstance = (SparkziApplication) getApplication();
		jsonParser = new JsonParser();

		scaledBmp = null;

		ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
		btnUpdate = (Button) findViewById(R.id.b_update);
		btnUpdate.setVisibility(View.GONE);

		UserCred userCred = appInstance.getUserCred();
		String imageUrl = userCred.getPicUrl();

		ImageLoader imageLoader = new ImageLoader(UploadPicActivity.this);
		if (fromActivity == Constants.PARENT_ACTIVITY_PROFILE) {
			// imageLoader.DisplayImage(imageUrl, ivProfilePic);
			ivProfilePic.setBackgroundResource(android.R.color.transparent);
			ivProfilePic.setImageBitmap(imageLoader.getRoundedPicFromURL(
					imageUrl, ivProfilePic));
			btnUpdate.setVisibility(View.VISIBLE);
		}

		token = userCred.getToken();
		// imageLoader.DisplayImage(imageUrl, ivProfilePic);
		ivProfilePic.setBackgroundResource(android.R.color.transparent);
		ivProfilePic.setImageBitmap(imageLoader.getRoundedPicFromURL(imageUrl,
				ivProfilePic));
		if (isSDCardMounted()) {
			picFile = new File(Constants.APP_DIRECTORY.toString(),
					"profile_pic.png");
			Log.d(TAG,
					"SD Card mounted and picFile initialized as: "
							+ picFile.toString());
		} else {
			Toast.makeText(UploadPicActivity.this, "SD Card not mounted!",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		BugSenseHandler.startSession(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(this);
	}

	public void onClickTakePic(View v) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			// Set the file name
			File directory = Constants.APP_DIRECTORY;
			String mainDir = directory.toString();
			String photoFileName = "profile_pic" + ".png";
			Log.d(TAG, photoFileName);
			picFile = new File(mainDir, photoFileName);

			// set uri from the file
			// mImageCaptureUri = Uri.fromFile(picFile);
			if (isSDCardMounted()) {
				mImageCaptureUri = Uri.fromFile(picFile);
			} else {
				Toast.makeText(UploadPicActivity.this, "Media Not Mounted!",
						Toast.LENGTH_LONG).show();
				return;
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);
			Log.d(TAG, "cam intent starting");
			startActivityForResult(intent, CAMERA_REQ_CODE);
		} catch (ActivityNotFoundException e) {
			Log.d("Error", "Activity Not Found" + e.toString());
		}
	}

	public void onClickGoToGallery(View v) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// Intent intent = new Intent(Intent.ACTION_PICK,
		// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// startActivityForResult(Intent.createChooser(intent,
		// TODO "Select Picture"),
		startActivityForResult(intent, GALLERY_REQ_CODE);
	}

	private void startCropImage() {

		Intent intent = new Intent(this, CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH, picFile.getPath());
		intent.putExtra(CropImage.SCALE, true);

		intent.putExtra(CropImage.ASPECT_X, 2);
		intent.putExtra(CropImage.ASPECT_Y, 2);

		Log.d(TAG, "Starting crop from startCropImage()");
		startActivityForResult(intent, CROP_REQ_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CAMERA_REQ_CODE:
				Log.d(TAG, "cam onActivityResult");
				try {
					Bitmap bmp = decodeFile(picFile, 500);
					int angle = getCorrectionAngleForCam();
					int w = bmp.getWidth();
					if (w < 200)
						w = 200;
					int h = bmp.getHeight();
					if (h < 200)
						h = 200;
					if (angle == 0) {
						scaledBmp = Bitmap.createScaledBitmap(bmp, w, h, true);
					} else {
						Matrix mat = new Matrix();
						mat.postRotate(angle);
						Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0,
								bmp.getWidth(), bmp.getHeight(), mat, true);
						scaledBmp = Bitmap.createScaledBitmap(correctBmp, w, h,
								true);
						Log.d("", "scaled");
					}

					try {
						FileOutputStream out = new FileOutputStream(picFile);
						scaledBmp.compress(Bitmap.CompressFormat.PNG, 90, out);
						mImageCaptureUri = Uri.fromFile(picFile);
						out.close();
					} catch (Exception e) {
						Log.e("Error_Touhid", e.toString());
					}
					// code b4 crop: ivProfilePic.setImageBitmap(scaledBmp);
					// btnUpdate.setVisibility(View.VISIBLE);
				} catch (IOException e) {
					Toast.makeText(UploadPicActivity.this,
							"IOException - Failed to load", Toast.LENGTH_SHORT)
							.show();
					Log.e("Camera", e.toString());
				} catch (OutOfMemoryError oom) {
					Toast.makeText(UploadPicActivity.this,
							"OOM error - Failed to load", Toast.LENGTH_SHORT)
							.show();
					Log.e("Camera", oom.toString());
				}
				Log.d(TAG, "starting crop");
				startCropImage();
				//
				// doCrop();
				// imageUriToString = mImageCaptureUri.toString();
				break;

			case GALLERY_REQ_CODE:
				try {
					mImageCaptureUri = data.getData();

					if (getPath(mImageCaptureUri) != null) {

						InputStream inputStream = getContentResolver()
								.openInputStream(mImageCaptureUri);
						FileOutputStream fileOutputStream = new FileOutputStream(
								picFile);
						copyStream(inputStream, fileOutputStream);
						fileOutputStream.close();
						inputStream.close();

						Log.d(TAG, "Gal code: starting crop");
						startCropImage();
					} else {
						System.out.println("Picasa Image!");
						loadPicasaImageFromGallery(mImageCaptureUri);
						startCropImage();
					}

				} catch (Exception e) {
					Log.e(TAG, "Error while creating thr picture file", e);
				}
				// doCrop();
				// imageUriToString = mImageCaptureUri.toString();
				break;

			case CROP_REQ_CODE:
				String path = data.getStringExtra(CropImage.IMAGE_PATH);
				Log.d(TAG, "path = " + path);
				if (path == null) {
					return;
				}
				scaledBmp = BitmapFactory.decodeFile(picFile.getPath());
				scaledBmp = Bitmap
						.createScaledBitmap(scaledBmp, 200, 200, true);
				ivProfilePic.setBackgroundResource(android.R.color.transparent);
				ivProfilePic.setImageBitmap(getRoundedImage(scaledBmp, 200));
				btnUpdate.setVisibility(View.VISIBLE);
				break;

			}
		}
	}

	/** @author Touhid */
	private Bitmap getRoundedImage(Bitmap bitmap, int cornerRadius) {
		Bitmap scaled = Bitmap.createScaledBitmap(bitmap, cornerRadius,
				cornerRadius, false);
		return RoundedDrawable.fromBitmap(scaled)
				.setScaleType(ImageView.ScaleType.CENTER_CROP)
				.setCornerRadius(cornerRadius).setOval(true).toBitmap();
	}

	private int getCorrectionAngleForCam() throws IOException {
		ExifInterface exif = new ExifInterface(picFile.getPath());
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_NORMAL);

		int angle = 0;
		if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
			angle = 90;
		} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
			angle = 180;
		} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
			angle = 270;
		}
		Log.d(TAG, "angle = " + angle);
		return angle;
	}

	private void loadPicasaImageFromGallery(final Uri uri) {
		String[] projection = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
			if (columnIndex != -1) {
				new Thread(new Runnable() {
					public void run() {
						try {
							Bitmap bitmap = android.provider.MediaStore.Images.Media
									.getBitmap(getContentResolver(), uri);
							scaledBmp = Bitmap.createScaledBitmap(bitmap, 200,
									200, true);

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										FileOutputStream out = new FileOutputStream(
												picFile);
										scaledBmp.compress(
												Bitmap.CompressFormat.PNG, 90,
												out);
										mImageCaptureUri = Uri
												.fromFile(picFile);
										out.close();
										// ivProfilePic.setImageBitmap(scaledBmp);
										// btnUpdate.setVisibility(View.VISIBLE);
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							});

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}).start();
			}
		}
		cursor.close();

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		} else
			return uri.getPath();
	}

	public void onClickUpdate(View v) {
		if (scaledBmp == null) {
			alert("Please update image first.");
			return;
		}
		new UploadProfilePicture().execute();
		// finish();
		// overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
			finish();
			// BugSenseHandler.closeSession(UploadPicActivity.this);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class UploadProfilePicture extends
			AsyncTask<Void, Void, JSONObject> {

		protected void onPreExecute() {
			pDialog = new ProgressDialog(UploadPicActivity.this);
			pDialog.setMessage("Uploading profile pic ...");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected JSONObject doInBackground(Void... params) {

			// Bitmap bitmapOrg =
			// BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			scaledBmp.compress(Bitmap.CompressFormat.PNG, 90, bao);
			byte[] ba = bao.toByteArray();
			Log.d("BITMAP SIZE in asynctask", "bitmap size after compress = "
					+ ba.length);

			String base64Str = Base64.encodeBytes(ba);
			// ArrayList<NameValuePair> contentParams = new
			// ArrayList<NameValuePair>();
			// contentParams.add(new BasicNameValuePair("image", base64Str));

			// List<NameValuePair> urlParam = new ArrayList<NameValuePair>();
			// urlParam.add(new BasicNameValuePair("method",
			// Constants.METHOD_UPLOAD_PROFILE_PICTURE));

			String url = Constants.URL_ROOT + "pic";

			try {
				JSONObject contentObj = new JSONObject();
				contentObj.put("pic", base64Str);
				contentObj.put("picname", "hello");
				String contentData = contentObj.toString();
				Log.d(">>>><<<<<", "content = " + contentData);

				// String appToken = appInstance.getAccessToken();
				ServerResponse response = jsonParser.retrieveServerData(
						Constants.REQUEST_TYPE_PUT, url, null, contentData,
						token);

				if (response.getStatus() == 200) {
					Log.d(">>>><<<<", "success in retrieving response in login");
					JSONObject responseObj = response.getjObj();
					return responseObj;
				} else
					return null;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}

			// if(response.getStatus() == 200){
			// Log.d(">>>><<<<", "success in getting response");
			// JSONObject responsObj = response.getjObj();
			//
			//
			// String responseStr = responsObj.getString("response");
			// if(responseStr.equals("success")){
			// String imageUrl = responsObj.getString("image_url");
			// appInstance.setProfileImageUrl(imageUrl);
			// return true;
			// }
			// else{
			// return false;
			// }
			// }
			//
			// }catch (JSONException e) {
			// // Auto-generated catch block
			// e.printStackTrace();
			// }
			// return false;
		}

		protected void onPostExecute(JSONObject responseObj) {
			if (pDialog.isShowing())
				pDialog.dismiss();
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {

						String imageUrl = responseObj.getString("picUrl");
						if (!(imageUrl == null) && !imageUrl.equals("null")
								&& !imageUrl.startsWith("http://")
								&& !imageUrl.startsWith("https://")) {
							imageUrl = "http://sparkzi.com/api/apinew/"
									+ imageUrl;
							Log.d(TAG,
									"imageUrl after uploading in UploadPicActivity = "
											+ imageUrl);
							// Log.d("??????????", "image url = " + imageUrl);
							UserCred userCred = appInstance.getUserCred();
							userCred.setPicUrl(imageUrl);
							appInstance.setUserCred(userCred);
						}

						if (fromActivity == Constants.PARENT_ACTIVITY_LOGIN) {
							runOnUiThread(new Runnable() {
								public void run() {
									showUpdateEssentialsDialog();
									// show dialog answer dialog

									// Intent i = new Intent(LoginActivity.this,
									// UploadPicActivity.class);
									// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									// startActivity(i);
									// finish();
								}
							});
						} else {
							finish();
						}
					} else {
						alert("Upload failed. Please try again.");
					}
				} catch (JSONException e) {
					alert("Registration Exception.");
					e.printStackTrace();
				}
			} else {
				alert("Server error. Please try again.");
			}

		}
	}

	private void showUpdateEssentialsDialog() {
//		LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
//		View addDIalogView = inflater.inflate(
//				R.layout.dialog_answer_essentials, null);
//		final AlertDialog alert = new AlertDialog.Builder(
//				UploadPicActivity.this).create();
//		alert.setView(addDIalogView);
//		alert.setCancelable(false);
//
//		Button bLater = (Button) addDIalogView.findViewById(R.id.b_later);
//		bLater.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				alert.dismiss();
//				Intent i = new Intent(UploadPicActivity.this,
//						MainActivity.class);
//				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(i);
//				finish();
//			}
//
//		});
//
//		Button bTryOut = (Button) addDIalogView.findViewById(R.id.b_try_out);
//		bTryOut.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				alert.dismiss();
				Intent i = new Intent(UploadPicActivity.this,
						EssentialDetailsActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				Bundle bundle = new Bundle();
				bundle.putInt(Constants.FROM_ACTIVITY,
						Constants.PARENT_ACTIVITY_LOGIN);
				i.putExtras(bundle);

				startActivity(i);
				finish();
//			}
//
//		});
//		alert.show();
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(
				UploadPicActivity.this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		bld.create().show();
	}

	private Bitmap decodeFile(File f, int imageQuality) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = imageQuality;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;

			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			Log.i("SCALE", "scale = " + scale);

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isSDCardMounted() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}

	}

}

// private String imageUriToString;
// private static final int GALLERY_CROP_REQ_CODE = 1002;
// ImageLoader imageLoader;
// Bitmap bitmap;
// private Boolean isPicassaImage;

// private String selectedImagePath;

// ADDED
// private String filemanagerstring;

/**
 * case CAMERA_CROP_REQ_CODE:// TODO_DONE
 * 
 * Bundle extras = data.getExtras();
 * 
 * if (extras != null) { Bitmap photo = extras.getParcelable("data"); // String
 * imageUriToString = mImageCaptureUri.toString();
 * ivProfilePic.setImageBitmap(photo); btnUpdate.setVisibility(View.VISIBLE);//
 * here }
 * 
 * // File f = new File(mImageCaptureUri.getPath()); // // if (f.exists()) //
 * f.delete(); break;
 */

/**
 * 
 case GALLERY_CROP_REQ_CODE: Uri selectedImageUri = data.getData();
 * Log.d("URI VAL", "selectedImageUri = " + selectedImageUri.toString()); String
 * selectedImagePath = getPath(selectedImageUri);
 * 
 * if (selectedImagePath != null) { // if local image System.out
 * .println("selectedImagePath is the right one for you!"); FileInputStream in;
 * BufferedInputStream buf; try { in = new FileInputStream(selectedImagePath);
 * buf = new BufferedInputStream(in, 1070); byte[] bMapArray = new
 * byte[buf.available()]; buf.read(bMapArray); Bitmap bMap =
 * BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length); scaledBmp =
 * Bitmap.createScaledBitmap(bMap, 200, 200, true);
 * ivProfilePic.setImageBitmap(scaledBmp);
 * btnUpdate.setVisibility(View.VISIBLE); if (in != null) in.close(); if (buf !=
 * null) buf.close(); } catch (Exception e) { Log.e("Error reading file",
 * e.toString()); } } else { System.out.println("picasa image!");
 * loadPicasaImageFromGallery(selectedImageUri); }
 * 
 * break;
 */

/**
 * private void doCrop() { final ArrayList<CropOption> cropOptions = new
 * ArrayList<CropOption>();
 * 
 * Intent intent = new Intent("com.android.camera.action.CROP");
 * intent.setType("image/*");
 * 
 * List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent,
 * 0);
 * 
 * int size = list.size();
 * 
 * if (size == 0) { Toast.makeText(this, "Can not crop the image",
 * Toast.LENGTH_SHORT) .show(); return; } else {
 * intent.setData(mImageCaptureUri);
 * 
 * intent.putExtra("crop", "true"); intent.putExtra("outputX", 130);
 * intent.putExtra("outputY", 130); intent.putExtra("aspectX", 1);
 * intent.putExtra("aspectY", 1); intent.putExtra("scale", true);
 * intent.putExtra("return-data", false);
 * 
 * intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
 * intent.putExtra("noFaceDetection", false);
 * 
 * if (size == 1) { Intent i = new Intent(intent); ResolveInfo res =
 * list.get(0); // TODO_DONE i.setComponent(new
 * ComponentName(res.activityInfo.packageName, res.activityInfo.name));
 * startActivityForResult(i, CAMERA_CROP_REQ_CODE); } else { for (ResolveInfo
 * res : list) { final CropOption co = new CropOption();
 * 
 * co.title = getPackageManager().getApplicationLabel(
 * res.activityInfo.applicationInfo); co.icon =
 * getPackageManager().getApplicationIcon( res.activityInfo.applicationInfo);
 * co.appIntent = new Intent(intent);
 * 
 * co.appIntent .setComponent(new ComponentName( res.activityInfo.packageName,
 * res.activityInfo.name)); cropOptions.add(co); }
 * 
 * CropOptionAdapter adapter = new CropOptionAdapter( getApplicationContext(),
 * cropOptions);
 * 
 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
 * builder.setTitle("Choose Crop App"); builder.setAdapter(adapter, new
 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
 * dialog, int item) { startActivityForResult( cropOptions.get(item).appIntent,
 * CAMERA_CROP_REQ_CODE); } });
 * 
 * builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
 * 
 * @Override public void onCancel(DialogInterface dialog) { if (mImageCaptureUri
 *           != null) { getContentResolver().delete(mImageCaptureUri, null,
 *           null); mImageCaptureUri = null; } } });
 * 
 *           AlertDialog alert = builder.create(); alert.show(); } } }
 * 
 *           private Uri getTempUri() { return Uri.fromFile(getTempFile()); }
 * 
 *           private File getTempFile() { if (isSDCARDMounted()) {
 * 
 *           File directory = Constants.APP_DIRECTORY; directory.mkdirs();
 *           String mainDir = directory.toString();
 * 
 *           // long currentTime = System.currentTimeMillis(); String
 *           photoFileName = "profile_pic" + ".png";
 * 
 *           File photoFolder = new File(mainDir, photoFileName);
 *           mImageCaptureUri = Uri.fromFile(photoFolder); // nameOfFile =
 *           imageUri.toString();
 * 
 *           return photoFolder; } else { return null; } }
 */
