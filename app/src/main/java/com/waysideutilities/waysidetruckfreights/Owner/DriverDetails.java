package com.waysideutilities.waysidetruckfreights.Owner;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.PojoClasses.Truck;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.FrightUtils;
import com.waysideutilities.waysidetruckfreights.helper.VolleyMultipartRequest;
import com.waysideutilities.waysidetruckfreights.helper.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.waysideutilities.waysidetruckfreights.helper.Constants.CAMERA_REQUEST_CODE;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.GALLARY_REQUEST_CODE;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS;

/**
 * Created by Archana on 8/29/2016.
 */
public class DriverDetails extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView imageTruck, imageLicence, imageInsurance_provider, imageVehicleRegCopy;
    private int cameraPermission, storagePermission;
    public Uri fileUri, captureImageuri, insuranceFileUri, licenceFileUri, truckFileUri, truckRegUri = null;
    private EditText txtDriverName, txtDriverNumber, txtTruckRegNumber, txtLoadPassing, txtCity;
    private Button btnTruckNext;
    private int imageSelector;
    private String language;
    private Bundle bundle = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_driver_details);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.driver_detail);

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        imageTruck = (ImageView) findViewById(R.id.imageTruck);
        imageTruck.setOnClickListener(DriverDetails.this);

        imageLicence = (ImageView) findViewById(R.id.imageLicence);
        imageLicence.setOnClickListener(DriverDetails.this);

        imageInsurance_provider = (ImageView) findViewById(R.id.imageInsurance_provider);
        imageInsurance_provider.setOnClickListener(DriverDetails.this);

        imageVehicleRegCopy = (ImageView) findViewById(R.id.imageVehicleRegCopy);
        imageVehicleRegCopy.setOnClickListener(DriverDetails.this);

        txtDriverName = (EditText) findViewById(R.id.txtDriverName);
        txtDriverNumber = (EditText) findViewById(R.id.txtDriverNumber);
       // txtTruckNumber = (EditText) findViewById(R.id.txtTruckNumber);
        txtTruckRegNumber = (EditText) findViewById(R.id.txtTruckRegNumber);
        txtLoadPassing = (EditText) findViewById(R.id.txtLoadPassing);
        txtCity = (EditText) findViewById(R.id.txtCity);
        btnTruckNext = (Button) findViewById(R.id.btnNext);
        btnTruckNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtDriverName.getText().toString().length() == 0) {
                    Toast.makeText(DriverDetails.this, R.string.err_msg_driver_name, Toast.LENGTH_SHORT).show();
                } else if (txtDriverNumber.getText().toString().length() == 0) {
                    Toast.makeText(DriverDetails.this, R.string.err_msg_driver_number, Toast.LENGTH_SHORT).show();
                } /*else if (txtTruckNumber.getText().toString().length() == 0) {
                    Toast.makeText(DriverDetails.this, R.string.err_msg_Truck_Number, Toast.LENGTH_SHORT).show();
                }*/ else if (txtTruckRegNumber.getText().toString().length() == 0) {
                    Toast.makeText(DriverDetails.this, R.string.err_msg_Truck_RegNumber, Toast.LENGTH_SHORT).show();
                } else if (txtLoadPassing.getText().toString().length() == 0) {
                    Toast.makeText(DriverDetails.this, R.string.err_msg_Load_Passing, Toast.LENGTH_SHORT).show();
                } else if (txtCity.getText().toString().length() == 0) {
                    Toast.makeText(DriverDetails.this, R.string.err_msg_city, Toast.LENGTH_SHORT).show();
                } else {
                    Truck truck = new Truck();
                    truck.setDriverName(txtDriverName.getText().toString());
                    truck.setDriverNumber(txtDriverNumber.getText().toString());
                    //truck.setTruckNumber(txtTruckNumber.getText().toString());
                    truck.setTruckRegNumber(txtTruckRegNumber.getText().toString());
                    truck.setTruckLoadPassing(txtLoadPassing.getText().toString());
                    truck.setTruckCity(txtCity.getText().toString());
                    UploadImage(truck);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.imageTruck) {
            imageSelector = 1;
            getImage();
        }
        if (view.getId() == R.id.imageLicence) {
            imageSelector = 2;
            getImage();
        }
        if (view.getId() == R.id.imageInsurance_provider) {
            imageSelector = 3;
            getImage();
        }
        if (view.getId() == R.id.imageVehicleRegCopy) {
            imageSelector = 4;
            getImage();
        }
    }

    private void getImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraPermission == 0 && storagePermission == 0) {
            selectImage();
        } else {
            checkForPermission();
        }
    }

    private void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int count = 0;
            String[] permissions = new String[]{"", ""};
            if ((cameraPermission != PackageManager.PERMISSION_GRANTED) || (storagePermission != PackageManager.PERMISSION_GRANTED)) {
                if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                    permissions[count++] = Manifest.permission.CAMERA;
                }
                if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                    permissions[count] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }
                requestPermissions(permissions, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if ((grantResults.length == 2) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(DriverDetails.this, "CAMERA PERMISSION_GRANTED", Toast.LENGTH_SHORT);
                    cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
                    storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (cameraPermission == 0 && storagePermission == 0) {
                        selectImage();
                    }
                } else if ((grantResults[0] == PackageManager.PERMISSION_DENIED) && (grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(DriverDetails.this, "CAMERA permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
                    storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (cameraPermission == 0 && storagePermission == 0) {
                        selectImage();
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(DriverDetails.this, "CAMERA permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] options = {getResources().getString(R.string.take_photo), getResources().getString(R.string.choose_from_gallery), getResources().getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverDetails.this);
        builder.setTitle(getResources().getString(R.string.add_photo));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getResources().getString(R.string.take_photo))) {
                    cameraIntent();
                } else if (options[item].equals(getResources().getString(R.string.choose_from_gallery))) {
                    galleryIntent();
                } else if (options[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLARY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                onCaptureImageResult(data);
            } else if (requestCode == GALLARY_REQUEST_CODE) {
                onSelectFromGalleryResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageSelector == 1) {
            imageTruck.setImageBitmap(thumbnail);
        } else if (imageSelector == 2) {
            imageLicence.setImageBitmap(thumbnail);
        } else if (imageSelector == 3) {
            imageInsurance_provider.setImageBitmap(thumbnail);
        } else {
            imageVehicleRegCopy.setImageBitmap(thumbnail);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (imageSelector == 1) {
            imageTruck.setImageBitmap(bm);
        } else if (imageSelector == 2) {
            imageLicence.setImageBitmap(bm);
        } else if (imageSelector == 3) {
            imageInsurance_provider.setImageBitmap(bm);
        } else {
            imageVehicleRegCopy.setImageBitmap(bm);
        }
    }

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void UploadImage(final Truck truck) {
        progressDialog = new ProgressDialog(DriverDetails.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://www.waysideutilities.com/api/fill_truck_detail.php";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                progressDialog.dismiss();
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("success");
                    String message = result.getString("message");

                    if (status.equals("1")) {
                        Log.i("Messsage", message);
                        finish();
                    } else {
                        Log.i("Unexpected", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        //String status = response.getString("status";
                        String message = response.getString("success_message");

                        //Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        })
         {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                params.put("driver_name", truck.getDriverName());
                params.put("driver_mobile_no", truck.getDriverNumber());
               // params.put("truck_no", truck.getTruckNumber());
                params.put("truck_reg_no",truck.getTruckRegNumber());
                params.put("load_passing", truck.getTruckLoadPassing());
                params.put("truck_city", truck.getTruckCity());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("truck_image", new DataPart("truck_image.jpg", getFileDataFromDrawable(getBaseContext(), imageTruck.getDrawable()), "image/jpeg"));
                params.put("insurance_copy", new DataPart("insurance_image.jpg", getFileDataFromDrawable(getBaseContext(), imageInsurance_provider.getDrawable()), "image/jpeg"));
                params.put("license_copy", new DataPart("license_image.jpg", getFileDataFromDrawable(getBaseContext(), imageLicence.getDrawable()), "image/jpeg"));
                params.put("vehicle_reg_copy", new DataPart("vehical_image.jpg", getFileDataFromDrawable(getBaseContext(), imageVehicleRegCopy.getDrawable()), "image/jpeg"));
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
