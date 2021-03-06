package com.example.kirin.cs2340.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kirin.cs2340.Model.ActivityLog;
import com.example.kirin.cs2340.Model.CurrentUser;
import com.example.kirin.cs2340.Model.GeneralUser;
import com.example.kirin.cs2340.Model.LogType;
import com.example.kirin.cs2340.Model.Manager;
import com.example.kirin.cs2340.Model.WaterQualityReport;
import com.example.kirin.cs2340.Model.WaterSourceReport;
import com.example.kirin.cs2340.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Kirin on 3/11/2017.
 * Serves as adapter to give each report a view
 */

public class WQRReportAdapter extends RecyclerView.Adapter<WQRReportAdapter.WQRViewHolder> {
    private final List<WaterQualityReport> reports;
    private ViewQualityActivity context;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    /**
     * List item holder that holds report view fields
     */
    public static class WQRViewHolder extends RecyclerView.ViewHolder {
        public final View v;

        /**
         * Constructor for ViewHolder
         * @param v the view to be displayed
         */
        public WQRViewHolder(View v) {
            super(v);
            this.v = v;
        }
    }

    /**
     * Constructor for report adapter
     * @param dataSet the content for the list view
     */
    public WQRReportAdapter(List<WaterQualityReport> dataSet) {
        this.reports = dataSet;
    }

    /**
     * Gets number of items in list
     * @return number of items in list
     */
    public int getItemCount() {
        return reports.size();
    }

    /**
     * Creates list item view
     * @param parent the parent of the list item
     * @param viewType the type of view
     * @return new list item view
     */
    public WQRReportAdapter.WQRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wqr_report_card, parent, false);
        return new WQRReportAdapter.WQRViewHolder(itemView);
    }

    /**
     * populates the fields in the list item view
     * @param viewHolder the list item view
     * @param i index of item in overall list that needs populated fields
     */
    public void onBindViewHolder(WQRReportAdapter.WQRViewHolder viewHolder, int i) {
        final WaterQualityReport wqr = reports.get(i);
        viewHolder.v.setLongClickable(true);
        final ViewQualityActivity context = this.context;
        final int pos = i;
        if (CurrentUser.getInstance().getCurrentUser() instanceof Manager) {
            viewHolder.v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Manager Action");
                    builder.setMessage("Delete this report?");
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    builder.setPositiveButton("Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityLog log = new ActivityLog();
                                    log.setId1(CurrentUser.getInstance().getCurrentUser().getId());
                                    log.setType(LogType.REPORT_DELETE);
                                    log.setId2(wqr.getReportId());
                                    database.getRoot().child("Security Log").push().setValue(log);
                                    Query query = database.child("Reports").child("QR").orderByValue();
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot child: dataSnapshot.getChildren()) {
                                                WaterQualityReport temp = child.getValue(WaterQualityReport.class);
                                                if(temp.getDate().getTime() == wqr.getDate().getTime()) {
                                                    child.getRef().removeValue();
                                                    reports.remove(pos);
                                                    notifyItemRemoved(pos);

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                    dialog.cancel();
                                }
                            });
                    builder.create().show();
                    return true;
                }
            });
        }
        TextView tv = (TextView) viewHolder.v.findViewById(R.id.report_id);
        tv.setText(String.format(Integer.toString(wqr.getReportId())));
        tv = (TextView) viewHolder.v.findViewById(R.id.submitter);
        tv.setText(wqr.getName());
        tv = (TextView) viewHolder.v.findViewById(R.id.location);
        tv.setText("(" + wqr.getLat() + ", " + wqr.getLng() + ")");
        tv = (TextView) viewHolder.v.findViewById(R.id.condition);
        tv.setText(wqr.getCondition().toString());
        tv = (TextView) viewHolder.v.findViewById(R.id.virus);
        tv.setText(String.format(Integer.toString(wqr.getVirusPPM())));
        tv = (TextView) viewHolder.v.findViewById(R.id.contaminant);
        tv.setText(String.format(Integer.toString(wqr.getContaminantPPM())));
        tv = (TextView) viewHolder.v.findViewById(R.id.date);
        tv.setText(wqr.getDate().toString());
    }
    public void setContext(ViewQualityActivity activity) {
        this.context = activity;
    }
}
