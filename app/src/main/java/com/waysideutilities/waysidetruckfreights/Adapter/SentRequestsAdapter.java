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
public class SentRequestsAdapter extends BaseAdapter {

    public Context context;
    private ArrayList<Request> listRequests;
    private ProgressDialog progressDialog;
    private String userType;

    public SentRequestsAdapter(Context context, ArrayList<Request> listRequests) {
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
        final LayoutInflater inflater = activity.getLayoutInflater();
        ViewHolder holder = null;
        userType = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("USERTYPE", null);

        final Request request = this.listRequests.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_sent_requests_adapter, null);
            holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
            holder.btnAccept = (Button) convertView.findViewById(R.id.btnAccept);
            holder.btnAccept.setTag(position);
            holder.btnReject = (Button) convertView.findViewById(R.id.btnReject);
            holder.btnReject.setTag(position);
            holder.rel_sent_accept = (RelativeLayout) convertView.findViewById(R.id.rel_sent_accept);
            holder.rel_sent_accept.setTag(position);
            holder.rel_sent_reject = (RelativeLayout) convertView.findViewById(R.id.rel_sent_reject);
            holder.rel_sent_reject.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtMessage.setText(request.getSent_message());
        if (userType.equals("Cargo Provider")) {
            if (request.getRequest_status().equals("0")) {
                holder.btnReject.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.rel_sent_accept.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnReject.setText(R.string.cancel);
            } else if (request.getRequest_status().equals("1")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#1742ed"));
                holder.rel_sent_reject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.payment);
            } else if (request.getRequest_status().equals("2")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.rel_sent_reject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.rejected);
            } else if (request.getRequest_status().equals("3")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#32CD32"));
                holder.rel_sent_reject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.booked);
            }
        } else {
            if (request.getRequest_status().equals("0")) {
                holder.btnReject.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.rel_sent_accept.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnReject.setText(R.string.cancel);
            } else if (request.getRequest_status().equals("1")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#32CD32"));
                holder.btnReject.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.confirm);
                holder.btnReject.setText(R.string.decline);
            } else if (request.getRequest_status().equals("2")) {
                holder.btnReject.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.rel_sent_accept.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnReject.setText(R.string.rejected);
            } else if (request.getRequest_status().equals("3")) {
                holder.btnAccept.setBackgroundColor(Color.parseColor("#FF7F50"));
                holder.rel_sent_reject.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnAccept.setText(R.string.waiting_payment);
            } else if (request.getRequest_status().equals("4")) {
                holder.btnReject.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.rel_sent_accept.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnReject.setText(R.string.declined);
            } else if (request.getRequest_status().equals("5")) {
                holder.btnReject.setBackgroundColor(Color.parseColor("#32CD32"));
                holder.rel_sent_accept.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnReject.setText(R.string.booked);
            }
        }
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int sPosotion = (Integer) view.getTag();
                //Request sRequest = listRequests.get(sPosotion);//send_acceped_request_to_truck_owner
                Request sRequest = listRequests.get(position);
                if (userType.equals("Cargo Provider")) {
                    if (sRequest.getRequest_status().equals("1")) {
                        /*payment after payment set flag 3
                        after payment set book status 1 book_by_id and trip status*/
                        Bundle bundle = new Bundle();
                        bundle.putString("LOAD_ID", sRequest.getLoad_id());
                        bundle.putString("POST_TRUCK_ID", sRequest.getPosttruck_id());
                        bundle.putString("TRUCK_ID", sRequest.getRtruck_id());
                        bundle.putString("RECIVER_ID", sRequest.getReceiver_id());
                        bundle.putString("RECIVER_NUMBER", sRequest.getReceiver_number());
                        bundle.putString("SENDER_NUMBER", sRequest.getSender_number());
                        bundle.putString("REQUEST_STATUS", "3");
                        Intent intent = new Intent(context, FinalPriceActivity.class);
                        intent.putExtra("BUNDLE", bundle);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        /*Bundle bundle = new Bundle();
                        bundle.putString("LOAD_ID",sRequest.getLoad_id());
                        bundle.putString("TRUCK_ID",sRequest.getPosttruck_id());
                        bundle.putString("RTRUCK_ID",sRequest.getRtruck_id());
                        bundle.putString("RECIVER_ID",sRequest.getPosttruck_id());
                        bundle.putString("RECIVER_NUMBER",sRequest.sender_number);
                        Intent intent = new Intent(context, FinalPriceActivity.class);
                        intent.putExtra("BUNDLE",bundle);
                        context.startActivity(intent);*/
                        //sRequest.setRequest_status("3");
                        //sRequest.setReceived_message("The accepted request of load id " + sRequest.getLoad_id()+" for your truck with post truck id " + sRequest.getPosttruck_id() +" has been booked.");
                        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        //new Update_status_request(sRequest).execute();
                    }
                } else {
                    if (sRequest.getRequest_status().equals("1")) {
                        //confirm set flag 3
                        //payment after payment set flag 3
                        // after payment set book status 1 book_by_id and trip status
                        sRequest.setRequest_status("3");
                        sRequest.setReceived_message("The accepted request of truck with post truck id " + sRequest.getPosttruck_id() + " for your load id " + sRequest.getLoad_id() + " has confirmed.");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Update_status_request(sRequest).execute();
                        ((Activity) context).finish();
                    }
                }
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int sPosotion = (Integer) view.getTag();
                //Request sRequest = listRequests.get(sPosotion);
                Request sRequest = listRequests.get(position);
                if (userType.equals("Cargo Provider")) {
                    if (sRequest.getRequest_status().equals("0")) {
                        //delete record
                        sRequest.setReceived_message("The request from load id " + sRequest.getLoad_id() + " for your truck with post truck id " + sRequest.getPosttruck_id() + " has cancelled.");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Delete_status_request(sRequest).execute();
                    }
                } else {
                    if (sRequest.getRequest_status().equals("0")) {
                        //delete record
                        sRequest.setReceived_message("The request from truck with post truck id " + sRequest.getPosttruck_id() + " for your load id  " + sRequest.getLoad_id() + " has cancelled.");
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().detectAll().build());
                        new Delete_status_request(sRequest).execute();
                    } else if (sRequest.getRequest_status().equals("1")) {
                        //decline set flag 4
                        sRequest.setRequest_status("4");
                        sRequest.setReceived_message("The accepted request of truck with post truck id " + sRequest.getPosttruck_id() + " for your load id " + sRequest.getLoad_id() + " has declined.");
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
        RelativeLayout rel_sent_reject, rel_sent_accept;
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
                Log.e("Update_status sent", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        ((Activity) context).finish();
                        String receiver_mb = this.request.getReceiver_number();
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
    }

    private class Delete_status_request extends AsyncTask<Void, Void, InputStream> {
        private Request request;

        public Delete_status_request(Request sRequest) {
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
                inputStream = helper.makeHttpRequest("http://www.waysideutilities.com/api/delete_request.php", "GET", newString);

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
                Log.e("Update_status sent", stringResult);
                try {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        ((Activity) context).finish();
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        String receiver_mb = this.request.getReceiver_number();
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
