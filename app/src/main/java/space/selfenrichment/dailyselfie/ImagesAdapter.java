package space.selfenrichment.dailyselfie;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import space.selfenrichment.dailyselfie.lib.ImagesFS;

import static space.selfenrichment.dailyselfie.lib.Defs.*;

/**
 * Created by roberto on 12/08/2017.
 */

public class ImagesAdapter extends ArrayAdapter<String>{
    private Context context;
    private LayoutInflater inflater;
    private int layoutResourceId;
    private List<String> data;
    private View.OnClickListener mOnClickListener;

    public ImagesAdapter(Context context, View.OnClickListener ocl, int layoutResourceId, List<String> data) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;

        this.context = context;
        this.mOnClickListener = ocl;

        this.inflater = ((MainActivity) context).getLayoutInflater();
        this.data = data;
    }

    @Override
    public void add(@Nullable String filename) {
        super.add(filename);

        data.add(filename);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public void clear() {
        super.clear();

        data.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Log.i(TAG, "getView for position:" + position);

        String filename = data.get(position);

        // inflate the layout
        view = inflater.inflate(R.layout.grid_view_item, null);
        view.setTag(R.id.tag_filename, filename);

        view.setOnClickListener(mOnClickListener);

        ImageView image = view.findViewById(R.id.gridViewItem_image);

        ImagesFS.loadImageToView(context, image, filename);

        TextView filenameLabel = view.findViewById(R.id.gridViewItem_filename);
        filenameLabel.setText(filename.toString());

        return view;
    }
}
