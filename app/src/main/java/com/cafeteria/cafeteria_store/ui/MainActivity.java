package com.cafeteria.cafeteria_store.ui;

        import android.Manifest;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.os.Build;
        import android.preference.PreferenceManager;
        import android.support.design.widget.TabLayout;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentStatePagerAdapter;
        import android.support.v4.content.ContextCompat;
        import android.support.v4.content.PermissionChecker;
        import android.support.v4.view.ViewPager;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.text.InputType;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.Switch;
        import android.widget.Toast;

        import com.cafeteria.cafeteria_store.R;
        import com.cafeteria.cafeteria_store.data.Cafeteria;
        import com.google.gson.Gson;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                showAlert();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layout = getLayoutInflater();
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorHeadlines));
        input.setLinkTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorHeadlines));
        input.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialogBuilder.setView(input);
        alertDialogBuilder
                .setTitle(getString(R.string.dialog_manager_password_title))
                .setMessage(getString(R.string.dialog_manager_password_message))
                .setPositiveButton(getResources().getString(R.string.dialog_positve),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String password = input.getText().toString();
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String cafeteriaJSON = sharedPreferences.getString("cafeteria","");
                                if (!cafeteriaJSON.equals("")){
                                    Cafeteria c = new Gson().fromJson(cafeteriaJSON,Cafeteria.class);
                                    if (c.getAdminPassword().equals(password)){
                                        exit();
                                    }else{
                                        Toast.makeText(MainActivity.this,getString(R.string.dialog_not_match),Toast.LENGTH_LONG).show();
                                    }
                                }
                                dialogInterface.dismiss();

                            }
                        })
                .setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void exit(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cafeteriaJSON = sharedPreferences.getString("cafeteria","");
        if (!cafeteriaJSON.equals("")){
            editor.remove("cafeteria");
            editor.apply();
        }
        finish();
        Intent chooseCategory = new Intent(this,ChooseCafeteriaActivity.class);
        startActivity(chooseCategory);
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
