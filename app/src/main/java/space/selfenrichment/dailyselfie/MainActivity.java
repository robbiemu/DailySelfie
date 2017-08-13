package space.selfenrichment.dailyselfie;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import space.selfenrichment.dailyselfie.lib.ImagesFS;

import static space.selfenrichment.dailyselfie.FullImageFragment.FULLIMAGE_FRAGMENT;
import static space.selfenrichment.dailyselfie.GridViewFragment.GRIDVIEW_FRAGMENT;
import static space.selfenrichment.dailyselfie.lib.Defs.*;

public class MainActivity extends AppCompatActivity
        implements GridViewFragment.OnFragmentInteractionListener,
        FullImageFragment.OnFragmentInteractionListener {
    public static final Byte COMMAND_NAVTO = 0;
    private static final int TAKE_PICTURE = 1;
    private static final String URI_STRING = "URI_STRING";
    private static final String CURRENT_FRAGMENT = "CURRENT_FRAGMENT";
    private static final String CURRENT_FRAGMENT_TAG = "CURRENT_FRAGMENT_TAG";
    private static final String PREVIOUS_FRAGMENT_TAG = "PREVIOUS_FRAGMENT_TAG";

    private GridViewFragment mGridViewFragment;
    private ImagesAdapter mImageAdapter;
    private Uri mCameraImageUri;
    private Fragment mCurrentFragment;
    private String mCurrentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ImagesFS.init();

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            String fragment_tag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            mCurrentFragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, CURRENT_FRAGMENT);
            if(fragment_tag == GRIDVIEW_FRAGMENT) {
                mGridViewFragment = (GridViewFragment) mCurrentFragment;
                mCurrentFragmentTag = GRIDVIEW_FRAGMENT;
            } else if (fragment_tag == FULLIMAGE_FRAGMENT) {
                //mFullImageFragment = (FullImageFragment) mCurrentFragment;
                mCurrentFragmentTag = FULLIMAGE_FRAGMENT;
            } else {
                //error
            }

            if(mCurrentFragment.isAdded()) {
                return; //or return false/true, based on where you are calling from
            }

            FragmentManager fm =  getSupportFragmentManager();
            fm.beginTransaction()
                    .add(R.id.imageFragmentsMountPoint, mCurrentFragment)
                    .commit();
        } else {
            mGridViewFragment = space.selfenrichment.dailyselfie.GridViewFragment.newInstance(null);
            mCurrentFragment = mGridViewFragment;
            mCurrentFragmentTag = GRIDVIEW_FRAGMENT;

            FragmentManager fm =  getSupportFragmentManager();
            fm.beginTransaction()
                    .add(R.id.imageFragmentsMountPoint, mGridViewFragment)
                    .commit();

            /* this alarm could be associated with a setting to be enabled/disabled. */
            ComponentName receiver = new ComponentName(this, Alarm.class);
            PackageManager pm = getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            Alarm.scheduleRepeatingElapsedNotification(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_FRAGMENT_TAG, mCurrentFragmentTag);

        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, mCurrentFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_camera:
                intendCameraAction();
                return true;
            case R.id.menu_item_delete:
                ImagesFS.freeAll();
                mGridViewFragment.clear();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* get the shot for the user, save to file */
    private void intendCameraAction() {
        Log.i(TAG, "intendCameraAction");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mCameraImageUri = ImagesFS.prepareFileForPhoto();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);

        // note this doesn't always work, and as of 2017 there is no official solution as there really is not a default android camera app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        } else {
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }

        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);

//        Log.i(TAG, "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);

        switch (requestCode) {
            case TAKE_PICTURE:
                String filename = mCameraImageUri.toString();
                if(resultCode == Activity.RESULT_OK) {
                    mGridViewFragment.addFile(filename);
                } else {
                    ImagesFS.freeUp(filename);
                }
            default:
                return;
        }
    }
    @Override
    public void onFragmentInteraction(Byte command, String fragment_identifier, String filename) {
        if(command == COMMAND_NAVTO) {
            loadFragment(fragment_identifier, filename);
        }
    }

    private void loadFragment(String fragment_identifier, String filename) {
        if (fragment_identifier == GRIDVIEW_FRAGMENT) {
            mCurrentFragment = GridViewFragment.newInstance(filename);
            mCurrentFragmentTag = GRIDVIEW_FRAGMENT;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.imageFragmentsMountPoint, mCurrentFragment)
                    .addToBackStack(PREVIOUS_FRAGMENT_TAG)
                    .commit();
        } else if (fragment_identifier == FULLIMAGE_FRAGMENT) {
            mCurrentFragment = FullImageFragment.newInstance(filename);
            mCurrentFragmentTag = FULLIMAGE_FRAGMENT;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.imageFragmentsMountPoint, mCurrentFragment)
                    .addToBackStack(PREVIOUS_FRAGMENT_TAG)
                    .commit();
        }
    }
}
