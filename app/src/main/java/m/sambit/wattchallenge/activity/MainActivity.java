package m.sambit.wattchallenge.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import m.sambit.wattchallenge.R;
import m.sambit.wattchallenge.fragment.LoginFragment;
import m.sambit.wattchallenge.utils.SessionManagement;
import m.sambit.wattchallenge.utils.Utility;

/**
 * @author Sambit Mallick
 * {@link MainActivity} contains two fragments {@link LoginFragment} and {@link m.sambit.wattchallenge.fragment.RegistrationFragment}
 *
 * {@link SessionManagement} is used to store information localy till the deletation of app.
 * If user has already logged in then {@link ShopActivity} will be shown directly
 * so no need to login again.
 */
public class MainActivity extends AppCompatActivity {

    private SessionManagement sessionManagement;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.parent_main);

        final View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something here
            }
        };

        sessionManagement = new SessionManagement(MainActivity.this);
        if (sessionManagement.isLoggedIn()) {
            if (!Utility.checkInternet(MainActivity.this)) {
                // Define the click listener as a member
                // Pass in the click listener when displaying the Snackbar
                Snackbar.make(coordinatorLayout, "Connect to internet!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", myOnClickListener)
                        .show(); // Don’t forget to show!
            } else {
                Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_LONG).show();
                Intent shopActivity = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(shopActivity);
                finish();
            }
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment()).addToBackStack(null)
                    .commit();
        }
    }
}
