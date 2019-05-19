package m.sambit.wattchallenge.fragment;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import m.sambit.wattchallenge.R;
import m.sambit.wattchallenge.activity.MainActivity;
import m.sambit.wattchallenge.activity.ShopActivity;
import m.sambit.wattchallenge.customlayout.CarouselLinearLayout;
import m.sambit.wattchallenge.utils.GlideApp;
import m.sambit.wattchallenge.utils.SessionManagement;
import m.sambit.wattchallenge.utils.Utility;

/**
 * To show shirt in carousal view using {@link m.sambit.wattchallenge.adapter.CarouselPagerAdapter}
 */
public class ItemFragment extends Fragment {

    private static final String POSITON = "position";
    private static final String SCALE = "scale";
    private static final String HIDE_SIZES = "hide_sizes";
    private static final String DRAWABLE_RESOURE = "resource";
    private static final String XS = "xs";
    private static final String S = "s";
    private static final String M = "m";
    private static final String L = "l";
    private static final String XL = "xl";
    private FirebaseFirestore db;
    private String img;
    private TextView availableSizeView;
    private MaterialButton chooseThis;
    private int screenWidth;
    private int screenHeight;
    private ArrayList<String> availableSizes;
    private HashMap<String, Integer> availableQuantities;
    private Spinner sizeSpinner;
    private SessionManagement sessionManagement;

    public static Fragment newInstance(ShopActivity context, int pos, float scale) {
        Bundle b = new Bundle();
        b.putInt(POSITON, pos);
        b.putFloat(SCALE, scale);

        return Fragment.instantiate(context, ItemFragment.class.getName(), b);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidthAndHeight();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        final int position = this.getArguments().getInt(POSITON);
        float scale = this.getArguments().getFloat(SCALE);

        if (position == 0)
            img = "shirt";
        else
            img = "shirt_" + position;
        db = FirebaseFirestore.getInstance();
        sessionManagement = new SessionManagement(getContext());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("image/" + img + ".png");
//        StorageReference gsReference = storage.getReferenceFromUrl("gs://watt-sambit.appspot.com/image/shirt.png");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth / 2, screenHeight / 2);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_image, container, false);

        TextView textView = linearLayout.findViewById(R.id.text);
        CarouselLinearLayout root = linearLayout.findViewById(R.id.root_container);
        ImageView imageView = linearLayout.findViewById(R.id.pagerImg);
        availableSizeView = linearLayout.findViewById(R.id.sizes);
        chooseThis = linearLayout.findViewById(R.id.choose_button);
        textView.setText(img);
        imageView.setLayoutParams(layoutParams);
        GlideApp.with(this)
                .load(pathReference)
                .into(imageView);

        chooseThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderDialog();
            }
        });

        getAvailableSizes();

        root.setScaleBoth(scale);

        return linearLayout;
    }

    /**
     * Get device screen width and height
     */
    private void getWidthAndHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    /**
     * To get available size of the particular shirt
     */
    private void getAvailableSizes() {
        DocumentReference user = db.collection("shirt").document(img);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    String sizes = "";
                    availableSizes = new ArrayList<>();
                    availableSizes.add("Choose Size");
                    availableQuantities = new HashMap<>();
                    if (Integer.valueOf(doc.get(XS).toString()) > 0) {
                        sizes += "XS ";
                        availableSizes.add("XS");
                        availableQuantities.put("XS", Integer.valueOf(doc.get(XS).toString()));
                    }
                    if (Integer.valueOf(doc.get(S).toString()) > 0) {
                        sizes += "S ";
                        availableSizes.add("S");
                        availableQuantities.put("S", Integer.valueOf(doc.get(S).toString()));
                    }
                    if (Integer.valueOf(doc.get(M).toString()) > 0) {
                        sizes += "M ";
                        availableSizes.add("M");
                        availableQuantities.put("M", Integer.valueOf(doc.get(M).toString()));
                    }
                    if (Integer.valueOf(doc.get(L).toString()) > 0) {
                        sizes += "L ";
                        availableSizes.add("L");
                        availableQuantities.put("L", Integer.valueOf(doc.get(L).toString()));
                    }
                    if (Integer.valueOf(doc.get(XL).toString()) > 0) {
                        sizes += "XL ";
                        availableSizes.add("XL");
                        availableQuantities.put("XL", Integer.valueOf(doc.get(XL).toString()));
                    }
                    if (sizes.equals("")) {
                        chooseThis.setVisibility(View.GONE);
                        sizes = "Not Available";
                    } else {
                        chooseThis.setVisibility(View.VISIBLE);
                    }
                    availableSizeView.setText(sizes);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utility.showDialog("Error!", getContext());
                    }
                });
    }

    /**
     * Shows dialog to let the user the his/her size from the available size
     */
    private void showOrderDialog() {

        AlertDialog.Builder orderDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.place_order_dialog, null);
        orderDialog.setView(dialogView);
        sizeSpinner = dialogView.findViewById(R.id.size_spinner);
        final MaterialButton placeOrder = dialogView.findViewById(R.id.order_button);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, availableSizes);
        sizeSpinner.setAdapter(arrayAdapter);

        sizeSpinner = Utility.SetSpinner(getActivity(), availableSizes, sizeSpinner);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sizeSpinner.getSelectedItemPosition() > 0)
                    addNewOrder();
                else
                    Toast.makeText(getContext(), "Choose size", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alert = orderDialog.create();
        alert.show();
    }

    /**
     * Updating the quantity of the shirt in Firestore and placing order successfully
     * @param size
     * @param quantity
     */
    private void updateData(String size, int quantity) {
        DocumentReference contact = db.collection("shirt").document(img);
        contact.update(size.toLowerCase(), quantity - 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent shopActivity = new Intent(getContext(), ShopActivity.class);
                        shopActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(shopActivity);
                    }
                });
    }

    /**
     * For storing order information
     */
    private void addNewOrder(){
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading");
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        Map< String, Object > newOrder = new HashMap < > ();
        newOrder.put("email", sessionManagement.getUserDetails().get(SessionManagement.USER_EMAIL));
        newOrder.put("address", sessionManagement.getUserDetails().get(SessionManagement.USER_ADDRESS));
        newOrder.put("phone", sessionManagement.getUserDetails().get(SessionManagement.USER_PHONE));
        newOrder.put("shirt_item", img);
        newOrder.put("shirt_size", sizeSpinner.getSelectedItem().toString());
        db.collection("orders")
                .add(newOrder)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        pDialog.dismiss();
                        Toast.makeText(getContext(), "Order placed successfully", Toast.LENGTH_LONG).show();
                        updateData(sizeSpinner.getSelectedItem().toString(), availableQuantities.get(sizeSpinner.getSelectedItem().toString()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialog.dismiss();
                        Utility.showDialog("Error in placing your order", getContext());
                    }
                });
    }
}