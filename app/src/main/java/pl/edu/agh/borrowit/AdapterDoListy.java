package pl.edu.agh.borrowit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by lukasz on 25.11.15.
 */
public class AdapterDoListy extends RecyclerView.Adapter<AdapterDoListy.VH> {

    private Context context;
    private List<Model> dataSet = new ArrayList<>();

    public AdapterDoListy(Context context) {
        this.context = context;
        refreshAll();
    }

    public void updateWith(List<Model> models) {
        dataSet.clear();
        refreshWith(models);
    }
    private void refreshWith(List<Model> data){
        dataSet.addAll(data);
        notifyItemRangeChanged(0, getItemCount());
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH vh, int position) {
        final Model model = dataSet.get(position);
        final Bitmap pencilImage = BitmapFactory.decodeFile(model.getImageFilePath());
        final Bitmap kasztan = BitmapFactory.decodeFile(model.getBorrowerFilePath());
        vh.borrower.setImageBitmap(kasztan);
        vh.pencil.setImageBitmap(pencilImage);
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Realm realm = Realm.getInstance(context);
                realm.beginTransaction();
                RealmResults<Model> toDelete = realm.where(Model.class)
                        .equalTo("primaryKey",model.getPrimaryKey()).findAll();
                toDelete.clear();
                realm.commitTransaction();
                refreshAll();
                return false;
            }
        });
    }

    private void refreshAll() {
        dataSet.clear();
        Realm realm = Realm.getInstance(context);
        RealmResults<Model> results = realm.where(Model.class).findAll();
        refreshWith(results);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        private ImageView pencil, borrower;

        public VH(View itemView) {
            super(itemView);
            pencil = (ImageView) itemView.findViewById(R.id.pencil);
            borrower = (ImageView) itemView.findViewById(R.id.borrower);
        }
    }
}
