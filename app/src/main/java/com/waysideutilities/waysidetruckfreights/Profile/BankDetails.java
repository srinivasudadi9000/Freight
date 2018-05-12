package com.waysideutilities.waysidetruckfreights.Profile;

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
import com.squareup.picasso.Picasso;
import com.waysideutilities.waysidetruckfreights.BaseActivity;
import com.waysideutilities.waysidetruckfreights.PojoClasses.User;
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
public class BankDetails extends BaseActivity {
    private EditText txtOwnerName, txtOwnerCity, txtOwnerNo, txtOwnerAddress, txtAccName, txtAccNo, txtIFSCNo, txtBankName;
    private Toolbar toolbar;
    private Button btnSubmit, btnCancel;
    private ImageView imagePan;
    private int cameraPermission, storagePermission;
    private String language;
    private Bundle bundle;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("LANGUAGE", null);
        if (language != null)
            FrightUtils.SetChoosenLanguage(language, this.getApplicationContext());
        setContentView(R.layout.activity_bank_details);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.bank_details));
        bundle = getIntent().getBundleExtra("BUNDLE");
        if (bundle != null)
            setValueFromBundle(bundle);
    }

    private void setValueFromBundle(Bundle bundle) {
        if (bundle.getString("OWNER_NAME") != null)
            txtOwnerName.setText(bundle.getString("OWNER_NAME"));
        if (bundle.getString("OWNER_CITY") != null)
            txtOwnerCity.setText(bundle.getString("OWNER_CITY"));
        if (bundle.getString("OWNER_MOB_NO") != null)
            txtOwnerNo.setText(bundle.getString("OWNER_MOB_NO"));
        if ((bundle.getString("OWNER_PAN_ADHAR_NO") != null) && (!bundle.getString("OWNER_PAN_ADHAR_NO").equals("")))
            Picasso.with(getApplicationContext()).load(bundle.getString("OWNER_PAN_ADHAR_NO")).placeholder(R.drawable.image_not_found).noFade().error(R.drawable.image_not_found).resize(200, 200).centerCrop().into(imagePan);

        if (bundle.getString("OWNER_ADDRESS") != null)
            txtOwnerAddress.setText(bundle.getString("OWNER_ADDRESS"));
        if (bundle.getString("ACCOUNT_NAME") != null)
            txtAccName.setText(bundle.getString("ACCOUNT_NAME"));
        if (bundle.getString("ACCOUNT_NO") != null)
            txtAccNo.setText(bundle.getString("ACCOUNT_NO"));
        if (bundle.getString("IFSC_CODE") != null)
            txtIFSCNo.setText(bundle.getString("IFSC_CODE"));
        if (bundle.getString("BANK_NAME") != null)
            txtBankName.setText(bundle.getString("BANK_NAME"));
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imagePan = (ImageView) findViewById(R.id.imagePan);
        imagePan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        txtOwnerName = (EditText) findViewById(R.id.txtOwnerName);
        txtOwnerCity = (EditText) findViewById(R.id.txtOwnerCity);
        txtOwnerNo = (EditText) findViewById(R.id.txtOwnerNo);
        txtOwnerAddress = (EditText) findViewById(R.id.txtOwnerAddress);
        txtAccName = (EditText) findViewById(R.id.txtAccName);
        txtAccNo = (EditText) findViewById(R.id.txtAccNo);
        txtIFSCNo = (EditText) findViewById(R.id.txtIFSCNo);
        txtBankName = (EditText) findViewById(R.id.txtBankName);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtOwnerName.getText().toString().length() == 0) {
                    Toast.makeText(BankDetails.this, R.string.err_msg_owner_name, Toast.LENGTH_SHORT).show();
                } else if (txtOwnerCity.getText().toString().length() == 0) {
                    Toast.makeText(BankDetails.this, R.string.err_msg_owner_city, Toast.LENGTH_SHORT).show();
                } else if (txtOwnerNo.getText().toString().length() == 0) {
                    Toast.makeText(BankDetails.this, R.string.err_msg_owner_number, Toast.LENGTH_SHORT).show();
                } else if (txtOwnerAddress.getText().toString().length() == 0) {
                    Toast.makeText(BankDetails.this, R.string.err_msg_owner_address, Toast.LENGTH_SHORT).show();
                } else if (txtAccName.getText().toString().length() == 0) {
                    Toast.makeText(BankDetails.this, R.string.err_msg_account_name, Toast.LENGTH_SHORT).show();
                } else if (txtAccNo.getText().toString().length() == 0) {
                    Toast.makeText(BankDetails.this, R.string.err_msg_account_number, Toast.LENGTH_SHORT).show();
                } else if (txtIFSCNo.getText().toString().length() == 0) {
                    Toast.makeText(BankDetails.this, R.string.err_msg_owner_bank_ifsc_code, Toast.LENGTH_SHORT).show();
                } else if (txtBankName.getText().toString().length() == 0) {
                    Toast.makeText(BankDetails.this, R.string.err_msg_bank_name, Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User();
                    user.setTruckOwnerName(txtOwnerName.getText().toString());
                    user.setTruckOwnerCity(txtOwnerCity.getText().toString());
                    user.setTruckOwnerNo(txtOwnerNo.getText().toString());
                    user.setTruckOwnerAddress(txtOwnerAddress.getText().toString());
                    user.setTruckOwnerAccName(txtAccName.getText().toString());
                    user.setTruckOwnerAccNo(txtAccNo.getText().toString());
                    user.setTruckOwnerBankIFSCNo(txtIFSCNo.getText().toString());
                    user.setTruckOwnerBankName(txtBankName.getText().toString());
                    UploadImage(user);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtOwnerName.setText("");
                txtOwnerCity.setText("");
                txtOwnerNo.setText("");
                txtOwnerAddress.setText("");
                txtAccName.setText("");
                txtAccNo.setText("");
                txtIFSCNo.setText("");
                txtBankName.setText("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(BankDetails.this, Profile.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                    Toast.makeText(BankDetails.this, "CAMERA PERMISSION_GRANTED", Toast.LENGTH_SHORT);
                    cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
                    storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (cameraPermission == 0 && storagePermission == 0) {
                        selectImage();
                    }
                } else if ((grantResults[0] == PackageManager.PERMISSION_DENIED) && (grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(BankDetails.this, "CAMERA permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
                    storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (cameraPermission == 0 && storagePermission == 0) {
                        selectImage();
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(BankDetails.this, "CAMERA permission allows us to access phone state. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void selectImage() {

        final CharSequence[] options = {getResources().getString(R.string.take_photo), getResources().getString(R.string.choose_from_gallery), getResources().getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(BankDetails.this);
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
        imagePan.setImageBitmap(thumbnail);
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
        imagePan.setImageBitmap(bm);
    }

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void UploadImage(final User user) {
        progressDialog = new ProgressDialog(BankDetails.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://www.waysideutilities.com/api/bank_details.php";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                progressDialog.dismiss();
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("success");
                    Log.i("Result : ", resultResponse);

                    if (status.equals("1")) {
                        Log.i("Messsage", status);
                        startActivity(new Intent(BankDetails.this, Profile.class));
                        finish();
                    } else {
                        Log.i("Unexpected", status);
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                params.put("owner_name", user.getTruckOwnerName());
                params.put("owner_mobile_no", user.getTruckOwnerNo());
                params.put("owner_city", user.getTruckOwnerCity());
                params.put("owner_address", user.getTruckOwnerAddress());
                params.put("account_name", user.getTruckOwnerAccName());
                params.put("account_no", user.getTruckOwnerAccNo());
                params.put("IFSC_code", user.getTruckOwnerBankIFSCNo());
                params.put("bank_name", user.getTruckOwnerBankName());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("owner_pan_adhar", new DataPart("pan_adhar.jpg", getFileDataFromDrawable(getBaseContext(), imagePan.getDrawable()), "image/jpeg"));
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }
}



/*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                try {
                    previewCapturedImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GALLARY_REQUEST_CODE) {
                Uri selectedImage = data.getData();
                String filePath[] = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap bitmapImage = BitmapFactory.decodeFile(picturePath);
                captureImageuri = Uri.parse(picturePath);
                Bitmap newBitmap = BaseActivity.decodeSampledBitmapFromResource(captureImageuri, IMG_SHRINK_WIDTH, IMG_SHRINK_HEIGHT);
                try {
                    newBitmap = checkImageRotation(newBitmap, captureImageuri);
                } catch (IOException e) {

                    e.printStackTrace();
                }
                licenceImageUri = captureImageuri;
                imagePan.setImageBitmap(newBitmap);
            }
        }
    }

    private void previewCapturedImage() throws IOException {
        Bitmap newBitmap = BaseActivity.decodeSampledBitmapFromResource(fileUri, IMG_SHRINK_WIDTH, IMG_SHRINK_HEIGHT);
        newBitmap = checkImageRotation(newBitmap, fileUri);
        saveBitmap(newBitmap);
        licenceImageUri = fileUri;
        imagePan.setImageBitmap(newBitmap);
    }

    private Bitmap checkImageRotation(Bitmap bitmap, Uri uri) throws IOException {
        ExifInterface ei = new ExifInterface(uri.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateImage(bitmap, 180);
                break;
        }
        return bitmap;
    }

    public void saveBitmap(Bitmap newBitmap) {
        FileOutputStream out = null;
        File file = new File(fileUri.getPath());
        if (file.exists()) {
            file.delete();
        }
        try {
            out = new FileOutputStream(file);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return retVal;
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG" + timeStamp + "_";
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File mediaFile = null;
        try {
            mediaFile = File.createTempFile(imageFileName, *//* prefix *//*
                    ".jpg", *//* suffix *//*
                    mediaStorageDir *//* directory *//*
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mediaFile != null)
            mediaFile.delete();
        return mediaFile;
    }
*/