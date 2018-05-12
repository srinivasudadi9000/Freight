package com.waysideutilities.waysidetruckfreights.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.waysideutilities.waysidetruckfreights.PojoClasses.Request;
import com.waysideutilities.waysidetruckfreights.Profile.FinalPriceActivity;
import com.waysideutilities.waysidetruckfreights.R;
import com.waysideutilities.waysidetruckfreights.helper.HTTPhelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.waysideutilities.waysidetruckfreights.helper.Constants.MY_PREFS_NAME;

/**
 * Created by Archana on 1/4/2017.
 */
public class ReceivedRequestsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Request> listRequests;
    private ProgressDialog progressDialog;
    private String userType;

    public ReceivedRequestsAdapter(Context context, ArrayList<Request> listRequests) {
        this.context = context;
        this.listRequests = listRequests;
    }

    @Override
    public int getCount() {
        return listRequests.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) context;
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewHolder holder = null;
        userType = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERTYPE", null);

        final Request request = this.listRequests.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_received_requests_adapter, null);

            //send_acceped_request_to_truck_owner
            holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
            holder.btnAccept = (Button) convertView.findViewById(R.id.btnAccept);
            holder.btnAccept.setTag(position);
            holder.btnReject = (Button) convertView.findViewById(R.id.btnReject);
            holder.btnReject.setTag(position);
            holder.relReject = (RelativeLayout) convertView.findViewById(R.id.relReject);
            holder.relReject.setTag(position);
            holder.relAccept = (RelativeLayout) convertView.findViewById(R.id.relAccept);
            holder.relAccept.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtMessage.setText(request.getReceived_message());
        if (userType.equals("Cargo Provider")) {
            if (request.getRequest_status().equals("0")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#32CD32"));
                holder.btnReject.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.accept);
                holder.btnReject.setText(R.string.reject);
            } else if (request.getRequest_status().equals("1")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#FF7F50"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.waiting_confirmation);
                holder.btnReject.setVisibility(View.GONE);
                holder.relReject.setVisibility(View.GONE);
            } else if (request.getRequest_status().equals("2")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.rejected);
                holder.relReject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            } else if (request.getRequest_status().equals("3")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#1742ed"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.payment);
                holder.relReject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            } else if (request.getRequest_status().equals("4")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.declined);
                holder.relReject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            } else if (request.getRequest_status().equals("5")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#32CD32"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.booked);
                holder.relReject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            }
        } else {
            if (request.getRequest_status().equals("0")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#32CD32"));
                holder.btnReject.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.accept);
                holder.btnReject.setText(R.string.reject);
            } else if (request.getRequest_status().equals("1")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#FF7F50"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.waiting_payment);
                holder.relReject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            } else if (request.getRequest_status().equals("2")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.rejected);
                holder.relReject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            } else if (request.getRequest_status().equals("3")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#32CD32"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.booked);
                holder.relReject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            }
        }
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int sPosotion = (Integer) view.getTag();
                // Request sRequest = listRequests.get(sPosotion);
                Request sRequest = listRequests.get(position);
                if (userType.equals("Cargo Provider")) {
                    if (sRequest.getRequest_status().equals("0")) {
                        //Accept set flag 1
                        sRequest.setRequest_status("1");
                        sRequest.setReceived_message("Your request of post truck id " + sRequest.getPosttruck_id() + " for load id " + sRequest.getLoad_id() + " has accepted. Please check your sent requests list.");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Update_status_request(sRequest).execute();
                    } else if (sRequest.getRequest_status().equals("3")) {
                        //Payment set flag 5
                        //after payment set book status 1 book_by_id and trip status

                        Bundle bundle = new Bundle();
                        bundle.putString("LOAD_ID", sRequest.getLoad_id());
                        bundle.putString("POST_TRUCK_ID", sRequest.getPosttruck_id());
                        bundle.putString("TRUCK_ID", sRequest.getRtruck_id());
                        bundle.putString("RECIVER_ID", sRequest.getReceiver_id());
                        bundle.putString("RECIVER_NUMBER", sRequest.getSender_number());
                        bundle.putString("SENDER_NUMBER", sRequest.getReceiver_number());

                        bundle.putString("REQUEST_STATUS", "5");
                        Intent intent = new Intent(context, FinalPriceActivity.class);
                        intent.putExtra("BUNDLE", bundle);
                        context.startActivity(intent);
                        ((Activity) context).finish();

                        /*sRequest.setRequest_status("5");
                        sRequest.setReceived_message("The accepted request of load id " + sRequest.getLoad_id()+" for your truck with post truck id "+ sRequest.getPosttruck_id() +" has been booked.");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Update_status_request(sRequest).execute();*/
                    }
                } else {
                    if (sRequest.getRequest_status().equals("0")) {
                        //Accept set flag 1
                        sRequest.setRequest_status("1");
                        sRequest.setReceived_message("Your request of load id " + sRequest.getLoad_id() + " for post truck id " + sRequest.getPosttruck_id() + " has accepted. Please check your sent requests list.");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Update_status_request(sRequest).execute();
                    }
                }
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*int sPosotion = (Integer) view.getTag();
                Request sRequest = listRequests.get(sPosotion);*/
                Request sRequest = listRequests.get(position);
                if (userType.equals("Cargo Provider")) {
                    if (sRequest.getRequest_status().equals("0")) {
                        //Reject set flag 2
                        sRequest.setRequest_status("2");
                        sRequest.setReceived_message("Your request of load id " + sRequest.getLoad_id() + " for post truck id " + sRequest.getPosttruck_id() + " has rejected. Please check your sent requests list.");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Update_status_request(sRequest).execute();
                    }
                } else {
                    if (sRequest.getRequest_status().equals("0")) {
                        //Reject set flag 2
                        sRequest.setRequest_status("2");
                        sRequest.setReceived_message("Your request of post truck id " + sRequest.getPosttruck_id() + " for load id " + sRequest.getLoad_id() + " has rejected. Please check your sent requests list.");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Update_status_request(sRequest).execute();
                    }
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        Button btnAccept, btnReject;
        TextView txtMessage;
        RelativeLayout relAccept, relReject;
    }

    private class Update_status_request extends AsyncTask<Void, Void, InputStream> {
        private Request request;

        public Update_status_request(Request sRequest) {
            this.request = sRequest;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            HTTPhelper helper = new HTTPhelper();
            InputStream inputStream = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("sender_id", context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERID", null));
                jsonObject.accumulate("receiver_id", this.request.getSender_id());
                jsonObject.accumulate("load_id", this.request.getLoad_id());
                jsonObject.accumulate("posttruck_id", this.request.getPosttruck_id());
                jsonObject.accumulate("truck_id", this.request.getRtruck_id());
                jsonObject.accumulate("request_status", this.request.getRequest_status());

                byte[] encoded = jsonObject.toString().getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encoded, Base64.DEFAULT);
                encodedString = encodedString.replace("\n", "");
                String newString = String.format("requestString=%s", encodedString);
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/update_request_status.php", "GET", newString);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            progressDialog.dismiss();
            if (inputStream != null) {
                StringBuilder builder = new StringBuilder();
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(streamReader);
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String stringResult = builder.toString();
                Log.e("Update_status received", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        // ((Activity) context).finish();
                        String receiver_mb = this.request.getSender_number();
                        String received_message = this.request.getReceived_message();
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new RequestForTruckMessage(context, receiver_mb, received_message).execute();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }

        public class RequestForTruckMessage extends AsyncTask<Void, Void, String> {
            private Context context;
            private String receiver_mb;
            private String received_message;

            public RequestForTruckMessage(Context context, String receiver_mb, String received_message) {
                this.context = context;
                this.receiver_mb = receiver_mb;
                this.received_message = received_message;
            }


            @Override
            protected String doInBackground(Void... voids) {
                HTTPhelper helper = new HTTPhelper();
                String inputStream = null;

                if (this.receiver_mb != null) {
                    inputStream = helper.makeHttpPostRequest("http://www.waysideutilities.com/api/sms_gate.php", this.receiver_mb, this.received_message);
                }
                return inputStream;
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                progressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("status");
                        Log.e("request for truck", response);
                        if (success.equals("success")) {
                            //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        } else {
                            Toast.makeText(this.context, R.string.err_contact_no, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this.context, R.string.networkError, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
