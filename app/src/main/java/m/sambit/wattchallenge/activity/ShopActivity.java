package m.sambit.wattchallenge.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import m.sambit.wattchallenge.R;
import m.sambit.wattchallenge.adapter.CarouselPagerAdapter;

/**
 * {@link ShopActivity} contains {@link m.sambit.wattchallenge.fragment.ItemFragment} fragment which shows shits
 * in custom carousal {@link m.sambit.wattchallenge.customlayout.CarouselLinearLayout}
 */
public class ShopActivity extends AppCompatActivity {

    public final static int LOOPS = 1000;
    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    public static int count = 5; //ViewPager items size
    public static int FIRST_PAGE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        pager = findViewById(R.id.myviewpager);

        //set page margin between pages for viewpager
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pageMargin = ((metrics.widthPixels / 4) * 2);
        pager.setPageMargin(-pageMargin);

        adapter = new CarouselPagerAdapter(this, getSupportFragmentManager());
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pager.addOnPageChangeListener(adapter);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);
        pager.setOffscreenPageLimit(3);
    }
}
