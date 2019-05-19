package m.sambit.wattchallenge.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import m.sambit.wattchallenge.R;
import m.sambit.wattchallenge.activity.MainActivity;
import m.sambit.wattchallenge.utils.Utility;

/**
 * {@link RegistrationFragment} is used to register user using Firebase Auth and Firestore to store address, email and phone
 */
public class RegistrationFragment extends Fragment {

    private TextInputEditText emailInput, passwordInput, addressInput, phoneInput;
    private TextInputLayout emailView, passwordView, addressView, phoneView;
    private MaterialButton registrationButton, alreadyRegistered;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String email, password, address, phone;
    private FirebaseFirestore db;
    private CoordinatorLayout coordinatorLayout;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        emailInput = view.findViewById(R.id.new_email_input);
        passwordInput = view.findViewById(R.id.new_password_input);
        phoneInput = view.findViewById(R.id.new_phone_input);
        addressInput = view.findViewById(R.id.new_address_input);
        emailView = view.findViewById(R.id.new_email_view);
        passwordView = view.findViewById(R.id.new_password_view);
        phoneView = view.findViewById(R.id.new_phone_view);
        addressView = view.findViewById(R.id.new_address_view);
        registrationButton = view.findViewById(R.id.register_button);
        coordinatorLayout = view.findViewById(R.id.layout);
        alreadyRegistered = view.findViewById(R.id.already_registered_button);


        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, loginFragment);
                fragmentTransaction.commit();
            }
        });
        final View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something here
            }
        };

        registrationButton.setOnClickListener(new View.OnClickListener() {
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
                        address = addressInput.getText().toString().trim();
                        phone = phoneInput.getText().toString().trim();

                        fireBaseWorkForRegistrations();
                    }
                }
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

        if (!Utility.isValidEmailId(emailInput.getText().toString().trim())) {
            emailView.setError("Enter correct email");
            return false;
        }

        if (passwordInput.getText() == null || passwordInput.getText().toString().trim().length() == 0) {
            passwordView.setError("Enter password");
            return false;
        }

        if (!Utility.isValidPassword(passwordInput.getText().toString().trim())) {
            emailView.setError("Password must contain six characters");
            return false;
        }

        if (addressInput.getText() == null || addressInput.getText().toString().trim().length() == 0) {
            addressView.setError("Enter address");
            return false;
        }

        if (phoneInput.getText() == null || phoneInput.getText().toString().trim().length() == 0) {
            phoneView.setError("Enter phone");
            return false;
        }

        if (phoneInput.getText().toString().trim().length() != 10) {
            phoneView.setError("Enter correct phone");
            return false;
        }

        return true;
    }


    /**
     * For creating new user in Firebase auth
     */
    private void fireBaseWorkForRegistrations() {
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading");
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addNewUser();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            try {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthWeakPasswordException weakPassword) {
                                passwordView.setHelperText("Your password is weak. Try making it strong.");
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                emailView.setHelperText("Invalid Email!");
                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                emailView.setError("Email already exists");
                            } catch (Exception e) {
                                emailView.setError("Registration Error");
                            }
                        }
                    }
                });
    }

    /**
     * For storing address, email and phone of the user in Firestore
     */
    private void addNewUser() {
        Map<String, Object> newContact = new HashMap<>();
        newContact.put("email", email);
        newContact.put("address", address);
        newContact.put("phone", phone);

        db.collection("user").document(email).set(newContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "User Registered",
                                Toast.LENGTH_SHORT).show();
                        LoginFragment loginFragment = new LoginFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, loginFragment);
                        fragmentTransaction.commit();
                    }
                });
    }
}
