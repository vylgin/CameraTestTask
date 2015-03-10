package pro.vylgin.cameraarcsinus.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import pro.vylgin.cameraarcsinus.R;
import pro.vylgin.cameraarcsinus.fragment.MediaListFragment;
import pro.vylgin.cameraarcsinus.fragment.RecordFragment;
import pro.vylgin.cameraarcsinus.utils.Utils;


public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String RECORD_FRAGMENT_TAG = "RECORD_FRAGMENT_TAG";
    private static final int RECORD_AUDIO = 1;
    private MediaListFragment mediaListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getFragmentManager().addOnBackStackChangedListener(this);
        showMediaListFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();

        showMediaListFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recordItem:
                showRecordFragment();
                return true;
            case R.id.dictaphoneItem:
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, RECORD_AUDIO);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RECORD_AUDIO) {
                Uri audioUri = data.getData();
                File audioFileFrom = new File(Utils.getRealPathFromURI(this, audioUri));
                File audioFileTo = Utils.getOutputMediaFile(Utils.MediaType.AUDIO);
                audioFileFrom.renameTo(audioFileTo);

                showMediaListFragment();
                updateMediaFragment();
            }
        }
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
        updateMediaFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void shouldDisplayHomeUp() {
        boolean canback = getFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    private void showRecordFragment() {
        Fragment fragmentInContentFrameLayout = getFragmentManager().findFragmentById(R.id.contentFrameLayout);
        RecordFragment recordFragment;

        if (fragmentInContentFrameLayout instanceof RecordFragment) {
            recordFragment = (RecordFragment) fragmentInContentFrameLayout;
            getFragmentManager()
                    .beginTransaction()
                    .attach(recordFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            recordFragment = RecordFragment.newInstance();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentFrameLayout, recordFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    private void showMediaListFragment() {
        MediaListFragment listFragment = (MediaListFragment) getFragmentManager().findFragmentByTag(RECORD_FRAGMENT_TAG);
        if (listFragment != null && listFragment.isVisible()) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentFrameLayout, listFragment)
                    .commit();
            this.mediaListFragment = listFragment;
            updateMediaFragment();
        } else {
            Fragment fragmentInContentFrameLayout = getFragmentManager().findFragmentById(R.id.contentFrameLayout);
            if (fragmentInContentFrameLayout instanceof RecordFragment) {
                RecordFragment recordFragment = (RecordFragment) fragmentInContentFrameLayout;
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentFrameLayout, recordFragment)
                        .commit();
            } else {
                listFragment = MediaListFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentFrameLayout, listFragment)
                        .commit();
                this.mediaListFragment = listFragment;
            }
        }

    }

    private void updateMediaFragment() {
        mediaListFragment.updateMediaList();
    }
}
