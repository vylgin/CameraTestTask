package pro.vylgin.cameraarcsinus.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import pro.vylgin.cameraarcsinus.R;
import pro.vylgin.cameraarcsinus.model.MediaContent;
import pro.vylgin.cameraarcsinus.utils.Utils;


public class MediaListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private AbsListView listView;
    private ListAdapter adapter;

    public static MediaListFragment newInstance() {
        MediaListFragment fragment = new MediaListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public MediaListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        updateMediaList();
    }

    @Override
    public void onResume() {
        super.onResume();

//        updateMediaList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mediaitem, container, false);

        listView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) listView).setAdapter(adapter);

        listView.setOnItemClickListener(this);

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = listView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


    public void updateMediaList() {
        Utils.updateMediaContent();
        if (isAdded()) {
            adapter = new ArrayAdapter<MediaContent.MediaItem>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, MediaContent.ITEMS);
        }
    }
}
