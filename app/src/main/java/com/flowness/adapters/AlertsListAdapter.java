package com.flowness.adapters;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowness.R;
import com.flowness.activities.AlertsConfigActivity;
import com.flowness.model.Alert;
import com.flowness.volley.ApproveAlertRequest;
import com.flowness.volley.BasicRequest;
import com.flowness.volley.UpdateAlertsConfigRequest;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

public class AlertsListAdapter extends ArrayAdapter<Alert> implements View.OnClickListener {

    private List<Alert> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTitle;
        TextView txtDescription;
        ImageView iconApproved;
    }

    public AlertsListAdapter(List<Alert> data, Context context) {
        super(context, R.layout.alert_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        final Alert alertModel = (Alert) getItem(position);

        switch (v.getId()) {
            case R.id.alertApproval:
                if (!alertModel.isAlertApproved()) {
                    final Snackbar snackBar = Snackbar.make(v, "Mark Approved?", Snackbar.LENGTH_LONG);
                    snackBar.setAction("YES", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String postBody = getPostBody();
                            new ApproveAlertRequest(postBody,
                                    new BasicRequest.ResponseListener() {
                                        @Override
                                        public void onResponse(BasicRequest.Response response) {
                                            if (response.isSuccess()) {
                                                try {
                                                    JSONObject responseJson = new JSONObject(response.data);
                                                    alertModel.setAlertApproved();
                                                    notifyDataSetChanged();
                                                    Log.d("Alerts approve", String.format("Alert was approved"));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).execute(mContext);
                            snackBar.dismiss();
                        }

                        private String getPostBody() {
                            JsonObject requestBody = new JsonObject();
                            requestBody.addProperty("notificationId", alertModel.getAlertId());
                            return requestBody.toString();
                        }


                    });
                    snackBar.show();
                }
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Alert alertItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.alert_item, parent, false);
            viewHolder.txtTitle = convertView.findViewById(R.id.alertTitle);
            viewHolder.txtDescription = convertView.findViewById(R.id.alertDate);
            viewHolder.iconApproved = convertView.findViewById(R.id.alertApproval);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtTitle.setText(getAlertTitle(alertItem.getAlertType()));
        viewHolder.txtDescription.setText(alertItem.getAlertDate().toString());
        viewHolder.iconApproved.setImageResource(alertItem.isAlertApproved() ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);
        viewHolder.iconApproved.setOnClickListener(this);
        viewHolder.iconApproved.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    private String getAlertTitle(int alertType) {
        switch (alertType) {
            case 1:
                return "0-Flow Hours Alert";
            default:
                return "Genaral Alert";
        }
    }
}