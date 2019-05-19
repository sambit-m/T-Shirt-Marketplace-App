package m.sambit.wattchallenge.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.button.MaterialButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import m.sambit.wattchallenge.R;

public class Utility {

    public static Spinner SetSpinner(Context context, ArrayList<String> SpinnerList, Spinner spinner) {
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> SpinnerArrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, SpinnerList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        SpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(SpinnerArrayAdapter);
        return spinner;
    }

    public static boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static boolean isValidPassword(String email){

        return Pattern.compile("^\\S{6,10}$").matcher(email).matches();
    }

    public static void showDialog(String message, Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog alert = builder.create();
        View view = alert.getLayoutInflater().inflate(R.layout.show_dialog, null);
        TextView title =  view.findViewById(R.id.title);
        MaterialButton ok =  view.findViewById(R.id.Ok);
        title.setText(message);
        alert.setCustomTitle(view);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    public static boolean checkInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
