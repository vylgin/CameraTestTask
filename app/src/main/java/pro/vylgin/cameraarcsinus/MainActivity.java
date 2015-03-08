package pro.vylgin.cameraarcsinus;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import pro.vylgin.cameraarcsinus.fragment.RecordFragment;


public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

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
        if (item.getItemId() == R.id.recordItem) {
            showRecordFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
