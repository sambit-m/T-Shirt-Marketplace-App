package m.sambit.wattchallenge.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import m.sambit.wattchallenge.R;
import m.sambit.wattchallenge.activity.ShopActivity;
import m.sambit.wattchallenge.utils.SessionManagement;
import m.sambit.wattchallenge.utils.Utility;

/**
 * {@link LoginFragment} is used to login to using Firebase
 */
public class LoginFragment extends Fragment {

    private TextInputEditText emailInput, passwordInput;
    private TextInputLayout emailView, passwordView;
    private MaterialButton newUserRegistration, forgotPassword, loginButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String email, password;
    private FirebaseFirestore db;
    private SessionManagement sessionManagement;
    private CoordinatorLayout coordinatorLayout;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        sessionManagement = new SessionManagement(getContext());
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        emailView = view.findViewById(R.id.email_view);
        passwordView = view.findViewById(R.id.password_view);
        newUserRegistration = view.findViewById(R.id.new_user_button);
        forgotPassword = view.findViewById(R.id.forgot_password);
        loginButton = view.findViewById(R.id.login_button);
        coordinatorLayout = view.findViewById(R.id.layout);

        newUserRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationFragment registrationFragment = new RegistrationFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, registrationFragment);
                fragmentTransaction.commit();
            }
        });

        final View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something here
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.checkInternet(getContext())) {
                    // Define the click listener as a member
                    // Pass in the click listener when displaying the Snackbar
                    Snackbar.make(coordinatorLayout, "Connect to internet!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Dismiss", myOnClickListener)
                            .show(); // Don’t forget to show!
                } else {
                    if (isValid()) {
                        email = emailInput.getText().toString().trim();
                        password = passwordInput.getText().toString();
                        fireBaseLogin();
                    }
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.checkInternet(getContext())) {
                    // Define the click listener as a member
                    // Pass in the click listener when displaying the Snackbar
                    Snackbar.make(coordinatorLayout, "Connect to internet!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Dismiss", myOnClickListener)
                            .show(); // Don’t forget to show!
                } else
                    forgotPasswordDialog();
            }
        });
        return view;
    }

    /**
     * For field validation
     * @return true or false
     */
    private boolean isValid() {
        if (emailInput.getText() == null || emailInput.getText().toString().trim().length() == 0) {
            emailView.setError("Enter email");
            return false;
        }

        if (passwordInput.getText() == null || passwordInput.getText().toString().trim().length() == 0) {
            passwordView.setError("Enter password");
            return false;
        }

        return true;
    }

    /**
     * Firebase login mechanism
     */
    private void fireBaseLogin() {
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading");
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            fireBaseGetUserDetails();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            try {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthInvalidUserException invalidEmail) {
                                emailView.setHelperText("Invalid email");
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                passwordView.setError("Wrong password");
                            } catch (Exception e) {
                                emailView.setHelperText("Error");
                            }
                        }
                    }
                });
    }

    /**
     * For getting user info from Firestore
     */
    private void fireBaseGetUserDetails() {
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading");
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        DocumentReference user = db.collection("user").document(email);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    pDialog.dismiss();
                    DocumentSnapshot doc = task.getResult();

                    //Storing email, address and phone locally using shared pref
                    sessionManagement.createLoginSession(email, doc.get("address").toString(), doc.get("phone").toString());
                    Intent shopActivity = new Intent(getContext(), ShopActivity.class);
                    shopActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(shopActivity);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utility.showDialog("Error!", getContext());
                        pDialog.dismiss();
                    }
                });
    }

    /**
     * Dialog to enter email if user forgets password
     * A forgot password mail is sent to user's email
     */
    private void forgotPasswordDialog() {
        AlertDialog.Builder verifyPasswordBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.forgot_password_dialog, null);

        verifyPasswordBuilder.setPositiveButton("ok", null);
        verifyPasswordBuilder.setNegativeButton("cancel", null);
        final TextInputEditText input = viewInflated.findViewById(R.id.input);
        final TextInputLayout inputView = viewInflated.findViewById(R.id.email_view);
        verifyPasswordBuilder.setView(viewInflated);

        verifyPasswordBuilder.setMessage("Enter your email to proceed.");
        verifyPasswordBuilder.setCancelable(false);

        final AlertDialog alert = verifyPasswordBuilder.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (input.getText() == null || input.getText().toString().trim().length() == 0) {
                            inputView.setError("Enter your email.");
                        } else {
                            final String inputEmail = input.getText().toString();
                            FirebaseAuth.getInstance().sendPasswordResetEmail(inputEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Check your mail.", Toast.LENGTH_LONG).show();
                                                alert.dismiss();
                                            } else {
                                                inputView.setError("Enter valid email");
                                            }
                                        }
                                    });
                        }
                    }
                });

                Button c = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
                c.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
            }
        });
        alert.show();
    }
}
