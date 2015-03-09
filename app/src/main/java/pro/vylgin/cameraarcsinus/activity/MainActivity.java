package pro.vylgin.cameraarcsinus.activity;

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
import pro.vylgin.cameraarcsinus.fragment.RecordFragment;
import pro.vylgin.cameraarcsinus.utils.Utils;


public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().addOnBackStackChangedListener(this);
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
            }
        }
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
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

    public void shouldDisplayHomeUp(){
        boolean canback = getFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    private void showRecordFragment() {
        RecordFragment recordFragment = (RecordFragment) getFragmentManager().findFragmentById(R.id.contentFrameLayout);
        if (recordFragment == null) {
            recordFragment = RecordFragment.newInstance();
        }
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrameLayout, recordFragment)
                .addToBackStack(null)
                .commit();
    }
}
