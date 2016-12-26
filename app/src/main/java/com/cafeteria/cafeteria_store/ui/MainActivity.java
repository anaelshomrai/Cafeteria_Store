package com.cafeteria.cafeteria_store.ui;

        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.support.design.widget.TabLayout;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentStatePagerAdapter;
        import android.support.v4.content.PermissionChecker;
        import android.support.v4.view.ViewPager;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.widget.Toast;

        import com.cafeteria.cafeteria_store.R;

        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int CAMREA_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkCameraPermission();

        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        // Adding to the adapter the three fragments and their titlesAnimation
        adapter.addFragment(new InventoryFragment(), getResources().getString(R.string.inventory_tab_title));
        adapter.addFragment(new OrdersFragment(), getResources().getString(R.string.orders_tab_title));
        adapter.addFragment(new OrdersReadyFragment(), getResources().getString(R.string.orders_ready_tab_title));
        viewPager.setAdapter(adapter);
        // if the activity opens after intent from qr code scanning then the right tab is the ready orders on
        // if not its the orders tab
        int tab = getIntent().getIntExtra("tab",1);

        viewPager.setCurrentItem(tab);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //activity_menu.setElevation(0);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void checkCameraPermission(){
        int cameraPermission;

        if(Build.VERSION.SDK_INT < 23 ){
            cameraPermission = PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (cameraPermission == PermissionChecker.PERMISSION_GRANTED) {

            } else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,
                },CAMREA_PERMISSION_REQUEST);
            }
        } else{ //api 23 and above
            cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED ) {
                // We don't have permission so prompt the user
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        CAMREA_PERMISSION_REQUEST
                );
            } else {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMREA_PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    Toast.makeText(this, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Pager is the element that manages and displays the tabs
     */
    static class CustomPagerAdapter extends FragmentStatePagerAdapter {

        /**
         * List of the fragments in this pager
         */
        private final List<Fragment> fragmentList = new ArrayList<>();

        /**
         * List of titlesAnimation to display at the head of each fragment tab
         */
        private final List<String> fragmentTitleList = new ArrayList<>();


        public CustomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Returns the fragment in a given position
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        /**
         * Returns the amount of tabs in the pager
         * @return
         */
        @Override
        public int getCount() {
            return fragmentList.size();
        }

        /**
         * Returns the page title of specific tab (by position)
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        /**
         * Adds fragment to the pager
         * @param fragment
         * @param title
         */
        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }
}
