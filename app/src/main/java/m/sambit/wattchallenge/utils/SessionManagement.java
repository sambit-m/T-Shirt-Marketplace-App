package m.sambit.wattchallenge.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.firestore.auth.User;

import java.util.HashMap;

import m.sambit.wattchallenge.activity.MainActivity;

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "WATTCHALLENGE";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String USER_EMAIL = "user_email";
    public static final String USER_ADDRESS="user_address";
    public static final String USER_PHONE="user_phone";

    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String email, String address, String phone) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(USER_EMAIL, email);

        // Storing address in pref
        editor.putString(USER_ADDRESS, address);

        // Storing phone in pref
        editor.putString(USER_PHONE, phone);

        // commit changes
        editor.apply();
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(USER_EMAIL, pref.getString(USER_EMAIL, null));
        user.put(USER_ADDRESS, pref.getString(USER_ADDRESS, null));
        user.put(USER_PHONE, pref.getString(USER_PHONE, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.apply();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
